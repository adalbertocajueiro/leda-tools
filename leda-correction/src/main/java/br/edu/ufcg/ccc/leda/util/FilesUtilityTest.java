package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FilesUtilityTest {

	public static void main(String[] args) throws IOException {
		FilesUtility fu = new FilesUtility();
		File studentZipFile = new File("D:\\tmp\\hash\\subs\\Aluno1.zip");
		File envZipFile = new File("D:\\tmp\\hash\\Roteiro-TabelaHash-correction-env-T1.zip");
		File destDirectory = new File("D:\\tmp\\hash\\subs\\Aluno1");
		System.out.println("creating basic structure...");
		fu.unzip(envZipFile, destDirectory);
		/*FileFolder f1 = new FileFolder("Gnomesort.java",new File("sorting\\variationsOfBubblesort"));
		FileFolder f2 = new FileFolder("Combsort.java",new File("sorting\\variationsOfBubblesort"));
		ArrayList<FileFolder> al = new ArrayList<FileFolder>();
		al.add(f1);
		al.add(f2);*/
		ArrayList<String> al = new ArrayList<String>();
		al.add("Gnomesort.java");
		al.add("Combsort.java");
		System.out.println("Unpacking student's files. Overwriting...");
		fu.unzip(studentZipFile, destDirectory, al);
		System.out.println("Extraction finished!!!");
	}
}
