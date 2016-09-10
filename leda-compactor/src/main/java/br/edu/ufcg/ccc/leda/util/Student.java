package br.edu.ufcg.ccc.leda.util;

public class Student {

	private String matricula;
	private String nome;
	private String turma;
	public Student(String matricula, String nome, String turma) {
		super();
		this.matricula = matricula;
		this.nome = nome;
		this.turma = turma;
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
	public String getTurma() {
		return turma;
	}
	public void setTurma(String turma) {
		this.turma = turma;
	}
	@Override
	public String toString() {
		return this.nome;
	}
	
	
}
