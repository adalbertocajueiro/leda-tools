package br.edu.ufcg.ccc.leda.util;

import java.io.File;

public class CompactorTest {

	public static void main(String[] args) throws Exception {
		Compactor fu = new Compactor();
		File folder = new File("D:\\trash2\\leda-upload\\2016.1\\PP1-01");
		File destZipFile = new File("D:\\trash2\\leda-upload\\2016.1\\PP1-01.zip");
		fu.zipFolder(folder, destZipFile);
		System.out.println("Compaction finished!!!");
	}
}
