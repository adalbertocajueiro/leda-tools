package green.atm.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import green.atm.extrato.Transacao;

public class Util {
	public static final String FOLDER_CONFIG = "conf";
	public static final String MODALIDADES_FILE_NAME = "modalidades.json";
	public static final String TIPO_TRANSACAO_FILE_NAME = "tipoTransacao.json";
	public static final String FUNCIONARIOS_FILE_NAME = "funcionarios.txt";
	public static final String NOME_EMPRESA_SEGURANCA = "Optimus";
	public static final String NOME_ENERGIA = "Energia";
	
	public static final String TOKEN_MAIS_CONDOMINIO = "H F M BARROS";
	public static final String TOKEN_TERCEIRIZACAO_MAIS_CONDOMINIO = "Terceirizacao";
	
	public static final String TOKEN_SALARIO = "Salario";
	public static final String TOKEN_ADIANTAMENTO_SALARIAL = "Adiantamento salarial";
	
	public static final Pattern PATTERN_EXTRACAO_VALOR = Pattern.compile("[.]*([0-9]{1,3}[.])*[0-9]{1,3},[0-9]{2}[D[C]][.]*");
	public static final Pattern PATTERN_DATE = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}");
	public static final String TOKEN_FIM_MOVIMENTACOES= "RESUMO";
	public static final String TOKEN_PERIODO_EXTRATO = "PERÍODO:";
	public static final Pattern PATTERN_VALOR_CREDITO = Pattern.compile("([0-9]{1,3}[.])*[0-9]{1,3},[0-9]{2}[C]");
	public static final Pattern PATTERN_VALOR_DEBITO = Pattern.compile("([0-9]{1,3}[.])*[0-9]{1,3},[0-9]{2}[D]");
	public static final String SALDO_BLOQUEADO_ANTERIOR  = "SALDO BLOQ.ANTERIOR";
	public static final String SALDO_DO_DIA = "SALDO DO DIA";
	public static final String SALDO_ANTERIOR  = "SALDO ANTERIOR";
	public static final String TOKEN_DOC = "DOC.:";
	public static final String MODALIDADE_PGTO_RH = "DEB.TR.CT.DIF.TIT.";
	
	public static Transacao buildTransacao(String textoTransacao, int ano) {
		Transacao t = null;
		GregorianCalendar data = extrairData(textoTransacao.substring(0, textoTransacao.indexOf(" ")),ano);
		textoTransacao = textoTransacao.substring(textoTransacao.indexOf(" ")).trim(); //elimina a data extraida
		String numeroDOC = extrairDOC(textoTransacao);
		textoTransacao = textoTransacao.substring(0,textoTransacao.lastIndexOf("$")); //elimina a informacao do DOC
		double valor = 0.0;
		Matcher matcher = PATTERN_EXTRACAO_VALOR.matcher(textoTransacao);
		String[] camposTransacao = textoTransacao.split("[$]");
		String textoIdentificador = camposTransacao[0];
		String textoDescricao = "";
		if(matcher.find()) {
			String textoValor = matcher.group();
			textoIdentificador = textoIdentificador.substring(0, textoIdentificador.indexOf(textoValor)).trim();
			if(textoValor.endsWith("D")){
				textoValor = textoValor.replaceAll("[.]", "");
				textoValor = textoValor.replaceAll(",", ".");
				textoValor = textoValor.substring(0, textoValor.lastIndexOf('D'));
				valor = - Double.parseDouble(textoValor);
			}else {
				textoValor = textoValor.replaceAll("[.]", "");
				textoValor = textoValor.replaceAll(",", ".");
				textoValor = textoValor.substring(0, textoValor.lastIndexOf('C'));
				valor = Double.parseDouble(textoValor);
			}
			
			if(camposTransacao.length > 1) {
				for (int i = 1; i < camposTransacao.length; i++) {
					textoDescricao = textoDescricao + camposTransacao[i] + " - ";
				}
			}
		}
		
		
		t = new Transacao(data,textoIdentificador,valor,textoDescricao,numeroDOC);
		//precisa preencher as tags de tipo da transacao baseado nas informacoes dela
		///PPPPPP
		
		return t;
	}
	public static GregorianCalendar buildDate(String dataHora) {
		GregorianCalendar result = null;
		if(dataHora != null){
			result = new GregorianCalendar();
			//se estiver no formato errado retorna uma excecao
			//tem que fazer isso com string format provavelmente ou regex
			dataHora = dataHora.trim();
			if(PATTERN_DATE.matcher(dataHora).matches()){
				result.set(Calendar.DATE, Integer.parseInt(dataHora.substring(0, 2)));
				result.set(Calendar.MONTH, Integer.parseInt(dataHora.substring(3,5)) - 1); //janeiro eh considerado mes 0
				result.set(Calendar.YEAR, Integer.parseInt(dataHora.substring(6, 10)));
			}
			
		} 
		return result;
	}
	
	public static GregorianCalendar extrairData(String diaMes,int ano) {
		GregorianCalendar result = null;
		if(diaMes != null){
			result = new GregorianCalendar();
			int dia = Integer.parseInt(diaMes.substring(0, 2));
			int mes = Integer.parseInt(diaMes.substring(3));
			result.set(Calendar.DATE, dia);
			result.set(Calendar.MONTH, mes - 1); //mes vai de 0 ate 11
			result.set(Calendar.YEAR, ano);
		} 
		return result;
	}
	public static String extrairDOC(String textoTransacao) {

		return textoTransacao.substring(textoTransacao.indexOf("$DOC.:") + "$DOC.:".length()).trim();

	}
	public static final String TOKEN_INICIO_MOVIMENTACOES= "DATA HISTÓRICO VALOR";
}
