package green.atm.extrato;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Extrato {
	private GregorianCalendar dataInicial;
	private GregorianCalendar dataFinal;
	private double saldoAnterior;
	private double saldoMinimo;
	private double saldoMaximo;
	private ArrayList<Transacao> transacoes;
	
	public Extrato() {
		this.dataInicial = new GregorianCalendar();
		this.dataFinal = new GregorianCalendar();
		this.transacoes = new ArrayList<Transacao>();
	}
	
	public Extrato(GregorianCalendar dataInicial, GregorianCalendar dataFinal) {
		super();
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
		this.transacoes = new ArrayList<Transacao>();
	}
	
	public void calcularSaldosMinimoEMaximo() {
		//loop para encontrar o valor minimo do saldo atingido pela conta
		this.saldoMinimo = saldoAnterior;
		this.saldoMaximo = saldoAnterior;
		double saldoAtual = saldoAnterior;
		for (Transacao t : transacoes) {
			saldoAtual = saldoAtual + t.getValor();
			if (saldoAtual < this.saldoMinimo) {
				this.saldoMinimo = saldoAtual;
			}
			if (saldoAtual > this.saldoMaximo) {
				this.saldoMaximo = saldoAtual;
			}
		}
	}

	public GregorianCalendar getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(GregorianCalendar dataInicial) {
		this.dataInicial = dataInicial;
	}

	public GregorianCalendar getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(GregorianCalendar dataFinal) {
		this.dataFinal = dataFinal;
	}

	public double getSaldoMinimo() {
		return saldoMinimo;
	}

	public void setSaldoMinimo(double saldoMinimo) {
		this.saldoMinimo = saldoMinimo;
	}

	public double getSaldoMaximo() {
		return saldoMaximo;
	}

	public void setSaldoMaximo(double saldoMaximo) {
		this.saldoMaximo = saldoMaximo;
	}

	public ArrayList<Transacao> getTransacoes() {
		return transacoes;
	}

	public void setTransacoes(ArrayList<Transacao> transacoes) {
		this.transacoes = transacoes;
	}

	public double getSaldoAnterior() {
		return saldoAnterior;
	}

	public void setSaldoAnterior(double saldoAnterior) {
		this.saldoAnterior = saldoAnterior;
	}

	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY");
		return  
				"Extrato [dataInicial=" + df.format(dataInicial.getTime()) 
				+ ", dataFinal=" + df.format(dataFinal.getTime()) 
				+ ", saldoAnterior=" + saldoAnterior
				+ ", saldoMinimo=" + saldoMinimo 
				+ ", saldoMaximo=" + saldoMaximo + "]";
	}
	
	
}
