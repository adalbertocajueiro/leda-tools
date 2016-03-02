package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import sorting.Sorting;

public class Utilities {

	public static final String JSON_FILE_NAME = "data.json";
	
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
