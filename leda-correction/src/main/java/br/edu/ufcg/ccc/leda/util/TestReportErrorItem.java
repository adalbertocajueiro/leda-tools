package br.edu.ufcg.ccc.leda.util;

import java.io.File;

public class TestReportErrorItem extends TestReportItem {
	
	public TestReportErrorItem(String studentName,File mavenOutputLog) {
		super(null,studentName,0,0,0,0,0,null,mavenOutputLog);
	}	
}
