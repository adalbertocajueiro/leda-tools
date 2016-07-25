package br.edu.ufcg.ccc.leda.submission.util;

public class ProfessorUploadConfiguration extends UploadConfiguration {

	private int numeroTurmas = 1;
	public ProfessorUploadConfiguration(String semestre, String turma,
			String roteiro, int numeroTurmas) {
		
		super(semestre, turma, roteiro);
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
