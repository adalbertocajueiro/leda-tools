package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.jdom2.JDOMException;


public class ClusteringUtil {
/*	public static double[] getPoint(List<TestCase> testCases){
		//List<TestCase> testCasesSemSkipped = testCases.stream().filter( tc -> tc.getResult()!= TestResult.SKIPPED)
		//		.collect(Collectors.toList());
		double[] point = new double[testCases.size()];
		int index = 0;
		for (TestCase tc : testCases) {
			point[index++] = tc.getResult().ordinal();
		}
		return point;
	} 
*/	
	public static SimilarityMatrix buildSimilarityMatrix(File atividadeId) throws IOException, JDOMException{
		SimilarityMatrix result = null;
		
		File submissions = new File(atividadeId,Constants.SUBMISSIONS_FOLDER_NAME);
		List<StudentTestList> studentsTestList = buildStudentsTestList(submissions);
		result = new SimilarityMatrix(studentsTestList);
		
		return result;
	}
	 
	
	public static List<StudentTestList> buildStudentsTestList(File folderWithSubmissions) throws IOException, JDOMException{
		File[] subFolders = folderWithSubmissions.listFiles(new FileFilter() {
			
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
			//se deu problema de compilacao o 'tests' pode ter um array de testes vazio
			//e isso vai dar problema no calculo da similaridade. 
			//isso tem que ser considerado na construcao da matrix de similaridades
			listaTestes.add(tests);

		}
		
		return listaTestes;
	}
	
	public static List<CentroidCluster<StudentTestList>> runKMeansPlusPlus(int k, List<StudentTestList> listStudentTests){
		KMeansPlusPlusClusterer<StudentTestList> kmeansAlgorithm = 
				new KMeansPlusPlusClusterer<StudentTestList>(k);
		
		List<CentroidCluster<StudentTestList>> listaCentroides 
			= kmeansAlgorithm.cluster(listStudentTests);
		
		return listaCentroides; 
	}
	
	public static List<CentroidCluster<StudentTestList>> runKMeansPlusPlus(int k, 
			List<StudentTestList> listStudentTests, DistanceMeasure function){
		KMeansPlusPlusClusterer<StudentTestList> kmeansAlgorithm = 
				new KMeansPlusPlusClusterer<StudentTestList>(k, 11, function);
		
		List<CentroidCluster<StudentTestList>> listaCentroides 
			= kmeansAlgorithm.cluster(listStudentTests);
		
		return listaCentroides; 
	}
	
	public static void main(String[] args) throws IOException, JDOMException {
		File R03_01 = new File("D:\\UFCG\\2016.1\\disciplinas\\leda\\submissions\\R03-01");
		SimilarityMatrix matrix = ClusteringUtil.buildSimilarityMatrix(R03_01);
		double[][] m = matrix.getSimilarities();
		DecimalFormat df = new DecimalFormat("0.00");
		/*for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m.length; j++) {
				System.out.print(df.format(m[i][j]) + " ");
			}
			System.out.println("");
		}*/
		
		List<CentroidCluster<StudentTestList>> clusters = ClusteringUtil.runKMeansPlusPlus(3, matrix.getStudentsTestList(), new SimilarityDistance());
		int i = 0;
		System.out.println(clusters);
	}
	
}
