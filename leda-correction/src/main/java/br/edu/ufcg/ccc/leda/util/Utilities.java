package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


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
	
	public static void writeTestReportToJson(TestReport testReport, File jsonFile) throws IOException {
		Gson gson = new Gson();

		FileWriter fw = new FileWriter(jsonFile);
		gson.toJson(testReport, fw);
		fw.flush();
		fw.close();
	}
	
	public static TestReport loadTestReportFromJson(File jsonFile) throws IOException{
		Gson gson = new Gson();
		FileReader fr = new FileReader(jsonFile);
		TestReport result = gson.fromJson(fr, new TypeToken<TestReport>(){}.getType());
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		Properties prop = Utilities.loadProperties();
		String url = prop.getProperty(Utilities.SUBMISSION_SERVER_URL);
		System.out.println("Obtaining student lists from server " + url);
		String urlAllStudents = url + "/alunosJson";
		Map<String,Student> alunos = Util.getAllStudents(urlAllStudents);
		
	}
}