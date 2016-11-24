package br.edu.ufcg.ccc.leda.util;

public class TestCase {

	private String testCaseName;
	private String testCaseClass;
	private TestResult result;
	
	public TestCase(String testCaseName, String testCaseClass, TestResult result) {
		super();
		this.testCaseName = testCaseName;
		this.testCaseClass = testCaseClass;
		this.result = result;
	}

	public String getTestCaseName() {
		return testCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}

	public String getTestCaseClass() {
		return testCaseClass;
	}

	public void setTestCaseClass(String testCaseClass) {
		this.testCaseClass = testCaseClass;
	}

	public TestResult getResult() {
		return result;
	}

	public void setResult(TestResult result) {
		this.result = result;
	}
	
	
}
