package br.edu.ufcg.leda.sender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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

		CloseableHttpClient httpclient = HttpClients.createDefault();

		StringBuilder confirmation = new StringBuilder();
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
					+ httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);

			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					InputStreamReader isr = new InputStreamReader(
							resEntity.getContent());
					BufferedReader br = new BufferedReader(isr);
					String line = "";
					while ((line = br.readLine()) != null) {
						// System.out.println(line);
						confirmation.append(line);
						confirmation.append("\n");
					}
				}
				EntityUtils.consume(resEntity);
				writeTicket(this.id + "-send.log", confirmation.toString());
			} catch(Exception e){
				e.printStackTrace();
			}finally {
				response.close();
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
