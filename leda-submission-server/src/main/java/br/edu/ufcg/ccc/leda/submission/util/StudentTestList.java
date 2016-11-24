package br.edu.ufcg.ccc.leda.submission.util;

import java.util.ArrayList;

import org.apache.commons.math3.ml.clustering.Clusterable;

import br.edu.ufcg.ccc.leda.util.TestCase;

public class StudentTestList extends ArrayList<TestCase> implements Clusterable{

	private String matricula;
	
	public StudentTestList(String matricula) {
		super();
		this.matricula = matricula;
	}

	@Override
	public double[] getPoint() {
		double[] point = new double[this.size()];

		for (int i = 0; i < this.size(); i++) {
			point[i] = this.get(i).getResult().ordinal();			
		}
		return point;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	
}
