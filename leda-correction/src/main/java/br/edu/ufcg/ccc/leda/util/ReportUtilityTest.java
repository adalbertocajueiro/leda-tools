package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.jdom2.JDOMException;

public class ReportUtilityTest {

	public static void main(String[] args) throws IOException, JDOMException {
		ReportUtility ru = new ReportUtility();
		String FINAL_HTML_FILE = "D:\\trash2\\leda-upload\\2016.1\\R01-02\\target\\generated-report.html";
		File submissionsFolder = new File("D:\\trash2\\leda-upload\\2016.2\\RR1-01\\subs");
		Map<String,Student> alunos = new HashMap<String,Student>();
		try {
			alunos = Util.getAllStudents("http://web.cloud.lsd.ufcg.edu.br:44174/submission/alunosJson");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		ru.createAndSaveJsonTestReport(submissionsFolder, alunos);
		System.out.println("End");
	}
}
