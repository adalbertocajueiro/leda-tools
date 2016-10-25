package br.edu.ufcg.ccc.leda.submission.util;

import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

public abstract class Atividade {

	protected String id;
	protected String nome;
	protected String descricao;
	protected GregorianCalendar dataHora;
	protected List<LinkVideoAula> linksVideoAulas;
	private List<Monitor> monitores;
	
	public Atividade(String id, String nome, String descricao, 
			GregorianCalendar dataHora, List<LinkVideoAula> linksVideoAulas,
			List<Monitor> monitores) {
		super();
		this.id = id;
		this.nome = nome;
		this.descricao = descricao;
		this.dataHora = dataHora;
		this.linksVideoAulas = linksVideoAulas;
		this.monitores = monitores;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}	

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	
	
	@Override
	public String toString() {
		return "Atividade " + id + " (" + nome + ")";
	}

	public String getTurma(){
		return this.id.substring(4);
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public GregorianCalendar getDataHora() {
		return dataHora;
	}

	public void setDataHora(GregorianCalendar dataHora) {
		this.dataHora = dataHora;
	}

	
	public List<LinkVideoAula> getLinksVideoAulas() {
		return linksVideoAulas;
	}

	public void setLinksVideoAulas(List<LinkVideoAula> linksVideoAulas) {
		this.linksVideoAulas = linksVideoAulas;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Monitor> getMonitores() {
		return monitores;
	}

	public void setMonitores(List<Monitor> monitores) {
		this.monitores = monitores;
	}
	
	
}
