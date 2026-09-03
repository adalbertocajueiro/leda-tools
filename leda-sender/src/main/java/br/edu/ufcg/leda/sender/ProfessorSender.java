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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.edu.ufcg.leda.util.SenderException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfessorSender extends Sender {

	private File arquivoCorrecao;
	private File guiaCorrecaoFile;
	private String semestre;
	private String turma;
	private String email;

	private static final Logger logger = LogManager.getLogger(ProfessorSender.class);

	public ProfessorSender(File ambiente, File arquivoCorrecao, String roteiro,
			String url, String semestre, File guiaCorrecaoFile, String email) {
		super(ambiente, roteiro, url);
		this.arquivoCorrecao = arquivoCorrecao;
		this.semestre = semestre;
		this.email = email;
		this.guiaCorrecaoFile = guiaCorrecaoFile;
		// RXX-XX onde os ultimos XX sao a turma
		this.turma = roteiro.substring(4);
	}

	@Override
	public void send() throws IOException, ClientProtocolException {
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
			HttpPost httppost = new HttpPost(url);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create()
					.addPart("envFile", arq)
					.addPart("id", rot)
					.addPart("semester", sem)
					.addPart("corrProjFile", corrArq);
			if (guiaCorrecaoFile.exists()) {
				builder.addPart("guiaCorrFile", guia);
			}

			HttpEntity reqEntity = builder.build();

			httppost.addHeader("loggeduser", "{\"email\" = \"" + this.email + "\"}");
			httppost.addHeader("semester", this.semestre);
			
			httppost.setEntity(reqEntity);

			HttpClientResponseHandler<String> handler = response -> {
				StringBuilder content = new StringBuilder();

				int statusCode = response.getCode(); // e.g., 400, 404, 500
				String reason = response.getReasonPhrase();
				

				if (statusCode != 200) {
					logger.warn("HTTP STATUS CODE: " + statusCode);

					// 3. Extract the error payload from the body
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						try {
							// Convert the entity stream into a readable String
							String errorBody = EntityUtils.toString(entity);
							throw new SenderException("Server responded with error: " + statusCode + " - " + reason + ". Error body: " + errorBody);
						} catch (IOException e) {
							throw new SenderException("Failed to read error body", e);
						} finally {
							// Always ensure the entity is fully consumed or closed
							try {
								EntityUtils.consume(entity);
							} catch (IOException ignored) {

							}
						}
					} else {
						throw new SenderException("Server response with no content for error status code: " + statusCode);
					}
				} else {
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
						throw new SenderException(
								"Server response with no content for success status code: " + statusCode);
					}
					logger.debug("HTTP STATUS CODE: " + statusCode);
				}
				return content.toString();
			};

			String confirmation = httpclient.execute(httppost, handler);

			try {
				writeTicket(this.id + "-send.log", confirmation.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			httpclient.close();
	}

	@Override
	public String toString() {
		return "Ambiente: " + this.getArquivo().getAbsolutePath() + "\n"
				+ "Correcao: " + this.getArquivoCorrecao().getAbsolutePath()
				+ "\n" + "Roteiro: " + this.getId() + "\n" + "URL: "
				+ this.url + "\n" + "Semestre: " + this.semestre + "\n";
	}

}
