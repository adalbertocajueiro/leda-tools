package br.edu.ufcg.ccc.leda.submission.util;

/**
 * Classe que representa a configuracao de um upload, contendo informacoes sobre semestre, turma, 
 * matricula do aluno, etc. 
 * 
 * @author Adalberto
 *
 */
public class StudentUploadConfiguration extends UploadConfiguration {

	private String matricula;
	
	public StudentUploadConfiguration(String semestre, String turma,
			String roteiro,String matricula) {

		super(semestre,turma,roteiro);
		
		this.matricula = matricula;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}	

}
