package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.net.URL;

public class TestReportItem {
	private File testResultXMLFile;
	private String studentName;
	private int totalTests;
	private int errors;
	private int failures;
	private double time;
	private URL completeReport;
	
	public TestReportItem(File testResultXMLFile, String studentName,
			int totalTests, int errors, int failures, double time, URL completeReport) {
		super();
		this.testResultXMLFile = testResultXMLFile;
		this.studentName = studentName;
		this.totalTests = totalTests;
		this.errors = errors;
		this.failures = failures;
		this.time = time;
		this.completeReport = completeReport;
	}

	public File getTestResultXMLFile() {
		return testResultXMLFile;
	}

	public void setTestResultXMLFile(File testResultXMLFile) {
		this.testResultXMLFile = testResultXMLFile;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public int getFailures() {
		return failures;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}

	public URL getCompleteReport() {
		return completeReport;
	}

	public void setCompleteReport(URL completeReport) {
		this.completeReport = completeReport;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public int getTotalTests() {
		return totalTests;
	}

	public void setTotalTests(int totalTests) {
		this.totalTests = totalTests;
	} 
	
}