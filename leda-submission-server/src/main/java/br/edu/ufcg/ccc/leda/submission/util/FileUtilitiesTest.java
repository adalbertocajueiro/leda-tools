package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;






import jxl.read.biff.BiffException;

public class FileUtilitiesTest {

	public static void main(String[] args) throws Exception {
		FileUtilities fu = new FileUtilities();
		Map<String,Student> map = new HashMap<String,Student>();
		Map<String,Roteiro> roteiros = new HashMap<String,Roteiro>();
 		//fu.loadXLS(xls, map);
		//map = fu.loadStudentsFromExcelFile(xls);
		/*map = FileUtilities.loadStudentLists();
		Set<String> keys = map.keySet();
		for (String key : keys) {
			System.out.println(key + " - " + map.get(key));
		}*/
 		
 		roteiros = FileUtilities.loadRoteiros();
 		Set<String> keysRoteiros = roteiros.keySet();
 		Collection<Roteiro> values = roteiros.values();
 		for (String key : keysRoteiros) {
 			System.out.println("Roteiro: " + roteiros.get(key));
		}
 		/*
 		Student[] sorted = map.values().toArray(new Student[0]);
 		Arrays.sort(sorted);
 		for (int i = 0; i < sorted.length; i++) {
 			System.out.println(sorted[i]);
		}*/
	}
}
