package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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

public class Sender {

	private File arquivo;
	private String matricula;
	private String semestre;
	private String turma;
	private String roteiro;
	private String url;
	
	
	public Sender(File arquivo, String matricula, String semestre,
			String turma, String roteiro, String url) {
		super();
		this.arquivo = arquivo;
		this.matricula = matricula;
		this.semestre = semestre;
		this.turma = turma;
		this.roteiro = roteiro;
		this.url = url;
	}

	public void send() throws ClientProtocolException, IOException{

		FileBody arq = new FileBody(arquivo,ContentType.MULTIPART_FORM_DATA);
	    StringBody mat = new StringBody(matricula,ContentType.TEXT_PLAIN);
	    StringBody sem = new StringBody(semestre,ContentType.TEXT_PLAIN);
	    StringBody t = new StringBody(turma,ContentType.TEXT_PLAIN);
	    StringBody rot = new StringBody(roteiro,ContentType.TEXT_PLAIN);
	    
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    StringBuilder confirmation = new StringBuilder();
	    try {
	        HttpPost httppost = new HttpPost(url);
	        HttpEntity reqEntity = MultipartEntityBuilder.create()
	                .addPart("arquivo", arq)
	                .addPart("matricula", mat)
	                .addPart("semestre", sem)
	                .addPart("turma", t)
	                .addPart("roteiro", rot)
	                .build();

	        httppost.setEntity(reqEntity);
	        System.out.println("Sending file: " + httppost.getRequestLine());
	        CloseableHttpResponse response = httpclient.execute(httppost);
	        
	        try {
	            System.out.println("----------------------------------------");
	            System.out.println(response.getStatusLine());
	            HttpEntity resEntity = response.getEntity();
	            if (resEntity != null) {
	                //System.out.println("Response content length: " + resEntity.getContentLength());
	                InputStreamReader isr = new InputStreamReader(resEntity.getContent());
	                BufferedReader br =  new BufferedReader(isr);
	                String line = "";
	                while( (line = br.readLine()) != null){
	                	//System.out.println(line);
	                	confirmation.append(line);
	                	confirmation.append("\n");
	                }
	            }
	            EntityUtils.consume(resEntity);
	            writeTicket(confirmation.toString());
	        } finally {
	            response.close();
	        }
	    } finally {
	        httpclient.close();
	    }
	}
	
	private void writeTicket(String content) throws IOException{
		String fileName = this.matricula + "-send.log";
		File file = new File(fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		FileWriter fr = new FileWriter(file);
		fr.write(content);
		fr.close();
	}

	public File getArquivo() {
		return arquivo;
	}

	public void setArquivo(File arquivo) {
		this.arquivo = arquivo;
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

	public String getRoteiro() {
		return roteiro;
	}

	public void setRoteiro(String roteiro) {
		this.roteiro = roteiro;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
}
