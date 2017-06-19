package plag.runner;

import java.util.ArrayList;
import java.util.Collection;

import plag.parser.CachingDetectionResult;
import plag.parser.SubmissionDetectionResult;
import plag.parser.plaggie.Plaggie;

public class PlagRunner {
	public static void main(String[] args) {
		Plaggie plag = new Plaggie();
		String folder = "D:\\UFCG\\leda\\leda-tools\\leda-plaggie\\src\\main\\resources\\subs";
		String file1 = "D:\\UFCG\\leda\\leda-tools\\leda-plaggie\\target\\classes\\subs\\115210859-HECTOR HIAGO DE MEDEIROS\\src\\main\\java\\sorting\\variationsOfBubblesort\\SimultaneousBubblesort.java";
		String file2 = "D:\\UFCG\\leda\\leda-tools\\leda-plaggie\\target\\classes\\subs\\116210442-FILIPE PIRES GUIMARAES\\src\\main\\java\\sorting\\variationsOfBubblesort\\SimultaneousBubblesort.java";
		String[] args2 = {folder};
		plag.main(args2);
		ArrayList results = plag.detResults;
		ArrayList<SimilarityAnalysisResult> analysisResults = 
				new ArrayList<SimilarityAnalysisResult>();
		if(results.size() > 0){
			SubmissionDetectionResult result = (SubmissionDetectionResult)results.get(0);
			Collection fileResults = result.getFileDetectionResults();
			fileResults.forEach(o -> {
				if (o instanceof CachingDetectionResult){
					CachingDetectionResult r = (CachingDetectionResult)o;
					try {
						SimilarityAnalysisResult newResult
						 = new SimilarityAnalysisResult(r.getFileA(),r.getFileB(),Math.max(r.getSimilarityA(), r.getSimilarityB()));
						analysisResults.add(newResult);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		}
		int i = 0;
	}
}
