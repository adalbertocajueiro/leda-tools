package green.atm.extrato;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;

import green.atm.util.Configuration;
import green.atm.util.OrderedLinkedList;
import green.atm.util.Util;

public class ControladorAtmospheraGreen {
	private ProcessadorExtrato processadorExtrato;

	public ControladorAtmospheraGreen() {
		processadorExtrato = new ProcessadorExtratoSicoob();
	}

	/**
	 * Carrega os extratos em todos os arquivos de extrato do Sicoob qu eestao em uma pasta especifica
	 * @param folder
	 */
	public void carregarTodosExtratos(File folder) throws Exception{
		if (folder.exists()) {
			File[] arquivos = folder.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().startsWith("Sicoob")
							&& pathname.getName().endsWith(".pdf");
				}
			});
			for (File file : arquivos) {
				System.out.println("Carregando extrato de " + file.getName());
				this.loadArquivoExtrato(file.getAbsolutePath());
			}
		}
	}
	
	public void loadArquivoExtrato(String path) throws Exception {
		Extrato extrato = processadorExtrato.construirExtrato(path);
		OrderedLinkedList<Transacao> transacoes = Configuration.getInstance().getTransacoes();
		// quando pega a transacao, preserva a classificacao que ja tinha sido salva
		// no json de transacoes antigas
		for (Transacao transacao : extrato.getTransacoes()) {
			transacoes.add(transacao);
		}
		
		//depois de carregar transacoes de arquivo de extrato precisa salvar novamente no json
		this.salvarTransacoes();
	}

	private void salvarTransacoes() throws IOException {
		File folder = new File(Util.FOLDER_CONFIG);
		File file = new File(folder,Util.TRANSACOES_FILE_NAME);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		OrderedLinkedList<Transacao> transacoes = Configuration.getInstance().getTransacoes();
		bw.write((new GsonBuilder().setPrettyPrinting().create()).toJson(transacoes));
		bw.close();
	}
	public double obterSaldoInicial(int mes, int ano) {
		double saldoInicial = 0.0;
		Stream<Transacao> stream = Configuration.getInstance().getTransacoes().stream()
				.filter(t -> t.getData().get(GregorianCalendar.MONTH) == (mes - 1))
				.filter(t -> t.getData().get(GregorianCalendar.YEAR) == ano);

		Transacao saldoCorrente = stream.findFirst().orElse(null);
		if (saldoCorrente != null) {
			saldoInicial = saldoCorrente.getValor();
		}
		return saldoInicial;
	}

	public double obterValorAplicado(int mes, int ano) {
		double valorAplicacao = 0.0;
		Stream<Transacao> stream = Configuration.getInstance().getTransacoes().stream()
				.filter(t -> t.getData().get(GregorianCalendar.MONTH) == (mes - 1))
				.filter(t -> t.getData().get(GregorianCalendar.YEAR) == ano)
				.filter(t -> t.getTipos().contains(TipoTransacao.VALOR_APLICACAO));
		Transacao valorAplicado = stream.findFirst().orElse(null);
		if (valorAplicado != null) {
			valorAplicacao = valorAplicado.getValor();
		}
		return valorAplicacao;
	}
	/**
	 * Mes precisa ser informado de 1 até 12
	 * 
	 * @param mes
	 * @param ano
	 * @return
	 */
	public double obterSaldoMinimo(int mes, int ano) {
		double saldoMinimo = 0.0;
		Stream<Transacao> stream = Configuration.getInstance().getTransacoes().stream()
				.filter(t -> t.getData().get(GregorianCalendar.MONTH) == (mes - 1))
				.filter(t -> t.getData().get(GregorianCalendar.YEAR) == ano);

		// pega o saldo corrente que esta sempre no inicio do stream

		double saldoAnterior = this.obterSaldoInicial(mes, ano);
		saldoMinimo = saldoAnterior;
		double saldoAtual = saldoAnterior;
		List<Transacao> list = stream.skip(1) // descarta o primeiro elemento porque foi o saldo atual
				.filter(t -> !t.getTipos().contains(TipoTransacao.VALOR_APLICACAO)) //remove a transacao que guarda valor aplicado
				.collect(Collectors.toList()); 
																			 
		for (Transacao t : list) {
			saldoAtual = saldoAtual + t.getValor();
			if (saldoAtual < saldoMinimo) {
				saldoMinimo = saldoAtual;
			}
		}

		return saldoMinimo;
	}

	/**
	 * Mes precisa ser informado de 1 até 12
	 * 
	 * @param mes
	 * @param ano
	 * @return
	 */
	public double obterSaldoMaximo(int mes, int ano) {
		double saldoMaximo = 0.0;
		Stream<Transacao> stream = Configuration.getInstance().getTransacoes().stream()
				.filter(t -> t.getData().get(GregorianCalendar.MONTH) == (mes - 1))
				.filter(t -> t.getData().get(GregorianCalendar.YEAR) == ano);

		// pega o saldo corrente que esta sempre no inicio do stream

		double saldoAnterior = this.obterSaldoInicial(mes, ano);
		saldoMaximo = saldoAnterior;
		double saldoAtual = saldoAnterior;
		List<Transacao> list = stream.skip(1) //descarta o primeiro elemento porque foi o saldo atual
				.filter(t -> !t.getTipos().contains(TipoTransacao.VALOR_APLICACAO)) //remove a transacao que guarda valor aplicado
				.collect(Collectors.toList());  
																			
		for (Transacao t : list) {
			saldoAtual = saldoAtual + t.getValor();
			if (saldoAtual > saldoMaximo) {
				saldoMaximo = saldoAtual;
			}
		}

		return saldoMaximo;
	}
	
	

	public double calcularReceitas(int mes, int ano) {
		double creditos = 0.0;
		creditos = Configuration.getInstance().getTransacoes().stream()
				.filter(t -> t.getData().get(GregorianCalendar.MONTH) == (mes - 1))
				.filter(t -> t.getData().get(GregorianCalendar.YEAR) == ano)
				.filter(t -> t.getValor() > 0.0)
				.skip(1) //descarta sempre a primeira porque é saldo corrente
				.filter(t -> !t.getTipos().contains(TipoTransacao.VALOR_APLICACAO))
				.mapToDouble(Transacao::getValor).sum();
		return creditos;
	}

	public double calcularDespesas(int mes, int ano) {
		double debitos = 0.0;
		debitos = Configuration.getInstance().getTransacoes().stream()
				.filter(t -> t.getData().get(GregorianCalendar.MONTH) == (mes - 1))
				.filter(t -> t.getData().get(GregorianCalendar.YEAR) == ano).filter(t -> t.getValor() < 0.0)
				.mapToDouble(Transacao::getValor).sum();
		return debitos;
	}
	public double calcularSaldoRestante(int mes, int ano) {
		return this.obterSaldoInicial(mes, ano) + this.calcularReceitas(mes, ano) + this.calcularDespesas(mes, ano);
	}
	
	public List<Transacao> filtrarTransacoes(int mes, int ano, TipoTransacao tipo){
		OrderedLinkedList<Transacao> transacoes = Configuration.getInstance().getTransacoes();
		return transacoes.stream()
		.filter(t -> t.getData().get(GregorianCalendar.MONTH) == (mes - 1))
		.filter(t -> t.getData().get(GregorianCalendar.YEAR) == ano)
		.filter(t -> t.getTipos().contains(tipo))
		.collect(Collectors.toList());
	} 
}
