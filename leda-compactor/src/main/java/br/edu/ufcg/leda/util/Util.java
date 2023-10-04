package br.edu.ufcg.leda.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import br.edu.ufcg.leda.commons.user.Student;

public class Util {
	public static final String AUTOR_MATRICULA = "autor.matricula";
	public static final String AUTOR_NOME = "autor.nome";
	
	public static Map<String,String> addAuthorToFiles(List<File> files, String matricula, String nome) throws IOException{
		Map<String,String> filesOwners = new HashMap<String,String>();
		for (File file : files) {
			writeAuthor(file.toPath(), matricula, nome, filesOwners);
		}
		//System.out.println("%%%% Util.addAuthorToFiles"); 
		//filesOwners.forEach((arq,autor) -> System.out.println("%%%%% arquivo: " + arq + " tem autor " + autor));
		return filesOwners;
	}
	public static void writeAuthor(Path file, String matricula, String nome, Map<String,String> filesOwners) throws IOException{

		    UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
		    // The file attribute
		    
		    List<String> attributes = view.list();
		    if(!attributes.contains(AUTOR_MATRICULA)){
			    // Write the properties
			    byte[] bytesMatricula = matricula.getBytes("UTF-8");
			    ByteBuffer writeBuffer = ByteBuffer.allocate(bytesMatricula.length);
			    writeBuffer.put(bytesMatricula);
			    writeBuffer.flip();
			    view.write(AUTOR_MATRICULA, writeBuffer);

			    byte[] bytesNome = nome.getBytes("UTF-8");
			    ByteBuffer writeBufferNome = ByteBuffer.allocate(bytesNome.length);
			    writeBufferNome.put(bytesNome);
			    writeBufferNome.flip();
			    view.write(AUTOR_NOME, writeBufferNome);

			    filesOwners.put(file.toFile().getName(), matricula + "-" + nome);
		    }else{
		    	Student owner = readAuthor(file);
			    filesOwners.put(file.toFile().getName(), owner.getMatricula() + "-" + owner.getNome());		    	
		    }

	}
	
	public static Student readAuthor(Path file) throws IOException{
		Student proprietario = new Student(null,null,0,null);

	    UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
	    
	    String matricula = AUTOR_MATRICULA;
	    ByteBuffer readBuffer = ByteBuffer.allocate(view.size(matricula));
	    view.read(matricula, readBuffer);
	    readBuffer.flip();
	    proprietario.setMatricula(new String(readBuffer.array(), "UTF-8"));

	    String nome = AUTOR_NOME;
	    readBuffer = ByteBuffer.allocate(view.size(nome));
	    view.read(nome, readBuffer);
	    readBuffer.flip();
	    proprietario.setNome(new String(readBuffer.array(), "UTF-8"));
	    
	    return proprietario;
	}
	
	public static List<File> getFiles(File folder, String extension){
		ArrayList<File> files = new ArrayList<File>();
		addFile(folder, files, extension);
		return files; 
	}
	private static void addFile(File folder, List<File> files, String extension){
		if(folder.isDirectory()){
			File[] subFiles = folder.listFiles();
			for (int i = 0; i < subFiles.length; i++) {
				addFile(subFiles[i], files, extension);
			}
		} else if (folder.getName().endsWith(extension)){
			if(!files.contains(folder)){
				files.add(folder);
			}
		} 
	}
	public static Map<Integer, List<Student>> getAllStudents(String url) throws ClientProtocolException, IOException{
		Map<Integer, List<Student>> students = new HashMap<Integer, List<Student>>();
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
	    try {
	        HttpGet request = new HttpGet(url);
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
	        
	        Gson gson = new Gson();
			students = gson.fromJson(result.toString(), new TypeToken<Map<Integer, List<Student>>>(){}.getType());

	        
	    } finally {
	        client.close();
	    }
		return students;
	}
	
	public static double[] getPesos(String url) throws ClientProtocolException, IOException{
		double[] resp = new double[2];
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
	    try {
	        HttpGet request = new HttpGet(url);
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
	        
			@SuppressWarnings("null")
	        String[] pesos = line.split(",");
	        int index = 0;
	        for (String peso : pesos) {
				resp[index++] = Double.parseDouble(peso);
			}
	    } finally {
	        client.close();
	    }
		return resp;
	}

	private static String readLines(File f) throws IOException{
		StringBuilder result = new StringBuilder();
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		while((line = br.readLine()) != null){
			result.append(line + "\n");
		}
		fr.close();
		br.close();
		return result.toString();
	}
	public static void changeEncoding(File javaFile,String encoding){
		OutputStreamWriter osw = null;
		try{
		//Example to write a file into file system
		//Charset windows1252 = Charset.forName("windows-1252");
		String content = Util.readLines(javaFile);
		FileOutputStream fos = new FileOutputStream(javaFile);
		osw = new OutputStreamWriter(fos,encoding);
		osw.write(content);
		osw.close();
		System.out.println("Success");
		fos.close();
		}
		catch(Exception e)
		{
		System.out.println(e.getMessage());
		}
	}
}
