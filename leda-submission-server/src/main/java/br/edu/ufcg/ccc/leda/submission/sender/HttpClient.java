package br.edu.ufcg.ccc.leda.submission.sender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.api.client.http.HttpResponse;

public class HttpClient {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClientBuilder.create().build();
	    try {
	        HttpGet request = new HttpGet("http://localhost:8888/alunosJson");
	        CloseableHttpResponse response = client.execute(request);

	        System.out.println("Response Code : "
	                        + response.getStatusLine().getStatusCode());

	        BufferedReader rd = new BufferedReader(
	        	new InputStreamReader(response.getEntity().getContent()));

	        StringBuffer result = new StringBuffer();
	        String line = "";
	        while ((line = rd.readLine()) != null) {
	        	result.append(line);
	        }
	        
	        
	    } finally {
	        client.close();
	    }
	}
	

}
