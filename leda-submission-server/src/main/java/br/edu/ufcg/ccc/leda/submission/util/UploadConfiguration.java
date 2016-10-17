package br.edu.ufcg.ccc.leda.submission.util;

public class UploadConfiguration {

	private String id;
	private String semestre;
	private String turma;
	

	public UploadConfiguration(String id, String semestre, String turma) {
		this.id = id;
		this.semestre = semestre;
		this.turma = id.trim().substring(4);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}