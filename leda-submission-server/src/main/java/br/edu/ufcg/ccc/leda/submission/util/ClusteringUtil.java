package br.edu.ufcg.ccc.leda.submission.util;

import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;

import br.edu.ufcg.ccc.leda.util.TestCase;

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
	public static List<CentroidCluster<StudentTestList>> runKMeansPlusPlus(int k, List<StudentTestList> listStudentTests){
		KMeansPlusPlusClusterer<StudentTestList> kmeansAlgorithm = 
				new KMeansPlusPlusClusterer<StudentTestList>(k);
		
		List<CentroidCluster<StudentTestList>> listaCentroides 
			= kmeansAlgorithm.cluster(listStudentTests);
		
		return listaCentroides; 
	}
	
}
