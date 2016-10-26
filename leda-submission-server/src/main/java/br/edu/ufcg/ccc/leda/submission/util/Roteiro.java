package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Roteiro extends Atividade{

	private File arquivoAmbiente;
	private File arquivoProjetoCorrecao;
	
	private GregorianCalendar dataHoraLimiteEnvioNormal;
	private GregorianCalendar dataHoraLimiteEnvioAtraso;
	
	private Monitor corretor;
	private GregorianCalendar dataInicioCorrecao;
	private GregorianCalendar dataLimiteCorrecao;
	
	/**
	 * Construtor que acrescenta automaticamente certa quantidade de horas para determinar
	 * horarios limites de envio normal e com atraso.
	 * 
	 * @param id
	 * @param descricao
	 * @param environment
	 * @param correctionProj
	 * @param dataHoraLiberacao
	 */
	private Roteiro(String id, String nome, String descricao, 
			GregorianCalendar dataHoraLiberacao, List<LinkVideoAula> linksVideoAulas, 
			List<Monitor> monitores, File arquivoAmbiente, File arquivoProjetoCorrecao) {
		super(id,nome,descricao,dataHoraLiberacao,linksVideoAulas,monitores);
		this.arquivoAmbiente = arquivoAmbiente;
		this.arquivoProjetoCorrecao = arquivoProjetoCorrecao;
		this.dataHoraLimiteEnvioNormal = (GregorianCalendar)dataHoraLiberacao.clone();
		this.dataHoraLimiteEnvioNormal.add(Calendar.HOUR,Constants.HORAS_LIMITE_NORMAL);
		this.dataHoraLimiteEnvioAtraso = (GregorianCalendar)dataHoraLiberacao.clone();
		this.dataHoraLimiteEnvioAtraso.add(Calendar.HOUR,Constants.HORAS_LIMITE_ATRASO);
	}
	
	/**
	 * Construtor que permite especificamente setar as datas e horas limites de envio normal 
	 * e com atraso.
	 * 
	 * @param id
	 * @param descricao
	 * @param environment
	 * @param correctionProj
	 * @param dataHoraLiberacao
	 * @param dataHoraLimiteEnvioNormal
	 * @param dataHoraLimiteEnvioAtraso
	 */
	public Roteiro(String id, String nome, String descricao, 
			GregorianCalendar dataHoraLiberacao, List<LinkVideoAula> linksVideoAulas,
			GregorianCalendar dataHoraLimiteEnvioNormal,
			GregorianCalendar dataHoraLimiteEnvioAtraso,
			List<Monitor> monitores, Monitor monitorCorretor, 
			GregorianCalendar dataInicioCorrecao, GregorianCalendar dataLimiteCorrecao, 
			File arquivoAmbiente, File arquivoProjetoCorrecao) {

		this(id,nome,descricao,dataHoraLiberacao,
				linksVideoAulas,monitores, 
				arquivoAmbiente,arquivoProjetoCorrecao);
		this.corretor = monitorCorretor;
		this.dataInicioCorrecao = dataInicioCorrecao;
		this.dataLimiteCorrecao = dataLimiteCorrecao;
		
		if(dataHoraLimiteEnvioNormal == null){
			if(dataHoraLiberacao != null){
				this.dataHoraLimiteEnvioNormal = new GregorianCalendar();
				this.dataHoraLimiteEnvioNormal.setTimeInMillis(dataHoraLiberacao.getTimeInMillis());
				this.dataHoraLimiteEnvioNormal.add(Calendar.HOUR,Constants.HORAS_LIMITE_NORMAL);
				this.dataHoraLimiteEnvioAtraso = new GregorianCalendar();
				this.dataHoraLimiteEnvioAtraso.setTimeInMillis(dataHoraLiberacao.getTimeInMillis());
				this.dataHoraLimiteEnvioAtraso.add(Calendar.HOUR,Constants.HORAS_LIMITE_ATRASO);
			}
		} else{
			this.dataHoraLimiteEnvioNormal = dataHoraLimiteEnvioNormal;
			this.dataHoraLimiteEnvioAtraso = dataHoraLimiteEnvioAtraso;
		}
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("ID: " + this.id + " | ");
		result.append("Nome: " + this.nome + " | ");
		result.append("Data liberacao: " + Util.formatDate(this.dataHora) + " | ");
		if(this.dataHoraLimiteEnvioAtraso != null){
			result.append("Data limite envio: " + Util.formatDate(this.dataHoraLimiteEnvioAtraso) + "\n");
		}
		if(this.arquivoAmbiente != null && this.arquivoAmbiente.exists()){
			result.append("Ambiente: " + this.arquivoAmbiente.getName() + "\n");
		}

		return result.toString();
		
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

	public GregorianCalendar getDataHoraLimiteEnvioNormal() {
		return dataHoraLimiteEnvioNormal;
	}

	public void setDataHoraLimiteEnvioNormal(GregorianCalendar dataHoraLimiteEnvioNormal) {
		this.dataHoraLimiteEnvioNormal = dataHoraLimiteEnvioNormal;
	}

	public GregorianCalendar getDataHoraLimiteEnvioAtraso() {
		return dataHoraLimiteEnvioAtraso;
	}

	public void setDataHoraLimiteEnvioAtraso(GregorianCalendar dataHoraLimiteEnvioAtraso) {
		this.dataHoraLimiteEnvioAtraso = dataHoraLimiteEnvioAtraso;
	}

	
	public Monitor getCorretor() {
		return corretor;
	}

	public void setCorretor(Monitor corretor) {
		this.corretor = corretor;
	}

	public GregorianCalendar getDataInicioCorrecao() {
		return dataInicioCorrecao;
	}

	public void setDataInicioCorrecao(GregorianCalendar dataInicioCorrecao) {
		this.dataInicioCorrecao = dataInicioCorrecao;
	}

	public GregorianCalendar getDataLimiteCorrecao() {
		return dataLimiteCorrecao;
	}

	public void setDataLimiteCorrecao(GregorianCalendar dataLimiteCorrecao) {
		this.dataLimiteCorrecao = dataLimiteCorrecao;
	}

}
