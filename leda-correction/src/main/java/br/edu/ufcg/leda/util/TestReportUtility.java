package br.edu.ufcg.leda.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import br.edu.ufcg.leda.commons.user.Student;
import br.edu.ufcg.leda.commons.util.ReportUtility;
import br.edu.ufcg.leda.commons.util.XMLFileUtility;

public class TestReportUtility extends ReportUtility{

	/**
	 * It generates the main report referencing all students reports.
	 * 
	 * @param submissionsFolder
	 *            the folder containing all students projects folder generated
	 *            by the correction tool. Normally this folder is the same as
	 *            the folder containing all submissions (zip files).
	 * @param targetFolder
	 *            a pasta onde se encontra o relatorio final que contera links
	 *            relativos para os relatorios especificos
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 
	@Deprecated
	private TestReport generateReport(File submissionsFolder, File targetFolder)
			throws IOException, JDOMException {
		TestReport report = new TestReport(null, 0);

		if (submissionsFolder.isDirectory()) {
			File[] subFolders = submissionsFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			});
			// as pastas tem o nome sendo <MATRICULA>-<NOME DO ALUNO>
			for (File subFolder : subFolders) {
				File xmlFile = this.getSurefireReportFile(subFolder);

				File mavenOutputLog = new File(subFolder.getAbsolutePath()
						+ File.separator + MavenUtility.MAVEN_OUTPUT_LOG);

				if (xmlFile != null) {
					XMLFileUtility fu = new XMLFileUtility();
					Document xml = fu.loadXMLFile(xmlFile);
					Element testSuite = xml.getRootElement();
					int tests = Integer.parseInt(testSuite
							.getAttributeValue("tests"));
					if (report.getTestSuiteName() == null) {
						String testSuiteName = testSuite
								.getAttributeValue("name");
						report.setTestSuiteName(testSuiteName);
						report.setNumberOfTests(tests);
					}else{
						if(tests > report.getNumberOfTests()){
							report.setNumberOfTests(tests);
						}
					}
					double time = Double.parseDouble(testSuite
							.getAttributeValue("time"));
					int failures = Integer.parseInt(testSuite
							.getAttributeValue("failures"));
					String matricula = subFolder.getName()
							.substring(0, subFolder.getName().indexOf("-"))
							.trim();
					String studentName = subFolder.getName()
							.substring(subFolder.getName().indexOf("-") + 1)
							.trim();
					String generatedReport = subFolder.getAbsolutePath()
							+ File.separator + "target" + File.separator
							+ "site" + File.separator + "project-reports.html";
					File completeReport = new File(generatedReport);
					int errors = Integer.parseInt(testSuite
							.getAttributeValue("errors"));
					int skipped = Integer.parseInt(testSuite
							.getAttributeValue("skipped"));

					File sentZipFile = new File(subFolder.getParent(),
							subFolder.getName() + ".zip");

					TestReportItem item = new TestReportItem(xmlFile,
							matricula, studentName, sentZipFile.lastModified(),
							tests, errors, failures, skipped, time,
							completeReport, mavenOutputLog);
					report.getReportItems().add(item);
				} else {
					String matricula = subFolder.getName()
							.substring(0, subFolder.getName().indexOf("-"))
							.trim();
					String studentName = subFolder.getName();
					File sentZipFile = new File(subFolder.getName() + ".zip");

					TestReportErrorItem errorItem = new TestReportErrorItem(
							matricula, studentName, sentZipFile.lastModified(),
							mavenOutputLog);
					report.getReportItems().add(errorItem);
				}
			}
		}

		// precisa ordenar os itens do report por nome para facilitar
		report.getReportItems().sort(
				(s1, s2) -> s1.getStudentName().compareTo(s2.getStudentName()));

		return report;
	}
	*/
	private TestReport generateJsonTestReport(File submissionsFolder, Map<Integer,List<Student>> alunos)
			throws IOException, JDOMException {
		//System.out.println("submissions folder: " + submissionsFolder.getAbsolutePath());
		TestReport report = new TestReport(null, 0);
		//System.out.println("Lista de alunos recebida: " + alunos);
		Integer turma = Integer.parseInt(submissionsFolder.getParentFile().getName().substring(4));
		List<Student> alunosDaTurma = alunos.get(turma);
		alunosDaTurma.sort((s1,s2) -> s1.getNome().compareTo(s2.getNome()));

		if (submissionsFolder.isDirectory()) {
			
			for (Student aluno : alunosDaTurma) {
				File pastaDoAluno = getFolderForStudent(submissionsFolder, aluno.getMatricula());
				
				if(pastaDoAluno != null){
					File xmlFile = this.getSurefireReportFile(pastaDoAluno);
					File mavenOutputLog = new File(pastaDoAluno.getAbsolutePath()
							+ File.separator + MavenUtility.MAVEN_OUTPUT_LOG);
	
					if (xmlFile != null) {
						XMLFileUtility fu = new XMLFileUtility();
						Document xml = fu.loadXMLFile(xmlFile);
						
						Element testSuite = xml.getRootElement();
						int tests = Integer.parseInt(testSuite
								.getAttributeValue("tests"));
						if (report.getTestSuiteName() == null) {
							String testSuiteName = testSuite
									.getAttributeValue("name");
							report.setTestSuiteName(testSuiteName);
							report.setNumberOfTests(tests);
						}else{
							if(tests > report.getNumberOfTests()){
								report.setNumberOfTests(tests);
								updateNumberOfTestsInReportItems(tests,report);
							}
						}
						double time = Double.parseDouble(testSuite
								.getAttributeValue("time"));
						int failures = Integer.parseInt(testSuite
								.getAttributeValue("failures"));
						String matricula = aluno.getMatricula();
						String studentName = aluno.getNome();
						String generatedReport = pastaDoAluno.getAbsolutePath()
								+ File.separator + "target" + File.separator
								+ "site" + File.separator + "project-reports.html";
						File completeReport = new File(generatedReport);
						int errors = Integer.parseInt(testSuite
								.getAttributeValue("errors"));
						int skipped = Integer.parseInt(testSuite
								.getAttributeValue("skipped"));
	
						File sentZipFile = new File(pastaDoAluno.getParent(),
								pastaDoAluno.getName() + ".zip");
	
						TestReportItem item = new TestReportItem(xmlFile,
								matricula, studentName, sentZipFile.lastModified(),
								tests, errors, failures, skipped, time,
								completeReport, mavenOutputLog);
						report.getReportItems().add(item);
					} else {
						String matricula = aluno.getMatricula();
						String studentName = aluno.getNome();
						File sentZipFile = new File(pastaDoAluno.getParent(),
								pastaDoAluno.getName() + ".zip");
	
						TestReportErrorItem errorItem = new TestReportErrorItem(
								matricula, studentName, sentZipFile.lastModified(),
								mavenOutputLog);
						report.getReportItems().add(errorItem);
					}
				}else{
					TestReportErrorItem errorItem = new TestReportErrorItem(
							aluno.getMatricula(), aluno.getNome(), 0,
							null);
					report.getReportItems().add(errorItem);
				}
			}
			//no final precisa dar uma outra passada em todos os itens para lidar com os 
			//que entraram em loop porque a nota de testes dele esta incorreta
			//porque nao consideram a quantidade total de testes
			report.getReportItems().stream()
				.filter(tri -> tri.getTotalTests() < report.getNumberOfTests())
				.forEach(tri -> {
					int naoExecutados = report.getNumberOfTests() - tri.getTotalTests();
					tri.setTotalTests(report.getNumberOfTests());
					tri.setFailures(tri.getFailures() + naoExecutados);
				});
		}

		// precisa ordenar os itens do report por nome para facilitar
		report.getReportItems().sort(
				(s1, s2) -> s1.getStudentName().compareTo(s2.getStudentName()));

		return report;
	}
	

	

	private void updateNumberOfTestsInReportItems(int tests, TestReport report) {
		report.getReportItems().forEach(tri -> tri.setTotalTests(tests));
	}

	
	private File getFolderForStudent(File submissionsFolder, String matricula){
		File studentFolder = null;
		File[] subFolders = submissionsFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().contains(matricula);
			}
		});
		if(subFolders.length == 1){
			studentFolder = subFolders[0];
		}
		return studentFolder;
	}
	
	public void createAndSaveJsonTestReport(File submissionsFolder,
			Map<Integer,List<Student>> alunos)
			throws IOException, JDOMException {
		TestReport testReport = this
				.generateJsonTestReport(submissionsFolder, alunos);
		
		File jsonFile = new File(submissionsFolder.getParentFile(),submissionsFolder.getParentFile().getName() + "-report.json");
		Utilities.writeTestReportToJson(testReport, jsonFile);
	}
	

	private File getSurefireReportFile(File folder) {
		File result = null;
		File targetFolder = new File(folder, "target");
		if (targetFolder.exists()) {
			File surefireFolder = new File(targetFolder, "surefire-reports");
			if (surefireFolder.exists()) {
				File[] xmlFiles = surefireFolder.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {

						return pathname.getName().endsWith(".xml");
					}
				});
				if (xmlFiles.length > 0) {
					result = xmlFiles[0];
				}
			}
		}
		return result;
	}
}
