package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
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

import com.google.gson.Gson;

public class StudentSubmissionSender extends Sender {

	String matricula;
	String semestre;
	String turma;
	Map<String,String> filesOwners;

	public StudentSubmissionSender(File arquivo, String matricula,
			String semestre, String roteiro, String url, Map<String,String> files) {
		super(arquivo, roteiro, url);
		this.matricula = matricula;
		this.semestre = semestre;
		// RXX-XX onde os ultimos XX sao a turma
		this.turma = roteiro.substring(4);
		this.filesOwners = files;
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
		StringBody rot = new StringBody(roteiro, ContentType.TEXT_PLAIN);
		//TODO tem que ver que IP está pegando para nao pegar o localhost
		StringBody ip = new StringBody(Inet4Address.getLocalHost()
				.getHostAddress(), ContentType.TEXT_PLAIN);

		/** AQUI PODE PRECISAR DE ALGUMA LOGICA PARA ELIMINAR O LOOPBACK 127.0.0.1
		 * PORQUE EM ALGUNS CASOS ELE EH RETORNADO.
		 * Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
                for (; n.hasMoreElements();)
                {
                        NetworkInterface e = n.nextElement();
                        System.out.println("Interface: " + e.getName());
                        Enumeration<InetAddress> a = e.getInetAddresses();
                        for (; a.hasMoreElements();)
                        {
                                InetAddress addr = a.nextElement();
                                System.out.println("  " + addr.getHostAddress());
                        }
                }
		 */
		//Gson gson = new Gson();
		//StringBody files = new StringBody(gson.toJson(filesOwners), ContentType.TEXT_PLAIN);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		StringBuilder confirmation = new StringBuilder();
		try {
			HttpPost httppost = new HttpPost(url);
			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addPart("arquivo", arq).addPart("matricula", mat)
					.addPart("semestre", sem).addPart("turma", t)
					.addPart("roteiro", rot).addPart("ip", ip).build();
					//.addPart("filesOwners",files).build();

			httppost.setEntity(reqEntity);
			System.out.println("Sending file: " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);

			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					// System.out.println("Response content length: " +
					// resEntity.getContentLength());
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
				response.close();
				writeTicket(this.matricula + "-send.log",
						confirmation.toString());
				System.out.println("--- RESPOSTA RETORNADA DO SERVIDOR");
				System.out.println(confirmation.toString());
				System.out.println("----------------------------------");
				
			}catch(IOException ex) {
				response.close();
			}finally {
				response.close();
			} 
		} finally {
			httpclient.close();
		}
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	public String getTurma() {
		return turma;
	}

	public void setTurma(String turma) {
		this.turma = turma;
	}

	public Map<String, String> getFilesOwners() {
		return filesOwners;
	}

	public void setFilesOwners(Map<String, String> filesOwners) {
		this.filesOwners = filesOwners;
	}
	

}
