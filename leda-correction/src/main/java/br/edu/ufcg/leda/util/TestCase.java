package br.edu.ufcg.leda.util;

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
	

	@Override
	public boolean equals(Object obj) {
		boolean resp = false;
		if(obj instanceof TestCase){
			resp = this.testCaseClass.equals(((TestCase) obj).getTestCaseClass())
					&& this.testCaseName.equals(((TestCase) obj).getTestCaseName());
		}
		return resp;
	}

	

	@Override
	public String toString() {
		return this.result.ordinal() + "";
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
