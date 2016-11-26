package br.edu.ufcg.ccc.leda.submission.util;

import java.util.List;

public class SimilarityMatrix {
	private List<StudentTestList> studentsTestList;
	private double[][] similarities;
	
	public SimilarityMatrix(List<StudentTestList> studentsTestList) {
		super();
		this.studentsTestList = studentsTestList;
		calculateSimilariries();
	}

	private void calculateSimilariries() {
		this.similarities = new double[studentsTestList.size()][studentsTestList.size()];
		int row = 0;
		int col = 0;
		SimilarityDistance function = new SimilarityDistance();
		
		//tem que procurar pela studentTestList que mamis testes tem e 
		//depois tem que consertar a outra com testes que nao passaram para
		//que cada estudante tenha a lista com a mesma quantidade de testes.
		StudentTestList maximo = studentsTestList.stream()
				.max((stl1,stl2) -> Integer.compare(stl1.getListTestCases().size(), stl2.getListTestCases().size()))
				.get();
		
		//agora seta todas as studentTestLists com 0 testes (erro de compilacao)
		//para ter a mesma quantidade de testes (e os que entraram em loop tambem)
		//todas as studenttestlist precisam ter os mesmos testes e nas mesmas posicoes
		this.studentsTestList.forEach(stl -> stl.fixTestListIfNecessary(maximo)); 
		
		for (StudentTestList testList : studentsTestList) {
			for (StudentTestList testList2 : studentsTestList) {
				similarities[row][col++] = function.compute(testList.getPoint(), testList2.getPoint());
			}
			col = 0;
			row++;
		}		
	}

	public List<StudentTestList> getStudentsTestList() {
		return studentsTestList;
	}

	public void setStudentsTestList(List<StudentTestList> studentsTestList) {
		this.studentsTestList = studentsTestList;
	}

	public double[][] getSimilarities() {
		return similarities;
	}

	public void setSimilarities(double[][] similarities) {
		this.similarities = similarities;
	}
	
}
