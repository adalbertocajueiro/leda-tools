package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.util.GregorianCalendar;

public class Prova {

	private String provaId;
	private String descricao;
	private File arquivoAmbiente;
	private File arquivoProjetoCorrecao;
	private GregorianCalendar dataHoraLiberacao;
	private GregorianCalendar dataHoraLimiteEnvio;
	public Prova(String provaId, String descricao, File arquivoAmbiente,
			File arquivoProjetoCorrecao, GregorianCalendar dataHoraLiberacao,
			GregorianCalendar dataHoraLimiteEnvio) {
		super();
		this.provaId = provaId;
		this.descricao = descricao;
		this.arquivoAmbiente = arquivoAmbiente;
		this.arquivoProjetoCorrecao = arquivoProjetoCorrecao;
		this.dataHoraLiberacao = dataHoraLiberacao;
		this.dataHoraLimiteEnvio = dataHoraLimiteEnvio;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}	

	@Override
	public int hashCode() {
		return this.provaId.hashCode();
	}
	@Override
	public String toString() {
		if(this.dataHoraLiberacao != null){
			return this.provaId + " - " + Util.formatDate(this.dataHoraLiberacao);
		}else{
			return this.provaId;
		}
		
	}
	
	public String getProvaId() {
		return provaId;
	}
	public void setProvaId(String provaId) {
		this.provaId = provaId;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public File getArquivoAmbiente() {
		return arquivoAmbiente;
	}
	public void setArquivoAmbiente(File arquivoAmbiente) {
		this.arquivoAmbiente = arquivoAmbiente;
	}
	public File getArquivoProjetoCorrecao() {
		return arquivoProjetoCorrecao;
	}
	public void setArquivoProjetoCorrecao(File arquivoProjetoCorrecao) {
		this.arquivoProjetoCorrecao = arquivoProjetoCorrecao;
	}
	public GregorianCalendar getDataHoraLiberacao() {
		return dataHoraLiberacao;
	}
	public void setDataHoraLiberacao(GregorianCalendar dataHoraLiberacao) {
		this.dataHoraLiberacao = dataHoraLiberacao;
	}
	public GregorianCalendar getDataHoraLimiteEnvio() {
		return dataHoraLimiteEnvio;
	}
	public void setDataHoraLimiteEnvio(GregorianCalendar dataHoraLimiteEnvio) {
		this.dataHoraLimiteEnvio = dataHoraLimiteEnvio;
	}
	
	
	
}
