package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



import jxl.read.biff.BiffException;

public class FileUtilitiesTest {

	public static void main(String[] args) throws BiffException, IOException {
		FileUtilities fu = new FileUtilities();
		File xls = new File("D:\\UFCG\\2015.2\\disciplinas\\eda\\frequencia_2015.2_1411172-01_094420877.xlsx");
		Map<String,String> map = new HashMap<String,String>();
 		//fu.loadXLS(xls, map);
		map = fu.loadExcelFile(xls);
		Set<String> keys = map.keySet();
 		for (String key : keys) {
			System.out.println(key + " - " + map.get(key));
		}
 		
 		String[] sorted = map.values().toArray(new String[0]);
 		Arrays.sort(sorted);
 		for (int i = 0; i < sorted.length; i++) {
 			System.out.println(sorted[i]);
		}
	}
}
