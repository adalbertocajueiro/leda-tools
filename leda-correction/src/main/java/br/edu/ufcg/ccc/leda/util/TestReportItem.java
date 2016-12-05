package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

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

	public TestReportItem(File testResultXMLFile, String matricula,
			String studentName, long lastModified, int totalTests, int errors,
			int failures, int skiped, double time, File completeReport,
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
	
	private Path generateRelativeLink(File referenciado, File referenciador)
			throws MalformedURLException {
		//Path pathAbsolute = Paths.get(referenciado.getAbsolutePath());
		//Path pathBase = Paths.get(referenciador.getAbsolutePath());
		Path pathAbsolute = referenciado.toPath();
		Path pathBase = referenciador.toPath();
		Path pathRelative = pathBase.relativize(pathAbsolute);

		return pathRelative;
	}

	/**
	 * Gera o link relativo para o relatorio completo do surefire.
	 * o link relativo considera a pasta do projeto, que eh a pasta pai do maven-output.txt
	 * @return
	 */
	public String generateCompleteReportLink(){
		Path relativeLink = null;
		if(this.mavenOutputLog != null && this.mavenOutputLog.exists()){
			File atividadeFolder = this.mavenOutputLog.getParentFile().getParentFile().getParentFile();
			if(this.completeReport != null && this.completeReport.exists()){
				try {
					relativeLink = generateRelativeLink(this.completeReport, atividadeFolder);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					relativeLink = this.completeReport.toPath();
				}
			}
		}
		return relativeLink != null? relativeLink.toString():"";
	}
	
	public String generateMavenOutputLink(){
		Path relativeLink = null;
		if(this.mavenOutputLog != null && this.mavenOutputLog.exists()){
			File projectFolder = this.mavenOutputLog.getParentFile().getParentFile().getParentFile();
	
			try {
				relativeLink = generateRelativeLink(this.mavenOutputLog, projectFolder);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				relativeLink = this.mavenOutputLog.toPath();
			}
		}
		return relativeLink != null? relativeLink.toString():"";
	}
	
	public boolean hasSubmitted(){
		return this.mavenOutputLog != null;
	}
	
	public boolean hasCompilationError(){
		return this.completeReport == null;
	}
	
	public double calculateTestScore(){
		double score = 0;
		if(hasSubmitted() && !hasCompilationError()){
			double passed = this.totalTests - this.errors;
			if(passed < 0){
				passed = 0;
			}
			score = passed/this.totalTests*4.0;
		}
		return score;
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
