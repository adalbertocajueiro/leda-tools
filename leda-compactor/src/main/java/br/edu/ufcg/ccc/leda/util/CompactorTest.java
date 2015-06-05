package br.edu.ufcg.ccc.leda.util;

import java.io.File;

public class CompactorTest {

	public static void main(String[] args) throws Exception {
		Compactor fu = new Compactor();
		File folder = new File("D:\\tmp\\Rot1\\subs\\Aluno1\\src");
		File destZipFile = new File("D:\\tmp\\aluno1 da silva.zip");
		fu.zipFolder(folder, destZipFile);
		System.out.println("Compaction finished!!!");
	}
}
