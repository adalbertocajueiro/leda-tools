package br.edu.ufcg.leda.sender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import br.edu.ufcg.leda.util.Util;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentSubmissionSender extends Sender {

	String matricula;
	String semestre;
	String turma;

	public StudentSubmissionSender(File arquivo, String matricula,
			String semestre, String roteiro, String url, Map<String, String> files) {
		super(arquivo, roteiro, url);
		this.matricula = matricula;
		this.semestre = semestre;
		// RXX-XX onde os ultimos XX sao a turma
		this.turma = roteiro.substring(4);
	}

	public StudentSubmissionSender(File arquivo, String matricula,
			String semestre, String roteiro, String url) {
		super(arquivo, roteiro, url);
		this.matricula = matricula;
		this.semestre = semestre;
		// RXX-XX onde os ultimos XX sao a turma
		this.turma = roteiro.substring(4);
	}

	public void send() throws IOException {

		FileBody arq = new FileBody(arquivo, ContentType.MULTIPART_FORM_DATA);
		StringBody mat = new StringBody(matricula, ContentType.TEXT_PLAIN);
		StringBody sem = new StringBody(semestre, ContentType.TEXT_PLAIN);
		StringBody t = new StringBody(turma, ContentType.TEXT_PLAIN);
		StringBody rot = new StringBody(id, ContentType.TEXT_PLAIN);

		StringBody ip = new StringBody(Util.getLocalIP()
				.getHostAddress(), ContentType.TEXT_PLAIN);

		/**
		 * AQUI PODE PRECISAR DE ALGUMA LOGICA PARA ELIMINAR O LOOPBACK 127.0.0.1
		 * PORQUE EM ALGUNS CASOS ELE EH RETORNADO.
		 * Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		 * for (; n.hasMoreElements();)
		 * {
		 * NetworkInterface e = n.nextElement();
		 * System.out.println("Interface: " + e.getName());
		 * Enumeration<InetAddress> a = e.getInetAddresses();
		 * for (; a.hasMoreElements();)
		 * {
		 * InetAddress addr = a.nextElement();
		 * System.out.println(" " + addr.getHostAddress());
		 * }
		 * }
		 */
		// Gson gson = new Gson();
		// StringBody files = new StringBody(gson.toJson(filesOwners),
		// ContentType.TEXT_PLAIN);

		CloseableHttpClient httpclient = HttpClients.createDefault();

		StringBuilder confirmation = new StringBuilder();
		CloseableHttpResponse response = null;
		try {
			HttpPost httppost = new HttpPost(url);
			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addPart("arquivo", arq).addPart("matricula", mat)
					.addPart("semestre", sem).addPart("turma", t)
					.addPart("roteiro", rot).addPart("ip", ip).build();

			httppost.setEntity(reqEntity);
			System.out.println("Sending file: " + httppost.getRequestLine());
			response = httpclient.execute(httppost);

			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				InputStreamReader isr = new InputStreamReader(
						resEntity.getContent());
				BufferedReader br = new BufferedReader(isr);
				String line = "";
				while ((line = br.readLine()) != null) {
					confirmation.append(line);
					confirmation.append("\n");
				}
			}
			EntityUtils.consume(resEntity);
			response.close();
			if (response.getStatusLine().getStatusCode() != 200) {

				throw new IOException(confirmation.toString());

			}
			writeTicket(this.matricula + "-send.log",
					confirmation.toString());
			System.out.println("--- RESPOSTA RETORNADA DO SERVIDOR");
			System.out.println(confirmation.toString());
			System.out.println("----------------------------------");

		} catch (IOException ex) {
			throw ex;
		} finally {
			if (response != null) {
				response.close();
			}
			httpclient.close();
		}
	}
}
