package br.edu.ufcg.ccc.leda.submission.sender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

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

public class HttpSender {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
	    try {
	        HttpPost httppost = new HttpPost("http://localhost/submit3");

	        FileBody arquivo = new FileBody(new File("D:\\UFCG\\leda\\leda-roteiros\\Collections - QueueStack\\target\\Roteiro-Collections-QueueStack-environment.zip"));
	        StringBody comment = new StringBody("A binary file of some kind", ContentType.MULTIPART_FORM_DATA);
	        StringBody matricula = new StringBody("113110873",ContentType.TEXT_PLAIN);
	        StringBody semestre = new StringBody("2015.2",ContentType.TEXT_PLAIN);
	        StringBody turma = new StringBody("01",ContentType.TEXT_PLAIN);
	        StringBody roteiro = new StringBody("R05",ContentType.TEXT_PLAIN);
	        HttpEntity reqEntity = MultipartEntityBuilder.create()
	                .addPart("arquivo", arquivo)
	                .addPart("comment", comment)
	                .addPart("matricula", matricula)
	                .addPart("semestre", semestre)
	                .addPart("turma", turma)
	                .addPart("roteiro", roteiro)
	                .build();


	        httppost.setEntity(reqEntity);

	        System.out.println("executing request " + httppost.getRequestLine());
	        CloseableHttpResponse response = httpclient.execute(httppost);
	        try {
	            System.out.println("----------------------------------------");
	            System.out.println(response.getStatusLine());
	            //System.out.println(response.toString());
	            HttpEntity resEntity = response.getEntity();
	            if (resEntity != null) {
	                System.out.println("Response content length: " + resEntity.getContentLength());
	                InputStreamReader isr = new InputStreamReader(resEntity.getContent());
	                BufferedReader br =  new BufferedReader(isr);
	                String line = "";
	                while( (line = br.readLine()) != null){
	                	System.out.println(line);
	                }
	            }
	            EntityUtils.consume(resEntity);
	        } finally {
	            response.close();
	        }
	    } finally {
	        httpclient.close();
	    }
	}
	

}
