package br.edu.ufcg.leda.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import br.edu.ufcg.leda.commons.user.Student;

public class Util {
	public static final String AUTOR_MATRICULA = "autor.matricula";
	public static final String AUTOR_NOME = "autor.nome";
	
	public static String getCurrentSemester(String url) throws ClientProtocolException, IOException, URISyntaxException{
		String currentSemester = null;
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
	    
		try {
			ClassicHttpRequest httpGet = 
				ClassicRequestBuilder.get(url).build();
			
			HttpClientResponseHandler<List<String>> handler = response -> {
				List<String> singleList = new ArrayList<String>();
				StringBuilder content = new StringBuilder();
				HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStreamReader isr = new InputStreamReader(
								entity.getContent());
								BufferedReader br = new BufferedReader(isr);
								String line = "";
								while ((line = br.readLine()) != null) {
									content.append(line);
								}
								System.out.println("response: " + content.toString());
								Gson gson = new Gson();
								singleList = gson.fromJson(content.toString(), new TypeToken<List<String>>(){}.getType());	        
								//currentSemester = singleList.get(0);
								EntityUtils.consume(entity);
            } else {
							content.append("Server response with no content");
						}
            if(response.getCode() != 200){
							throw new IOException(content.toString());
						} else {
							return singleList;
						}
        };
				
	    currentSemester = client.execute(httpGet, handler).get(0);
			
		} finally {
	        client.close();
	  }
		return currentSemester;
	}

	public static Map<Integer, List<Student>> getAllStudents(String semestre, String url) throws ClientProtocolException, IOException, URISyntaxException{
		Map<Integer, List<Student>> students = new HashMap<Integer, List<Student>>();
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
	    try {
	      ClassicHttpRequest httpGet = 
					ClassicRequestBuilder.get(url).addParameter("semester",semestre).build();

				HttpClientResponseHandler<Map<Integer, List<Student>>> handler = response -> {
				Map<Integer, List<Student>> returnedMap = new HashMap<Integer, List<Student>>();
				StringBuilder content = new StringBuilder();
				HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStreamReader isr = new InputStreamReader(
								entity.getContent());
								BufferedReader br = new BufferedReader(isr);
								String line = "";
								while ((line = br.readLine()) != null) {
									content.append(line);
								}
								System.out.println("response: " + content.toString());
								Gson gson = new Gson();
								returnedMap = gson.fromJson(content.toString(), new TypeToken<Map<Integer, List<Student>>>(){}.getType());	     
								//currentSemester = singleList.get(0);
								EntityUtils.consume(entity);
            } else {
							content.append("Server response with no content");
						}
            if(response.getCode() != 200){
							throw new IOException(content.toString());
						} else {
							return returnedMap;
						}
        };
				students = client.execute(httpGet,handler);
				      
	    } finally {
	        client.close();
	    }
		return students;
	}
	
	
}
