package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class Util {
	public static final String AUTOR_MATRICULA = "autor.matricula";
	public static final String AUTOR_NOME = "autor.nome";
	
	public static Map<String,String> addAuthorToFiles(List<File> files, String matricula, String nome) throws IOException{
		Map<String,String> filesOwners = new HashMap<String,String>();
		for (File file : files) {
			writeAuthor(file.toPath(), matricula, nome, filesOwners);
		}
		System.out.println("%%%% Util.addAuthorToFiles"); 
		filesOwners.forEach((arq,autor) -> System.out.println("%%%%% arquivo: " + arq + " tem autor " + autor));
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
	
	public static void buildFileOwnerList(List<File> files){
		
	}
	
	public static Student readAuthor(Path file) throws IOException{
		Student proprietario = new Student(null,null,null);

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
	public static Map<String,Student> getAllStudents(String url) throws ClientProtocolException, IOException{
		Map<String,Student> students = new HashMap<String,Student>();
		
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
			students = gson.fromJson(result.toString(), new TypeToken<Map<String,Student>>(){}.getType());

	        
	    } finally {
	        client.close();
	    }
		return students;
	}
}
