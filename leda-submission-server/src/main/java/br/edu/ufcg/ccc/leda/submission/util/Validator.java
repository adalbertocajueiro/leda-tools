package br.edu.ufcg.ccc.leda.submission.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class Validator {

	public static boolean validateStudent(UploadConfiguration config) throws ConfigurationException, IOException {
		boolean result = false;
		
		Map<String,Student> studentMap = Configuration.getInstance().getStudents();
		//System.out.println("STUDENT: " + config.getMatricula());
		//studentMap.keySet().forEach(key -> System.out.println("CADASTRADO: " + key));
		result = studentMap.containsKey(config.getMatricula());
		
		return result;
	}
	

}
