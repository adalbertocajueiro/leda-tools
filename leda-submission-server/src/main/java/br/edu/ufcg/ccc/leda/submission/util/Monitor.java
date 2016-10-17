package br.edu.ufcg.ccc.leda.submission.util;

public class Monitor {
	private String matricula;
	private String nome;
	private String email;
	private String fone;
	public Monitor(String matricula, String nome, String email, String fone) {
		super();
		this.matricula = matricula;
		this.nome = nome;
		this.email = email;
		this.fone = fone;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFone() {
		return fone;
	}
	public void setFone(String fone) {
		this.fone = fone;
	}
	
	
	
	
}
