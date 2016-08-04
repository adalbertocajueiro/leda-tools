package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.text.SimpleDateFormat;

public class TestReportErrorItem extends TestReportItem {

	public TestReportErrorItem(String matricula, String studentName,
			long lastModified, File mavenOutputLog) {
		super(null, matricula, studentName, lastModified, 0, 0, 0, 0, 0, null,
				mavenOutputLog);
	}

	public static void main(String[] args) {
		File file = new File(
				"D:\\trash\\counting\\subs\\654321-ADERBAL DOS SANTOS.zip");

		System.out.println("Before Format : " + file.lastModified());

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		System.out.println("After Format : " + sdf.format(file.lastModified()));
	}
}
