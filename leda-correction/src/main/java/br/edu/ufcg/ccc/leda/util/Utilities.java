package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;



public class Utilities {
	public static final String SUBMISSION_SERVER_URL = "submission.server.url";
	public static final String PROPERTIES_FILE = "/app.properties";
	
	public static Properties loadProperties() throws IOException{
		Properties p = new Properties();
		InputStream is = Utilities.class.getResourceAsStream(PROPERTIES_FILE);
		
		p.load(is);
		
		return p;
	}
	
	public static void writeTestReportToJson(TestReport testReport, File jsonFile) throws IOException {
		//GsonBuilder builder = new GsonBuilder();
		//builder.registerTypeAdapter(TestReport.class, new TestReportAdapter());
		//Gson gson = builder.create();
		
		FileWriter fw = new FileWriter(jsonFile);
		//System.out.println("WRITING TEST REPORT TO JSON GSON WITH ADAPTER");
		//System.out.println("JSON WITH GSON: " + gson.toJson(testReport));

		ObjectMapper  objectMapper = new ObjectMapper();
        // Convert the object to JSON string
        String jsonString = objectMapper.writeValueAsString(testReport);
		//System.out.println("JSON WITH JACKSON: " + jsonString);
		//System.out.println("WRITTEN");
		// gson.toJson(testReport, fw);
		fw.write(jsonString);
		fw.flush();
		fw.close();
	}
	
	public static void writeCorrectionReportToJson(CorrectionReport correctionReport, File jsonFile) throws IOException {
		//Gson gson = new Gson();
		FileWriter fw = new FileWriter(jsonFile);
		ObjectMapper  objectMapper = new ObjectMapper();
        // Convert the object to JSON string
        String jsonString = objectMapper.writeValueAsString(correctionReport);
		fw.write(jsonString);
		//gson.toJson(correctionReport, fw);
		fw.flush();
		fw.close();
	}
	
	public static TestReport loadTestReportFromJson(File jsonFile) throws IOException{
		//Gson gson = new Gson();

		//FileReader fr = new FileReader(jsonFile);
		ObjectMapper  objectMapper = new ObjectMapper();
		TestReport result = objectMapper.readValue(jsonFile, TestReport.class);
		//TestReport result = gson.fromJson(fr, new TypeToken<TestReport>(){}.getType());
		return result;
	}
	
	public static CorrectionReport loadCorrectionReportFromJson(File jsonFile) throws IOException{
		//Gson gson = new Gson();
        //FileReader fr = new FileReader(jsonFile);
		ObjectMapper  objectMapper = new ObjectMapper();
		CorrectionReport result = objectMapper.readValue(jsonFile, CorrectionReport.class);

		//CorrectionReport result = gson.fromJson(fr, new TypeToken<CorrectionReport>(){}.getType());
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		Properties prop = Utilities.loadProperties();
		String url = prop.getProperty(Utilities.SUBMISSION_SERVER_URL);
		System.out.println("Obtaining student lists from server " + url);
		String urlAllStudents = url + "/alunosJson";
		Map<String,Student> alunos = Util.getAllStudents(urlAllStudents);
		int i = 0;
	}
}
