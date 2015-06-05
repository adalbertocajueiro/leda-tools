package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.JDOMException;

public class MavenUtilityTest {
	public static void main(String[] args) throws IOException, JDOMException {
		File mavenHomeFolder = new File("D:\\apache-maven-3.2.5");
		File projectsFolder = new File("D:\\tmp\\subs");
		MavenUtility mu = new MavenUtility(projectsFolder,mavenHomeFolder,
				"br.edu.ufcg.ccc.leda","Correction Tool Client", "Roteiro-OrdenacaoPorComparacao-I-tests.jar", "RunTestsTurma1.java");
		//File correctionZipFile = new File("D:\\tmp\\submissions\\Gnomesort-Combsort-environment.zip");
		File correctionZipFile = new File("D:\\tmp\\Rot10\\Roteiro10-correction-env.zip");
		ArrayList<String> al = new ArrayList<String>();
		al.add("AVLTree.java");
		//al.add("Combsort.java");
		File studentZipFile = new File("D:\\tmp\\subs\\Aluno1.zip");
		mu.createCompleteProjectFolder(correctionZipFile, studentZipFile, al);
		System.out.println("Creation completed");
		/*File projectFoder =  mu.createCompleteProjectFolder(envZipFile, studentZipFile, al);
		System.out.println("Folder created: " + projectFoder.getAbsolutePath());
		mu.executeMaven(projectFoder);
		System.out.println("Finished...");*/
	}
}
