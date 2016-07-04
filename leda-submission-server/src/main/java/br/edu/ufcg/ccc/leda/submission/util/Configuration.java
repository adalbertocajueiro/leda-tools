package br.edu.ufcg.ccc.leda.submission.util;

import java.io.IOException;
import java.util.Map;

public class Configuration {

	private Map<String, Student> students;
	private Map<String,Roteiro> roteiros;

	private static Configuration instance;
	
	private Configuration() throws ConfigurationException, IOException {
		students = FileUtilities.loadStudentLists();
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
	
	
}
