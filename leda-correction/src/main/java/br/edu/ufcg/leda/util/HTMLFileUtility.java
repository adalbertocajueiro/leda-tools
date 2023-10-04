package br.edu.ufcg.leda.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class HTMLFileUtility {

	private String loadHTMLFile(String filePath) throws IOException {

		StringBuilder result = new StringBuilder();
		InputStream is = HTMLFileUtility.class.getResourceAsStream(filePath);

		InputStreamReader isr = new InputStreamReader(is);
		// System.out.println("Loading HTML file: " + filePath + " stream: " +
		// is);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
			result.append(line + "\n");
		}

		is.close();
		isr.close();
		br.close();

		return result.toString();
	}

	public Document buildMainReport(String htmlFilePath, String testSuiteTag,
			String testSuiteName, String testsTag, int tests,
			TestReport report, File targetFolder) throws IOException {

		String contentString = this.loadHTMLFile(htmlFilePath);
		// System.out.println("$$$$$Testsuitetag: " + testSuiteTag);
		// System.out.println("$$$$$Testsuitename: " + testSuiteName);
		contentString = contentString.replace(testSuiteTag, testSuiteName);
		contentString = contentString.replace(testsTag, String.valueOf(tests));

		Document doc = Jsoup.parse(contentString);

		// puts the information from the test report of all students in the
		// general report
		// it first obtains the table of the reports
		Element table = doc.getElementById("reportTable");
		buildTestReportTable(table, report, tests, targetFolder);

		return doc;
	}

	private void buildTestReportTable(Element table, TestReport report,
			int tests, File targetFolder) {
		Element tableHeader = buildHeader();
		table.appendChild(tableHeader);

		boolean classA = false;
		ArrayList<TestReportItem> items = report.getReportItems();
		for (TestReportItem testReportItem : items) {
			Element line = this.buildTableLine(testReportItem, classA, tests,
					targetFolder);
			table.appendChild(line);
			if (classA) {
				classA = false;
			} else {
				classA = true;
			}
		}
	}

	private Element buildHeader() {
		Element tableHeader = new Element(Tag.valueOf("tr"), "");
		tableHeader.addClass("a");

		Element headerMatricula = new Element(Tag.valueOf("th"), "");
		headerMatricula.appendText("Enrollment");
		tableHeader.appendChild(headerMatricula);

		Element headerStudent = new Element(Tag.valueOf("th"), "");
		headerStudent.appendText("Student");
		tableHeader.appendChild(headerStudent);

		Element receptionTime = new Element(Tag.valueOf("th"), "");
		receptionTime.appendText("Received at");
		tableHeader.appendChild(receptionTime);

		Element pass = new Element(Tag.valueOf("th"), "");
		Element font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "green");
		font.appendText("Pass");
		pass.appendChild(font);
		tableHeader.appendChild(pass);

		Element errors = new Element(Tag.valueOf("th"), "");
		font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "red");
		font.appendText("Errors");
		errors.appendChild(font);
		tableHeader.appendChild(errors);

		Element failures = new Element(Tag.valueOf("th"), "");
		font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "blue");
		font.appendText("Failures");
		failures.appendChild(font);
		tableHeader.appendChild(failures);

		Element skipped = new Element(Tag.valueOf("th"), "");
		skipped.appendText("Skipped");
		tableHeader.appendChild(skipped);

		Element time = new Element(Tag.valueOf("th"), "");
		time.appendText("Time");
		tableHeader.appendChild(time);

		Element detailedReport = new Element(Tag.valueOf("th"), "");
		detailedReport.appendText("Detailed Report");
		tableHeader.appendChild(detailedReport);

		return tableHeader;
	}

	private Element buildTableLine(TestReportItem item, boolean classA,
			int tests, File targetFolder) {
		Element tableLine = new Element(Tag.valueOf("tr"), "");
		if (classA) {
			tableLine.addClass("a");
		} else {
			tableLine.addClass("b");
		}

		Element matricula = new Element(Tag.valueOf("td"), "");
		matricula.appendText(item.getMatricula());
		tableLine.appendChild(matricula);

		Element name = new Element(Tag.valueOf("td"), "");
		name.appendText(item.getStudentName());
		tableLine.appendChild(name);

		Element sentTime = new Element(Tag.valueOf("td"), "");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		sentTime.appendText(formatter.format(item.getLastModified()));
		tableLine.appendChild(sentTime);

		Element pass = new Element(Tag.valueOf("td"), "");
		Element font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "green");
		if (item instanceof TestReportErrorItem) {
			font.appendText(String.valueOf(0));
		} else {
			font.appendText(String.valueOf(item.getTotalTests()
					- item.getErrors() - item.getFailures() - item.getSkipped()));
		}
		pass.appendChild(font);
		tableLine.appendChild(pass);

		Element errors = new Element(Tag.valueOf("td"), "");
		font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "red");
		font.appendText(String.valueOf(item.getErrors()));
		errors.appendChild(font);
		tableLine.appendChild(errors);

		Element failures = new Element(Tag.valueOf("td"), "");
		font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "blue");
		font.appendText(String.valueOf(item.getFailures()));
		failures.appendChild(font);
		tableLine.appendChild(failures);

		Element skipped = new Element(Tag.valueOf("td"), "");
		skipped.appendText(String.valueOf(item.getSkipped()));
		tableLine.appendChild(skipped);

		Element time = new Element(Tag.valueOf("td"), "");
		time.appendText(String.valueOf(item.getTime()));
		tableLine.appendChild(time);

		Element detailedReport = new Element(Tag.valueOf("td"), "");
		if (item instanceof TestReportErrorItem) {
			font = new Element(Tag.valueOf("font"), "");
			font.attr("color", "red");
			font.appendText("Report not generated. Possible compilation error. ");
			detailedReport.appendChild(font);
		} else {
			Element link = new Element(Tag.valueOf("a"), "");
			try {
				Path relativeLink = generateRelativeLink(
						item.getCompleteReport(), targetFolder);
				link.attr("href", relativeLink.toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				link.attr("href", item.getCompleteReport().toString());
			}
			link.appendText("See detailed report. ");
			detailedReport.appendChild(link);

			// Element linkMvnOutput = new Element(Tag.valueOf("a"), "");
			// linkMvnOutput.attr("href", item.getMavenOutputLog().toString());
			// linkMvnOutput.appendText("See maven output.");
			// detailedReport.appendChild(linkMvnOutput);
		}
		Element linkMvnOutput = new Element(Tag.valueOf("a"), "");

		try {
			Path relativeLink = generateRelativeLink(item.getMavenOutputLog(),
					targetFolder);
			linkMvnOutput.attr("href", relativeLink.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			linkMvnOutput.attr("href", item.getMavenOutputLog().toString());
		}
		linkMvnOutput.appendText(" See maven output.");
		detailedReport.appendChild(linkMvnOutput);

		tableLine.appendChild(detailedReport);

		return tableLine;
	}

	/**
	 * Dados dois arquivos, observa a pasta em que o segundo esta e gera um link
	 * relativo dele para o primeiro. A ideia é ir subindo pelas pastas parent
	 * ate encontrar o mesmo parent e a partir dele montar o caminho reverso
	 * escrevendo a subida de pastas com "..";
	 * 
	 * @param referenciado
	 * @param referenciador
	 * @return
	 * @throws MalformedURLException
	 */
	private Path generateRelativeLink(File referenciado, File referenciador)
			throws MalformedURLException {
		Path pathAbsolute = Paths.get(referenciado.getAbsolutePath());
		Path pathBase = Paths.get(referenciador.getAbsolutePath());
		Path pathRelative = pathBase.relativize(pathAbsolute);
		// System.out.println(pathRelative);
		return pathRelative;
	}

	public static void main(String[] args) throws MalformedURLException {
		HTMLFileUtility ru = new HTMLFileUtility();
		File f1 = new File("D:\\trash2\\R2-01-correction\\subs");
		File f2 = new File("D:\\trash2\\R2-01-correction\\target");
		Path link = ru.generateRelativeLink(f1, f2);
		System.out.println(link);
		// link = ru.generateRelativeLink(f2, f1);
		// System.out.println(link);
		// int i = 0;
	}

	public void writeXMLFile(Document document, String filePath) {
		try {
			FileWriter writer = new FileWriter(filePath);
			writer.write(document.outerHtml());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
