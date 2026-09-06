package br.edu.ufcg.ledaSender.sender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;

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

import br.edu.ufcg.ledaSender.util.SenderException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentSubmissionSender extends Sender {

	String matricula;
	String semestre;
	String turma;

	private static final Logger logger = LogManager.getLogger(StudentSubmissionSender.class);

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
		StringBody rot = new StringBody(id, ContentType.TEXT_PLAIN);

		InetAddress localIp = getLocalIP();
		StringBody ip = new StringBody(localIp.getHostAddress(), ContentType.TEXT_PLAIN);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		HttpPost httppost = new HttpPost(url);
		HttpEntity reqEntity = MultipartEntityBuilder.create()
				.addPart("submissionFile", arq)
				.addPart("matricula", mat)
				.addPart("semester", sem)
				.addPart("id", rot)
				.addPart("ip", ip)
				.build();

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
						throw new SenderException("Server responded with error: " + statusCode + " - " + reason
								+ ". Error body: " + errorBody);
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
					throw new SenderException(
							"Server response with no content for error status code: " + statusCode);
				}
			} else {
				logger.debug("HTTP STATUS CODE: " + statusCode);
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
				
			}
			return content.toString();
		};
		String confirmation = httpclient.execute(httppost, handler);

		try {
			writeTicket(this.id + "-send.log", confirmation.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpclient.close();
		}

	}

	public InetAddress getLocalIP() throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress result = null;
		Pattern IPADDRESS_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
				"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

		while (interfaces.hasMoreElements()) {
			NetworkInterface current = interfaces.nextElement();
			if (!current.isUp() || current.isLoopback() || current.isVirtual())
				continue;
			Enumeration<InetAddress> addresses = current.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress current_addr = addresses.nextElement();
				if (current.toString().contains("127")) {
					result = current_addr;
				}
				if (current_addr.isLoopbackAddress()) {
					continue;
				} else {
					//TODO revisar est amáscara de IP fixa aqui.
					if (current_addr.toString().contains("150.165")) {
						return current_addr;
					} else {
						if (IPADDRESS_PATTERN.matcher(current_addr.getHostAddress()).matches()) {
							result = current_addr;
						}
					}
				}
			}
		}
		return result;
	}
}
