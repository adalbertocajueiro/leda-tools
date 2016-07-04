package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Roteiro {

	private String id;
	private String descricao;
	private File arquivoAmbiente;
	private File arquivoProjetoCorrecao;
	
	private GregorianCalendar dataHoraLiberacao;
	private GregorianCalendar dataHoraLimiteEnvioNormal;
	private GregorianCalendar dataHoraLimiteEnvioAtraso;
	
	private String monitorCorretor;
	private GregorianCalendar dataInicioCorrecao;
	private GregorianCalendar dataLimiteCorrecao;
	
	public static final int HORAS_LIMITE_NORMAL = 12;
	public static final int HORAS_LIMITE_ATRASO = 48;
	
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
	private Roteiro(String id, String descricao, File arquivoAmbiente,
			File arquivoProjetoCorrecao, GregorianCalendar dataHoraLiberacao) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.arquivoAmbiente = arquivoAmbiente;
		this.arquivoProjetoCorrecao = arquivoProjetoCorrecao;
		this.dataHoraLiberacao = dataHoraLiberacao;
		this.dataHoraLimiteEnvioNormal = (GregorianCalendar)dataHoraLiberacao.clone();
		this.dataHoraLimiteEnvioNormal.add(Calendar.HOUR,HORAS_LIMITE_NORMAL);
		this.dataHoraLimiteEnvioAtraso = (GregorianCalendar)dataHoraLiberacao.clone();
		this.dataHoraLimiteEnvioAtraso.add(Calendar.HOUR,HORAS_LIMITE_ATRASO);
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
	public Roteiro(String id, String descricao, GregorianCalendar dataHoraLiberacao,
			GregorianCalendar dataHoraLimiteEnvioNormal,
			GregorianCalendar dataHoraLimiteEnvioAtraso,
			String monitorCorretor, GregorianCalendar dataInicioCorrecao,
			GregorianCalendar dataLimiteCorrecao, File arquivoAmbiente,
			File arquivoProjetoCorrecao) {

		this.id = id;
		this.descricao = descricao;
		this.arquivoAmbiente = arquivoAmbiente;
		this.arquivoProjetoCorrecao = arquivoProjetoCorrecao;
		this.dataHoraLiberacao = dataHoraLiberacao;
		this.monitorCorretor = monitorCorretor;
		this.dataInicioCorrecao = dataInicioCorrecao;
		this.dataLimiteCorrecao = dataLimiteCorrecao;
		
		if(dataHoraLimiteEnvioNormal == null){
			if(dataHoraLiberacao != null){
				this.dataHoraLimiteEnvioNormal = new GregorianCalendar();
				this.dataHoraLimiteEnvioNormal.setTimeInMillis(dataHoraLiberacao.getTimeInMillis());
				this.dataHoraLimiteEnvioNormal.add(Calendar.HOUR,HORAS_LIMITE_NORMAL);
				this.dataHoraLimiteEnvioAtraso = new GregorianCalendar();
				this.dataHoraLimiteEnvioAtraso.setTimeInMillis(dataHoraLiberacao.getTimeInMillis());
				this.dataHoraLimiteEnvioAtraso.add(Calendar.HOUR,HORAS_LIMITE_ATRASO);
			}
		} else{
			this.dataHoraLimiteEnvioNormal = dataHoraLimiteEnvioNormal;
			this.dataHoraLimiteEnvioAtraso = dataHoraLimiteEnvioAtraso;
		}
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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if(this.dataHoraLiberacao != null){
			formatter.setCalendar(this.dataHoraLiberacao);
			return this.id + " - " + formatter.format(this.dataHoraLiberacao.getTime());
		}else{
			return this.id;
		}
		
	}
}
