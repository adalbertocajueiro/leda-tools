package br.edu.ufcg.leda.sender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorSender extends Sender {

	private File arquivoCorrecao;
	private File guiaCorrecaoFile;
	private String semestre;
	private String turma;
	private String userName;

	public ProfessorSender(File ambiente, File arquivoCorrecao, String roteiro,
			String url, String semestre, File guiaCorrecaoFile, String userName) {
		super(ambiente, roteiro, url);
		this.arquivoCorrecao = arquivoCorrecao;
		this.semestre = semestre;
		this.userName = userName;
		this.guiaCorrecaoFile = guiaCorrecaoFile;
		// RXX-XX onde os ultimos XX sao a turma
		this.turma = roteiro.substring(4);
	}

	@Override
	public void send() throws ClientProtocolException, IOException {
		// provas tambem sao empacotadas so mesmo jeito e o id delas eh P0X-0X.
		// entretnato a URL é diferente e o servidor nao preicsa se preocupar
		// com essa diferenca
		FileBody arq = new FileBody(arquivo, ContentType.MULTIPART_FORM_DATA);
		FileBody corrArq = new FileBody(arquivoCorrecao,
				ContentType.MULTIPART_FORM_DATA);
		StringBody rot = new StringBody(id, ContentType.TEXT_PLAIN);
		StringBody sem = new StringBody(semestre, ContentType.TEXT_PLAIN);
		FileBody guia = new FileBody(guiaCorrecaoFile, ContentType.MULTIPART_FORM_DATA);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		//HttpClients.createDefault();

		
		try {
			HttpPost httppost = new HttpPost(url);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create()
					.addPart("envFile", arq)
					.addPart("id", rot)
					.addPart("semester", sem)
					.addPart("corrProjFile", corrArq);
					if(guiaCorrecaoFile.exists()){
						builder.addPart("guiaCorrFile", guia);
					}

			HttpEntity reqEntity =  builder.build();
			
			httppost.addHeader("loggedUser", "{\"name\" = \""+ this.userName + "\"}");
			/*
			 * @RequestHeader Map<String, String> headers
			 * 
			 * @RequestParam String semester,
			 * 
			 * @RequestParam String id,
			 * 
			 * @RequestParam MultipartFile envFile,
			 * 
			 * @RequestParam MultipartFile corrProjFile
			 */
			httppost.setEntity(reqEntity);
			System.out.println("Sending environment file: "
					+ httppost.getEntity());
			
			HttpClientResponseHandler<String> handler = response -> {
				StringBuilder content = new StringBuilder();
				HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStreamReader isr = new InputStreamReader(
								entity.getContent());
								BufferedReader br = new BufferedReader(isr);
								String line = "";
								while ((line = br.readLine()) != null) {
									content.append(line);
									content.append("\n");
								}
								EntityUtils.consume(entity);
            } else {
							content.append("Server response with no content");
						}
            if(response.getCode() != 200){
							throw new IOException(content.toString());
						} else {
							return content.toString();
						}
        };
			String confirmation = httpclient.execute(httppost,handler);

			try {
				System.out.println("----------------------------------------");
				writeTicket(this.id + "-send.log", confirmation.toString());
			} catch(Exception e){
				e.printStackTrace();
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			httpclient.close();
		}

	}

	@Override
	public String toString() {
		return "Ambiente: " + this.getArquivo().getAbsolutePath() + "\n"
				+ "Correcao: " + this.getArquivoCorrecao().getAbsolutePath()
				+ "\n" + "Roteiro: " + this.getId() + "\n" + "URL: "
				+ this.url + "\n" + "Semestre: " + this.semestre + "\n";
	}

}
