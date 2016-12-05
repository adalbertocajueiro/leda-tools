package br.edu.ufcg.ccc.leda.submission.util;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class SimilarityDistance implements DistanceMeasure {

	@Override
	public double compute(double[] arg0, double[] arg1) throws DimensionMismatchException {
		double similarity = 0;
		
		if(arg0.length == arg1.length){
			int coincidences = 0;
			for (int i = 0; i < arg0.length; i++) {
				if(arg0[i] == arg1[i]){
					coincidences++;
				}
			}
			similarity = ((double)coincidences)/(double)arg0.length;
		}
		
		
		
		return similarity;
	}

}
