package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;




import jxl.read.biff.BiffException;

public class FileUtilitiesTest {

	public static void main(String[] args) throws BiffException, IOException {
		FileUtilities fu = new FileUtilities();
		File pub = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
		File xls = new File(pub,"frequencia_2015.2_1411179-01_153008308.xlsx");
		Map<String,Student> map = new HashMap<String,Student>();
 		//fu.loadXLS(xls, map);
		map = fu.loadStudentsFromExcelFile(xls);
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
