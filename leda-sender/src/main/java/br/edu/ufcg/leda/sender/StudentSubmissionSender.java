package br.edu.ufcg.leda.sender;

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
		StringBody rot = new StringBody(id, ContentType.TEXT_PLAIN);

		InetAddress localIp = getLocalIP();
		StringBody ip = new StringBody(localIp.getHostAddress(), ContentType.TEXT_PLAIN);

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
					.addPart("submissionFile", arq)
					.addPart("matricula", mat)
					.addPart("semester", sem)
					.addPart("id", rot)
					.addPart("ip", ip)
					.build();

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

	public InetAddress getLocalIP() throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress result = null;
		Pattern IPADDRESS_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
						"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		
		while (interfaces.hasMoreElements()){
		    NetworkInterface current = interfaces.nextElement();
		    //System.out.println(current);
		    if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
		    Enumeration<InetAddress> addresses = current.getInetAddresses();
		    while (addresses.hasMoreElements()){
		        InetAddress current_addr = addresses.nextElement();
		        if(current.toString().contains("127")) {
		        	result = current_addr;		        	
		        }
		        if (current_addr.isLoopbackAddress()) {
		        	continue;
		        }else {
		        	
		        	//System.out.println(current_addr.getHostAddress());
		        	if(current_addr.toString().contains("150.165")) {
		        		return current_addr;
		        	}else {
		        		if(IPADDRESS_PATTERN.matcher(current_addr.getHostAddress()).matches()) {
		        			result = current_addr;
		        		}
		        	}
		        }
		    }
		}
		return result;
	}
}
