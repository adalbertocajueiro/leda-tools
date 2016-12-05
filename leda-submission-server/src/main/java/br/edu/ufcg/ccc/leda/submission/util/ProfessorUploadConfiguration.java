package br.edu.ufcg.ccc.leda.submission.util;

public class ProfessorUploadConfiguration extends UploadConfiguration {

	private int numeroTurmas = 1;
	
	public ProfessorUploadConfiguration(String id, String semestre, String turma,
			int numeroTurmas) {
		
		super(id, semestre, turma);
		if(numeroTurmas > 1){
			this.numeroTurmas = numeroTurmas;
		}
	}
	public int getNumeroTurmas() {
		return numeroTurmas;
	}
	public void setNumeroTurmas(int numeroTurmas) {
		this.numeroTurmas = numeroTurmas;
	}
	
	

}
