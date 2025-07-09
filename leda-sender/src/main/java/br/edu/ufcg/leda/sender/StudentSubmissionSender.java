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

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

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
			System.out.println("Sending file: " + httppost.getEntity() + " to URL " + url);

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
			System.out.println("----------------------------------------");
			writeTicket(this.matricula + "-send.log", confirmation.toString());
		} catch(Exception e){
			//e.printStackTrace();
			throw e;
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
