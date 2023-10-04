package br.edu.ufcg.leda.util;

import java.util.ArrayList;

public class TestReport {
	private String testSuiteName;
	private int numberOfTests;
	private ArrayList<TestReportItem> reportItems;

	public TestReport(String testSuiteName, int numberOfTests) {
		super();
		this.testSuiteName = testSuiteName;
		this.numberOfTests = numberOfTests;
		this.reportItems = new ArrayList<TestReportItem>();
	}

	public TestReportItem getReportItemForStudent(String matricula){
		TestReportItem result = null;
		result = reportItems.stream().filter(tri -> tri.getMatricula().equals(matricula))
				.findFirst().orElse(null);
		
		return result;
	}
	
	public String getTestSuiteName() {
		return testSuiteName;
	}

	public void setTestSuiteName(String testSuitName) {
		this.testSuiteName = testSuitName;
	}

	public int getNumberOfTests() {
		return numberOfTests;
	}

	public void setNumberOfTests(int numberOfTests) {
		this.numberOfTests = numberOfTests;
	}

	public ArrayList<TestReportItem> getReportItems() {
		return reportItems;
	}

	public void setReportItems(ArrayList<TestReportItem> reportItems) {
		this.reportItems = reportItems;
	}

}
