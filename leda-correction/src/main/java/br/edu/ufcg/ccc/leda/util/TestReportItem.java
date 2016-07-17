package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.net.URL;
import java.util.GregorianCalendar;

/**
 * @author Adalberto
 *
 */
public class TestReportItem {
	private File testResultXMLFile;
	private String matricula;
	private String studentName;
	private long lastModified;
	private int totalTests;
	private int errors;
	private int failures;
	private int skipped;
	private double time;
	private File completeReport;
	private File mavenOutputLog;
	
	public TestReportItem(File testResultXMLFile, String matricula, String studentName, long lastModified, 
			int totalTests, int errors, int failures, int skiped, double time, File completeReport,
			File mavenOutputLog) {
		super();
		this.testResultXMLFile = testResultXMLFile;
		this.matricula = matricula;
		this.studentName = studentName;
		this.lastModified = lastModified;
		this.totalTests = totalTests;
		this.errors = errors;
		this.failures = failures;
		this.skipped = skiped;
		this.time = time;
		this.completeReport = completeReport;
		this.mavenOutputLog = mavenOutputLog;
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

	public int getSkipped() {
		return skipped;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

	public File getCompleteReport() {
		return completeReport;
	}

	public void setCompleteReport(File completeReport) {
		this.completeReport = completeReport;
	}

	public File getMavenOutputLog() {
		return mavenOutputLog;
	}

	public void setMavenOutputLog(File mavenOutputLog) {
		this.mavenOutputLog = mavenOutputLog;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	
}
