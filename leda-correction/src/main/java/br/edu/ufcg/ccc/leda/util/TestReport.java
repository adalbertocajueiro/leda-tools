package br.edu.ufcg.ccc.leda.util;

import java.util.ArrayList;

public class TestReport {
	private String testSuitName;
	private int numberOfTests;
	private ArrayList<TestReportItem> reportItems;

	public TestReport(String testSuitName, int numberOfTests) {
		super();
		this.testSuitName = testSuitName;
		this.numberOfTests = numberOfTests;
		this.reportItems = new ArrayList<TestReportItem>();
	}

	public String getTestSuitName() {
		return testSuitName;
	}

	public void setTestSuitName(String testSuitName) {
		this.testSuitName = testSuitName;
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
