package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import org.apache.commons.math3.ml.clustering.CentroidCluster;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import br.edu.ufcg.ccc.leda.util.TestCase;
import br.edu.ufcg.ccc.leda.util.TestResult;
import br.edu.ufcg.ccc.leda.util.XMLFileUtility;

public class XMLUtil {

	public Document loadXMLFile(String filePath) throws IOException, JDOMException {
		SAXBuilder saxBuilder = new SAXBuilder();

		InputStream is = XMLFileUtility.class.getResourceAsStream(filePath);
		Document document = saxBuilder.build(is);

		return document;
	}
	
	/**
	 * nao considera os casos de teste skipped
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	public static StudentTestList buildTestResultVector(File file,String matricula) throws IOException, JDOMException {
		StudentTestList result = new StudentTestList(matricula);
		
		SAXBuilder saxBuilder = new SAXBuilder();
		if(file.exists()){
			Document document = saxBuilder.build(file);
	
			List<Element> testcases = document.getRootElement().getChildren()
					.stream().filter(e -> e.getName().equals("testcase"))
					.collect(Collectors.toList());
			
			for (Element element : testcases) {
				TestResult tr = getTestResult(element);
				//para pegar o tipo de teste precisa varrer o conteudo do elemento procurando por outro element
				//que tenha name error ou Failure.class se nao for nenhum desses entao eh pass
				TestCase tc = new TestCase(element.getAttributeValue("name"),element.getAttributeValue("classname"),tr);		
				if(tc.getResult() != TestResult.SKIPPED){
					result.add(tc);
				}
			}
		}
		return result;
	}
	
	private static TestResult getTestResult(Element element) {
		TestResult result = TestResult.PASS;
		Element ele = element.getChildren().stream().filter(e -> e instanceof Element)
				.findFirst().orElse(null);
		if(ele != null ){
			if(ele.getName().equals("skipped")){
				result = TestResult.SKIPPED;
			}else{
				result = TestResult.ERROR_OR_FAILURE;
			}
		}
		return result;
	}

	public static void main(String[] args) throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		//File filePath = new File("D:\\UFCG\\2016.1\\disciplinas\\leda\\submissions\\R01-01\\subs\\ALESSANDRO LIA FOOK SANTOS\\target\\surefire-reports\\TEST-correction.tests.TestSuite.xml");
		File subs = new File("D:\\UFCG\\2016.1\\disciplinas\\leda\\submissions\\R03-01\\subs");
		File[] subFolders = subs.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		List<StudentTestList> listaTestes = new ArrayList<StudentTestList>();
		for (File subFolder : subFolders) {
			int index = subFolder.getName().indexOf("-");
			String matricula = subFolder.getName().substring(0,index);
			File filePath = new File(subFolder,"target" + File.separator + 
					"surefire-reports" + File.separator + "TEST-correction.tests.TestSuite.xml");
			
			StudentTestList tests = XMLUtil.buildTestResultVector(filePath,matricula);
			listaTestes.add(tests);

		}
		List<CentroidCluster<StudentTestList>> clusters = ClusteringUtil.runKMeansPlusPlus(3, listaTestes);
		
		//InputStream is = XMLUtil.class.getResourceAsStream(filePath)");
		File filePath = new File("D:\\UFCG\\2016.1\\disciplinas\\leda\\submissions\\R03-01\\subs\\114111352-ALEXANDRE GULLO\\target\\surefire-reports\\TEST-correction.tests.TestSuite.xml");
		//InputStream is = XMLUtil.class.getResourceAsStream(filePath);
		
		StudentTestList tests = XMLUtil.buildTestResultVector(filePath,"");
		listaTestes.add(tests);
		//double[] point = ClusteringUtil.getPoint(tests);
		filePath = new File("D:\\UFCG\\2016.1\\disciplinas\\leda\\submissions\\R03-01\\subs\\114210792-IGOR BRASILEIRO DUARTE\\target\\surefire-reports\\TEST-correction.tests.TestSuite.xml");
		//Map<TestResult,Long> filtro = tests.stream()
		//		.collect(Collectors.groupingBy(TestCase::getResult,Collectors.counting()));
		tests = XMLUtil.buildTestResultVector(filePath,"");
		listaTestes.add(tests);
		
		Document document = saxBuilder.build(filePath);
		List<Element> testcases = document.getRootElement().getChildren()
				.stream().filter(e -> e.getName().equals("testcase"))
				.collect(Collectors.toList());
		
		System.out.println(document.hasRootElement());
		int i = 0;
	}
}
