package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		//System.out.println("Loading HTML file: " + filePath + " stream: " + is);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while((line = br.readLine()) != null){
			result.append(line + "\n");
		}
		
		is.close();
		isr.close();
		br.close();
		
		return result.toString();
	}
	
	public Document buildMainReport(String htmlFilePath, String testSuiteTag, 
			String testSuiteName, String testsTag, int tests, TestReport report) throws IOException {
		
		String contentString = this.loadHTMLFile(htmlFilePath);
		//System.out.println("$$$$$Testsuitetag: " + testSuiteTag);
		//System.out.println("$$$$$Testsuitename: " + testSuiteName);
		contentString = contentString.replace(testSuiteTag, testSuiteName);
		contentString = contentString.replace(testsTag, String.valueOf(tests));
		
		Document doc = Jsoup.parse(contentString);
		
		//puts the information from the test report of all students in the general report
		//it first obtains the table of the reports
		Element table = doc.getElementById("reportTable");
		buildTestReportTable(table, report, tests);
		
		return doc;
	}
	
	private void buildTestReportTable(Element table, TestReport report, int tests){
		Element tableHeader = buildHeader();
		table.appendChild(tableHeader);
		
		boolean classA = false;
		ArrayList<TestReportItem> items = report.getReportItems();
		for (TestReportItem testReportItem : items) {
			Element line = this.buildTableLine(testReportItem, classA, tests);
			table.appendChild(line);
			if(classA){
				classA = false;
			}else{
				classA = true;
			}
		}
	}

	private Element buildHeader() {
		Element tableHeader = new Element(Tag.valueOf("tr"),"");
		tableHeader.addClass("a");
		
		Element headerStudent = new Element(Tag.valueOf("th"),"");
		headerStudent.appendText("Student");
		tableHeader.appendChild(headerStudent);
		
		Element pass = new Element(Tag.valueOf("th"),"");
		Element font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "green");
		font.appendText("Pass");
		pass.appendChild(font);
		tableHeader.appendChild(pass);
		
		Element errors = new Element(Tag.valueOf("th"),"");
		font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "red");
		font.appendText("Errors");
		errors.appendChild(font);
		tableHeader.appendChild(errors);
		
		Element failures = new Element(Tag.valueOf("th"),"");
		font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "blue");
		font.appendText("Failures");
		failures.appendChild(font);
		tableHeader.appendChild(failures);
		
		Element skipped = new Element(Tag.valueOf("th"),"");
		skipped.appendText("Skipped");
		tableHeader.appendChild(skipped);
		
		Element time = new Element(Tag.valueOf("th"),"");
		time.appendText("Time");
		tableHeader.appendChild(time);
		
		Element detailedReport = new Element(Tag.valueOf("th"),"");
		detailedReport.appendText("Detailed Report");
		tableHeader.appendChild(detailedReport);
		
		return tableHeader;
	}
	
	private Element buildTableLine(TestReportItem item, boolean classA, int tests) {
		Element tableLine = new Element(Tag.valueOf("tr"),"");
		if(classA){
			tableLine.addClass("a");
		} else{
			tableLine.addClass("b");
		}
		
		Element name = new Element(Tag.valueOf("td"),"");
		name.appendText(item.getStudentName());
		tableLine.appendChild(name);
		
		Element pass = new Element(Tag.valueOf("td"),"");
		Element font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "green");
		if(item instanceof TestReportErrorItem){
			font.appendText(String.valueOf(0));
		}else{
			font.appendText(String.valueOf(item.getTotalTests() - item.getErrors() - item.getFailures() - item.getSkipped()));
		}
		pass.appendChild(font);
		tableLine.appendChild(pass);
		
		Element errors = new Element(Tag.valueOf("td"),"");
		font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "red");
		font.appendText(String.valueOf(item.getErrors()));
		errors.appendChild(font);
		tableLine.appendChild(errors);
		
		Element failures = new Element(Tag.valueOf("td"),"");
		font = new Element(Tag.valueOf("font"), "");
		font.attr("color", "blue");
		font.appendText(String.valueOf(item.getFailures()));
		failures.appendChild(font);
		tableLine.appendChild(failures);
		
		Element skipped = new Element(Tag.valueOf("td"),"");
		skipped.appendText(String.valueOf(item.getSkipped()));
		tableLine.appendChild(skipped);
		
		Element time = new Element(Tag.valueOf("td"),"");
		time.appendText(String.valueOf(item.getTime()));
		tableLine.appendChild(time);
		
		Element detailedReport = new Element(Tag.valueOf("td"),"");
		if(item instanceof TestReportErrorItem){
			font = new Element(Tag.valueOf("font"), "");
			font.attr("color", "red");
			font.appendText("Report not generated. Possible compilation error. ");
			detailedReport.appendChild(font);
		}else{
			Element link = new Element(Tag.valueOf("a"), "");
			link.attr("href", item.getCompleteReport().toString());
			link.appendText("See detailed report. ");
			detailedReport.appendChild(link);
			
			//Element linkMvnOutput = new Element(Tag.valueOf("a"), "");
			//linkMvnOutput.attr("href", item.getMavenOutputLog().toString());
			//linkMvnOutput.appendText("See maven output.");
			//detailedReport.appendChild(linkMvnOutput);
		}
		Element linkMvnOutput = new Element(Tag.valueOf("a"), "");
		linkMvnOutput.attr("href", item.getMavenOutputLog().toString());
		linkMvnOutput.appendText(" See maven output.");
		detailedReport.appendChild(linkMvnOutput);
		
		tableLine.appendChild(detailedReport);
		
		return tableLine;
	}
	
	public void writeXMLFile(Document document, String filePath){
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
