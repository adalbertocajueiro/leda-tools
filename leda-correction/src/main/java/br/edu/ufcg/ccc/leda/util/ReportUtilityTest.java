package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

public class ReportUtilityTest {

	public static void main(String[] args) throws IOException, JDOMException {
		ReportUtility ru = new ReportUtility();
		String BASIC_HTML_FILE = "/template-report.html";
		String FINAL_HTML_FILE = "D:\\tmp\\avlcorr\\target\\generated-report.html";
		File folder = new File("D:\\tmp\\avlcorr\\subs");
		ru.createAndSaveReport(folder, BASIC_HTML_FILE, FINAL_HTML_FILE);
		System.out.println("End");
	}
}
