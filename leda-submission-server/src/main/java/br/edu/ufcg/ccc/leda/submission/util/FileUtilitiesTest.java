package br.edu.ufcg.ccc.leda.submission.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileUtilitiesTest {

	public static void main(String[] args) throws Exception {
		Map<String,Student> map = new HashMap<String,Student>();
		Map<String,Roteiro> roteiros = new HashMap<String,Roteiro>();
		Map<String,Prova> provas = new HashMap<String,Prova>();
 		//fu.loadXLS(xls, map);
		//map = fu.loadStudentsFromExcelFile(xls);
		
		/*
		map = FileUtilities.loadStudentLists();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			System.out.println(key + " - " + map.get(key));
		}
		*/
 		
 		provas = FileUtilities.loadProvas();
 		Set<String> keysProvas = provas.keySet();
 		for (String key : keysProvas) {
 			System.out.println("Prova: " + provas.get(key));
		}
 		/*
 		Student[] sorted = map.values().toArray(new Student[0]);
 		Arrays.sort(sorted);
 		for (int i = 0; i < sorted.length; i++) {
 			System.out.println(sorted[i]);
		}
		*/
	}
}
