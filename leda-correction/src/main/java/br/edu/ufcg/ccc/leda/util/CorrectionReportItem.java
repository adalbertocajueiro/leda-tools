package br.edu.ufcg.ccc.leda.util;

public class CorrectionReportItem {
	private String matricula;
	private String comentatio = "";
	
	public CorrectionReportItem(String matricula, String comentatio) {
		super();
		this.matricula = matricula;
		this.comentatio = comentatio;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getComentatio() {
		return comentatio;
	}

	public void setComentatio(String comentatio) {
		this.comentatio = comentatio;
	}
	
	
	
}
