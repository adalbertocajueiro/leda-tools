package br.edu.ufcg.ccc.leda.util;

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

public class ProfessorSender extends Sender {

	private File arquivoCorrecao;
	private String semestre;
	private String turma;
	
	public ProfessorSender(File ambiente, File arquivoCorrecao, String roteiro, 
			String url, String semestre) {
		super(ambiente, roteiro, url);
		this.arquivoCorrecao = arquivoCorrecao;
		this.semestre = semestre;
		//RXX-XX onde os ultimos XX sao a turma
		this.turma = roteiro.substring(4);
	}

	@Override
	public void send() throws ClientProtocolException, IOException {
		FileBody arq = new FileBody(arquivo,ContentType.MULTIPART_FORM_DATA);
		FileBody corrArq = new FileBody(arquivoCorrecao,ContentType.MULTIPART_FORM_DATA);
	    StringBody rot = new StringBody(roteiro,ContentType.TEXT_PLAIN);
	    StringBody tur = new StringBody(turma,ContentType.TEXT_PLAIN);
	    StringBody sem = new StringBody(semestre,ContentType.TEXT_PLAIN);
	    
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    StringBuilder confirmation = new StringBuilder();
	    try {
	        HttpPost httppost = new HttpPost(url);
	        HttpEntity reqEntity = MultipartEntityBuilder.create()
	                .addPart("arquivoAmbiente", arq)
	                .addPart("roteiro", rot)
	                .addPart("turma", tur)
	                .addPart("semestre", sem)
	                .addPart("arquivoCorrecao", corrArq)
	                .build();

	        httppost.setEntity(reqEntity);
	        System.out.println("Sending environment file: " + httppost.getRequestLine());
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
	            writeTicket(this.roteiro + "-send.log", confirmation.toString());
	        } finally {
	            response.close();
	        }
	    } finally {
	        httpclient.close();
	    }

	}

}