package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.JDOMException;

public class MavenUtilityTest {
	public static void main(String[] args) throws IOException, JDOMException {
		File mavenHomeFolder = new File("D:\\apache-maven-3.2.5");
		File projectsFolder = new File("D:\\UFCG\\2015.2\\disciplinas\\leda\\PP1-T2\\subs");
		MavenUtility mu = new MavenUtility(projectsFolder,mavenHomeFolder,
				"br.edu.ufcg.ccc.leda","Correction Tool Client", "Prova-RecursiveSelectionsort-tests.jar", "RunTests.java");
		//File correctionZipFile = new File("D:\\tmp\\submissions\\Gnomesort-Combsort-environment.zip");
		File correctionZipFile = new File("D:\\UFCG\\2015.2\\disciplinas\\leda\\PP1-T2\\Prova-RecursiveSelectionsort-correction-env.zip");
		ArrayList<String> al = new ArrayList<String>();
		al.add("RecursiveSelectionSort.java");
		//al.add("Combsort.java");
		File studentZipFile = new File("D:\\UFCG\\2015.2\\disciplinas\\leda\\PP1-T2\\subs\\ADAUTO FERREIRA DE BARRROS NETO.zip");
		mu.createCompleteProjectFolder(correctionZipFile, studentZipFile, al);
		System.out.println("Creation completed");
		/*File projectFoder =  mu.createCompleteProjectFolder(envZipFile, studentZipFile, al);
		System.out.println("Folder created: " + projectFoder.getAbsolutePath());
		mu.executeMaven(projectFoder);
		System.out.println("Finished...");*/
	}
}
