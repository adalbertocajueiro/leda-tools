package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.google.gdata.util.ServiceException;

import jxl.read.biff.BiffException;

public class Configuration {

	private Map<String, Student> students;
	private Map<String,Roteiro> roteiros;
	private Map<String,Prova> provas;
	private CorrectionManager correctionManager;
	private ArrayList<String> ipsAutorizados = new ArrayList<String>();
	private static String ID_ROTEIROS_SHEET = "19npZPI7Y1jyk1jxNKHgUZkYTk3hMT_vdmHunQS-tOhA";
	private static String ID_PROVAS_SHEET = "1mt0HNYUMgK_tT_P2Lz5PQjBP16F6Hn-UI8P21C0iPmI";
	private static Configuration instance;
	
	private Configuration() throws ConfigurationException, IOException, ServiceException{
		try {
			students = FileUtilities.loadStudentLists();
			//roteiros = FileUtilities.loadRoteiros();
			roteiros = Util.loadSpreadsheetRoteiros(ID_ROTEIROS_SHEET);
			//provas = FileUtilities.loadProvas();
			provas = Util.loadSpreadsheetProvas(ID_PROVAS_SHEET);
			File currentSemesterFolder = new File(new File(FileUtilities.UPLOAD_FOLDER),FileUtilities.CURRENT_SEMESTER);
			correctionManager = new CorrectionManager(currentSemesterFolder, this);
			ipsAutorizados.add("150.165.74");
			ipsAutorizados.add("150.165.54");
		} catch (BiffException e) {
			throw new ConfigurationException(e);
		}
	}
	public static Configuration getInstance() throws ConfigurationException, IOException, ServiceException {
		if(instance == null){
			instance = new Configuration();
		}
		return instance;
	}
	
	
	public void reload() throws Exception{
		instance = new Configuration();
	}
	
	public Map<String, Student> getStudents() {
		return students;
	}
	public Map<String, Roteiro> getRoteiros() {
		return roteiros;
	}
	public CorrectionManager getCorrectionManager() {
		return correctionManager;
	}
	public Map<String, Prova> getProvas() {
		return provas;
	}
	public ArrayList<String> getIpsAutorizados() {
		return ipsAutorizados;
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Roteiros: <br>");
		roteiros.values().stream().sorted((r1,r2)-> r1.getId().compareTo(r2.getId()))
			.forEach(r -> result.append(r.toString() + "<br>"));
		result.append("Provas: <br>");
		provas.values().stream().sorted((p1,p2)-> p1.getProvaId().compareTo(p2.getProvaId()))
			.forEach(p -> result.append(p.toString() + "<br>"));
		
		return result.toString();
	}
	
	
}
