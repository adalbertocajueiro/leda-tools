package br.edu.ufcg.ccc.leda.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import sorting.Sorting;

public class Utilities {

	public static final String JSON_FILE_NAME = "data.json";
	public static final String HTML_FILE_NAME = "index.html";
	public static final String WEB_FOLDER = "webChart";
	public static final String RESOURCES_WEB_FOLDER = "/web";
	
	public static void callMethod(final Sorting sortImplementation, final Integer[] argument){
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = new Callable<Object>() {
		   public Object call() {
		      sortImplementation.sort(argument);
		      return argument;
		   }
		};
		Future<Object> future = executor.submit(task);
		try {
		   Object result = future.get(5, TimeUnit.SECONDS); 
		} catch (TimeoutException ex) {
		   // handle the timeout
		} catch (InterruptedException e) {
		   // handle the interrupts
		} catch (ExecutionException e) {
		   // handle other exceptions
		} finally {
		   future.cancel(true); // may or may not desire this
		}
	}
	
	public static void fillWebFolder(File webFolder) throws IOException{
		URL file = Utilities.class.getResource(RESOURCES_WEB_FOLDER);
		System.out.println("URL: " + file);
		boolean dir = isDirectory(file);
		System.out.println("IS DIRECTORY: " + dir);
		int i = 0;
	}
	
	public static void main(String[] args) throws IOException {
		File folder = new File("D:\\UFCG\\leda\\leda-tools\\leda-chart\\src\\main\\resources");
		Utilities.fillWebFolder(folder);
	}
	public static boolean isDirectory (URL url) throws IOException {
		   String protocol = url.getProtocol();
		   if (protocol.equals("file")) {
			   System.out.println("IS REAL FILE");
		      return new File(url.getFile()).isDirectory();
		   }
		   if (protocol.equals("jar")) {
			   System.out.println("IS JAR FILE");
		      String file = url.getFile();
		      int bangIndex = file.indexOf('!');
		      String jarPath = file.substring(bangIndex + 2);
		      file = new URL(file.substring(0, bangIndex)).getFile();
		      ZipFile zip = new ZipFile(file);
		      ZipEntry entry = zip.getEntry(jarPath);
		      boolean isDirectory = entry.isDirectory();
		      if (!isDirectory) {
		         InputStream input = zip.getInputStream(entry);
		         isDirectory = input == null;
		         if (input != null) input.close();
		      }
		      return isDirectory;
		   }
		   throw new RuntimeException("Invalid protocol: " + protocol);
	}
	
	/*private String loadHTMLFile(String filePath) throws IOException {
		
		StringBuilder result = new StringBuilder();
		InputStream is = HTMLFileUtility.class.getResourceAsStream(filePath);
		
		InputStreamReader isr = new InputStreamReader(is);
		//System.out.println("Loading HTML file: " + filePath + " stream: " + is);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while((line = br.readLine()) != null){
			result.append(line + "\n");
		}
		
		is.close();
		isr.close();
		br.close();
		
		return result.toString();
	}*/

}
