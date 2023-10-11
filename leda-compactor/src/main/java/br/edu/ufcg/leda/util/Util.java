package br.edu.ufcg.leda.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

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
	        HttpGet httpGet = new HttpGet(url);
			URI uri = new URIBuilder(httpGet.getURI())
   					.build();
			httpGet.setURI(uri);

	        CloseableHttpResponse response = client.execute(httpGet);

	        System.out.println("Response Code : "
	                        + response.getStatusLine().getStatusCode());

	        BufferedReader rd = new BufferedReader(
	        	new InputStreamReader(response.getEntity().getContent()));

	        StringBuffer result = new StringBuffer();
	        String line = "";
	        while ((line = rd.readLine()) != null) {
	        	result.append(line);
	        }
	        
	        Gson gson = new Gson();
			List<String> singleList = gson.fromJson(result.toString(), new TypeToken<List<String>>(){}.getType());	        
			currentSemester = singleList.get(0);
		} finally {
	        client.close();
	    }
		return currentSemester;
	}

	public static Map<Integer, List<Student>> getAllStudents(String semestre, String url) throws ClientProtocolException, IOException, URISyntaxException{
		Map<Integer, List<Student>> students = new HashMap<Integer, List<Student>>();
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
	    try {
	        HttpGet httpGet = new HttpGet(url);
			URI uri = new URIBuilder(httpGet.getURI())
					.addParameter("semester", semestre)
   					.build();
			httpGet.setURI(uri);

	        CloseableHttpResponse response = client.execute(httpGet);

	        System.out.println("Response Code : "
	                        + response.getStatusLine().getStatusCode());

	        BufferedReader rd = new BufferedReader(
	        	new InputStreamReader(response.getEntity().getContent()));

	        StringBuffer result = new StringBuffer();
	        String line = "";
	        while ((line = rd.readLine()) != null) {
	        	result.append(line);
	        }
	        
	        Gson gson = new Gson();
			students = gson.fromJson(result.toString(), new TypeToken<Map<Integer, List<Student>>>(){}.getType());	        
	    } finally {
	        client.close();
	    }
		return students;
	}
	
	
}
