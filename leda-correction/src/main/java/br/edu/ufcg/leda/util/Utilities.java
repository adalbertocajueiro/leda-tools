package br.edu.ufcg.leda.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import br.edu.ufcg.leda.commons.correctionReport.CorrectionReport;


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
		Gson gson = new Gson();

		FileWriter fw = new FileWriter(jsonFile);
		gson.toJson(testReport, fw);
		fw.flush();
		fw.close();
	}
	
	public static void writeCorrectionReportToJson(CorrectionReport correctionReport, File jsonFile) throws IOException {
		Gson gson = new Gson();
		FileWriter fw = new FileWriter(jsonFile);
		gson.toJson(correctionReport, fw);
		fw.flush();
		fw.close();
	}
	
	public static TestReport loadTestReportFromJson(File jsonFile) throws IOException{
		Gson gson = new Gson();

		FileReader fr = new FileReader(jsonFile);
		TestReport result = gson.fromJson(fr, new TypeToken<TestReport>(){}.getType());
		return result;
	}
	
	public static CorrectionReport loadCorrectionReportFromJson(File jsonFile) throws IOException{
		Gson gson = new Gson();
        FileReader fr = new FileReader(jsonFile);
		CorrectionReport result = gson.fromJson(fr, new TypeToken<CorrectionReport>(){}.getType());
		return result;
	}
	
}
