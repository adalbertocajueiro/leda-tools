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
	private String ip;
	
	public StudentUploadConfiguration(String semestre, String turma,
			String roteiro,String matricula,String ip) {

		super(semestre,turma,roteiro);
		
		this.matricula = matricula;
		this.ip = ip;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}	
	
}
