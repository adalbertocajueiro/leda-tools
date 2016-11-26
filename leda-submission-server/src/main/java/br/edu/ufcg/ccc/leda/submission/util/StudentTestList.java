package br.edu.ufcg.ccc.leda.submission.util;

import java.util.ArrayList;

import org.apache.commons.math3.ml.clustering.Clusterable;

import br.edu.ufcg.ccc.leda.util.TestCase;
import br.edu.ufcg.ccc.leda.util.TestResult;

public class StudentTestList implements Clusterable{

	private String matricula;
	private ArrayList<TestCase> listTestCases;
	
	public StudentTestList(String matricula) {
		super();
		this.matricula = matricula;
		listTestCases = new ArrayList<TestCase>(); 
	}

	@Override
	public double[] getPoint() {
		double[] point = new double[this.listTestCases.size()];

		for (int i = 0; i < this.listTestCases.size(); i++) {
			point[i] = this.listTestCases.get(i).getResult().ordinal();			
		}
		return point;
	}

	//vai ver se essa testlist possui os mesmos testes e na mesma ordem dos que tem na 
	//studenttest list base. A ideia é ir construindo uma nova student test list se eles 
	//tam tamanho diferentes e ir completando com as da base (mas essas novas indicando erro
	//de execucao).
	public void fixTestListIfNecessary(StudentTestList base){
		if(this.listTestCases.size() != base.getListTestCases().size()){
			ArrayList<TestCase> novaListaDeTestes = new ArrayList<TestCase>();
			base.getListTestCases().forEach( tc -> {
				TestCase existente = 
						this.getListTestCases().stream().filter( t -> t.equals(tc)).findFirst().orElse(null);
				if(existente != null){
					novaListaDeTestes.add(existente);
				}else{
					TestCase novo = new TestCase(tc.getTestCaseName(), tc.getTestCaseClass(), TestResult.ERROR_OR_FAILURE);
					novaListaDeTestes.add(novo);
				}
			});
			this.setListTestCases(novaListaDeTestes);
		}
	}
	
	public void add(TestCase tc){
		this.listTestCases.add(tc);
	}
	
	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	
	public ArrayList<TestCase> getListTestCases() {
		return listTestCases;
	}

	public void setListTestCases(ArrayList<TestCase> listTestCases) {
		this.listTestCases = listTestCases;
	}

	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if(o instanceof StudentTestList){
			result = this.matricula.equals(((StudentTestList) o).getMatricula());
		}
		return false;
	}

	
	
}
