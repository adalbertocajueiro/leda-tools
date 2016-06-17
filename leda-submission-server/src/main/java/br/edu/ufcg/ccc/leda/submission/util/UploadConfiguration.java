package br.edu.ufcg.ccc.leda.submission.util;

/**
 * Classe que representa a configuracao de um upload, contendo informacoes sobre semestre, turma, 
 * matricula do aluno, etc. 
 * 
 * @author Adalberto
 *
 */
public class UploadConfiguration {

	private String matricula;
	private String semestre;
	private String turma;
	private String roteiro;

	
	public UploadConfiguration(String matricula, String semestre, String turma,
			String roteiro) {
		super();
		this.matricula = matricula;
		this.semestre = semestre;
		this.turma = turma;
		this.roteiro = roteiro;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
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
