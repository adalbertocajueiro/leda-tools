package br.edu.ufcg.ccc.leda.submission.util;

public class UploadConfiguration {

	private String semestre;
	private String turma;
	private String roteiro;

	public UploadConfiguration(String semestre, String turma, String roteiro) {
		super();
		this.semestre = semestre;
		this.turma = turma;
		this.roteiro = roteiro;
	}

	public String getSemestre() {
		return semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	public String getTurma() {
		return turma;
	}

	public void setTurma(String turma) {
		this.turma = turma;
	}

	public String getRoteiro() {
		return roteiro;
	}

	public void setRoteiro(String roteiro) {
		this.roteiro = roteiro;
	}

}