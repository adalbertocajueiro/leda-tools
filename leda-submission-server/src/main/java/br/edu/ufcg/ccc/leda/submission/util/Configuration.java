package br.edu.ufcg.ccc.leda.submission.util;

import java.util.Map;

public class Configuration {

	private Map<String, Student> students;

	private static Configuration instance;
	
	private Configuration() throws Exception{
		students = FileUtilities.loadStudentLists();
	}
	public static Configuration getInstance() throws Exception{
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
