package green.atm.extrato;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import green.atm.util.OrderedLinkedList;
import green.atm.util.Util;

public class ProcessadorExtratoSicoob implements ProcessadorExtrato{

	
	
	@Override
	public Extrato construirExtrato(String path) throws Exception {
		Extrato extrato = new Extrato();
		
		String conteudo = carregarConteudoPDF(path);
		String[] periodo = extrairPeriodoExtratoStrings(conteudo);
		GregorianCalendar dataInicial = Util.buildDate(periodo[0]);
		GregorianCalendar dataFinal = Util.buildDate(periodo[1]);
		double saldoAnterior = extrairSaldoAnterior(conteudo);
		double valorAplicacao = extrairValorAplicado(conteudo);
		
		ArrayList<String> list = removerInformacoesInuteis(conteudo);

		OrderedLinkedList<Transacao> transacoes = 
				construirListaTransacoes(saldoAnterior, list, dataFinal.get(Calendar.YEAR));
		extrato.setDataInicial(dataInicial);
		extrato.setDataFinal(dataFinal);
		extrato.setSaldoAnterior(saldoAnterior);
		extrato.setTransacoes(transacoes);
		extrato.calcularSaldosMinimoEMaximo();
		// acrescenta uma transacao inicial especifica somente para informar o saldo
		Transacao saldoCorrente = new Transacao(dataInicial,Util.TOKEN_SALDO_CORRENTE,
						saldoAnterior,"Saldo inicial do mes","00000000");
		saldoCorrente.getTipos().add(TipoTransacao.SALDO_CORRENTE);
		
		//esse tipo de transacao tem sempre esse numero de DOC
		extrato.getTransacoes().addFirst(saldoCorrente);
		
		Transacao valorAplicado = new Transacao(dataInicial,Util.TOKEN_RDC,
				valorAplicacao,"Valor aplicacao no mes","11111111");
		valorAplicado.getTipos().add(TipoTransacao.VALOR_APLICACAO);
		extrato.getTransacoes().add(valorAplicado);
		
		return extrato;
	}

	private double extrairValorAplicado(String content) {
		double valor = 0.0;
		String textoValor = "";
		int indexValorAplicado = content.indexOf(Util.TOKEN_RDC);
		textoValor = content.substring(indexValorAplicado + Util.TOKEN_RDC.length() + 1,content.indexOf('\n', indexValorAplicado)).trim();
		textoValor = textoValor.replaceAll("[.]", "");
		textoValor = textoValor.replaceAll(",", ".");
		
		if(textoValor.endsWith("D")){
			textoValor = textoValor.substring(0, textoValor.lastIndexOf('D'));
			valor = - Double.parseDouble(textoValor);
		}else {
			textoValor = textoValor.substring(0, textoValor.lastIndexOf('C'));
			valor = Double.parseDouble(textoValor);

		}
		return valor;
	}

	private String carregarConteudoPDF(String path) throws IOException, SAXException, TikaException{
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = new FileInputStream(path);
		ParseContext pcontext = new ParseContext();
		PDFParser pdfparser = new PDFParser();
		pdfparser.parse(inputstream, handler, metadata, pcontext);
		String content = handler.toString();
		content = content.replaceAll("\n\n\n\n", "\n"); //remover quebra de pagina
		content = content.replaceAll("\n\n", "\n"); //remover linhas duplas
		
		return content;
	}
	
	private OrderedLinkedList<Transacao> construirListaTransacoes(double saldoAnterior, ArrayList<String> list, int ano) {
		ArrayList<String> transacoes = new ArrayList<String>();
		String transacao = "";
		for (String string : list) {
			if(string.startsWith(Util.TOKEN_DOC)) {
				transacao = transacao + string;
				transacoes.add(transacao);
				transacao = "";
			} else {
				transacao = transacao + string + "$";
			}
		}
		//transacoes.forEach(t -> System.out.println(t));
		
		OrderedLinkedList<Transacao> transacoesObj = new OrderedLinkedList<Transacao>();
		transacoes.stream().forEach( t -> transacoesObj.add(Util.buildTransacao(t, ano)));
		
		
		return transacoesObj;
	}

	private ArrayList<String> removerInformacoesInuteis(String content) {
		ArrayList<String> list = new ArrayList<String>();
		content = removerTextoResumoInicial(content);
		content = removerTextoResumoFinal(content);

		String[]  text = content.split("\n");

		for (String string : text) {
			list.add(string);
		}
		
		//FILTRA A LISTA PARA EXTRAIR INFORMACAO DE SALDO ANTERIOR, SALDO BLOQUEADO ANTERIOR e SALDO DO DIA
		list = (ArrayList<String>) list.stream().
				filter(s -> !(s.contains(Util.SALDO_ANTERIOR) 								
						|| s.contains(Util.SALDO_BLOQUEADO_ANTERIOR) 
						|| s.contains(Util.SALDO_DO_DIA))).
				collect(Collectors.toList());

		return list;
	}
	private String[] extrairPeriodoExtratoStrings(String content) {
		int indexInicio = content.indexOf(Util.TOKEN_PERIODO_EXTRATO) + Util.TOKEN_PERIODO_EXTRATO.length();
		int indexFim = content.indexOf('\n', indexInicio);
		String[] datas = content.substring(indexInicio, indexFim).split("-");
		
		return datas;
	}

	private double extrairSaldoAnterior(String content) {
		double valor = 0.0;
		String textoValor = "";
		int indexSaldoAnterior = content.indexOf(Util.SALDO_ANTERIOR);
		textoValor = content.substring(indexSaldoAnterior + Util.SALDO_ANTERIOR.length() + 1,content.indexOf('\n', indexSaldoAnterior)).trim();
		textoValor = textoValor.replaceAll("[.]", "");
		textoValor = textoValor.replaceAll(",", ".");
		
		if(textoValor.endsWith("D")){
			textoValor = textoValor.substring(0, textoValor.lastIndexOf('D'));
			valor = - Double.parseDouble(textoValor);
		}else {
			textoValor = textoValor.substring(0, textoValor.lastIndexOf('C'));
			valor = Double.parseDouble(textoValor);

		}
		return valor;
	}
	private String removerTextoResumoInicial(String content) {
		String result = content;
		result = content.substring(content.indexOf(Util.TOKEN_INICIO_MOVIMENTACOES) + Util.TOKEN_INICIO_MOVIMENTACOES.length() + 1);
		return result;
	}
	private String removerTextoResumoFinal(String content) {
		String result = content;
		result = content.substring(0,content.indexOf(Util.TOKEN_FIM_MOVIMENTACOES));
		return result;
	}
	
	
}
