package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Utilities {
	private static final String RESOURCES_FOLDER = "src/main/resources";
	public static final String SUBMISSION_SERVER_URL = "submission.server.url";
	
	public static Properties loadProperties() throws IOException{
		Properties p = new Properties();
		File confFolder = new File(RESOURCES_FOLDER);
		FileReader fr = new FileReader(new File(confFolder,"app.properties"));
		p.load(fr);
		
		return p;
	}
	
	public static void main(String[] args) throws IOException {
		Properties prop = Utilities.loadProperties();
		String url = prop.getProperty(Utilities.SUBMISSION_SERVER_URL);
		System.out.println("Obtaining student lists from server " + url);
	}
}
