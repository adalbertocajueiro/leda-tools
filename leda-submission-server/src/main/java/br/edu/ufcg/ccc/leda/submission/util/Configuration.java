package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import jxl.read.biff.BiffException;

public class Configuration {

	private Map<String, Student> students;
	private Map<String,Roteiro> roteiros;
	private Map<String,Prova> provas;
	private CorrectionManager correctionManager;
	
	private static Configuration instance;
	
	private Configuration() throws ConfigurationException, IOException {
		try {
			students = FileUtilities.loadStudentLists();
			roteiros = FileUtilities.loadRoteiros();
			provas = FileUtilities.loadProvas();
			File roteirosFolder = new File(new File(FileUtilities.UPLOAD_FOLDER),FileUtilities.CURRENT_SEMESTER);
			correctionManager = new CorrectionManager(roteirosFolder, this);
		} catch (BiffException e) {
			throw new ConfigurationException(e);
		}
	}
	public static Configuration getInstance() throws ConfigurationException, IOException {
		if(instance == null){
			instance = new Configuration();
		}
		return instance;
	}
	
	public void reloadStudents() throws Exception{
		students = FileUtilities.loadStudentLists();
	}
	public Map<String, Student> getStudents() {
		return students;
	}
	public void reloadRoteiros() throws Exception{
		roteiros = FileUtilities.loadRoteiros();
	}
	public void reloadProvas() throws Exception{
		provas = FileUtilities.loadProvas();
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
	
}
