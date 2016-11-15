package br.edu.ufcg.ccc.leda.util;

public class CorrectionReportItem {
	private String matricula;
	private String comentario = "";
	
	public CorrectionReportItem(String matricula, String comentario) {
		super();
		this.matricula = matricula;
		this.comentario = comentario;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	
	
	
	
}
