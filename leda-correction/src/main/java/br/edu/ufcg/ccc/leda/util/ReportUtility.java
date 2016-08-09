package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

public class ReportUtility {

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
	 */
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
					if (report.getTestSuitName() == null) {
						String testSuiteName = testSuite
								.getAttributeValue("name");
						report.setTestSuitName(testSuiteName);
						report.setNumberOfTests(tests);
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

	public static void main(String[] args) throws MalformedURLException {
		ReportUtility ru = new ReportUtility();
		File f1 = new File("D:\\trash2\\R2-01-correction\\subs");
		File f2 = new File("D:\\trash2\\R2-01-correction\\target");
		// Path link = ru.generateRelativeLink(f1, f2);
		// System.out.println(link);
		// link = ru.generateRelativeLink(f2, f1);
		// System.out.println(link);
		// int i = 0;
	}

	public void createAndSaveReport(File submissionsFolder,
			String templateHtml, File targetFolder, String finalPathHtml)
			throws IOException, JDOMException {
		TestReport report = this
				.generateReport(submissionsFolder, targetFolder);
		HTMLFileUtility htmlfu = new HTMLFileUtility();
		org.jsoup.nodes.Document doc = htmlfu.buildMainReport(templateHtml,
				"SUITENAME", report.getTestSuitName(), "TESTS",
				report.getNumberOfTests(), report, targetFolder);
		htmlfu.writeXMLFile(doc, finalPathHtml);
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
