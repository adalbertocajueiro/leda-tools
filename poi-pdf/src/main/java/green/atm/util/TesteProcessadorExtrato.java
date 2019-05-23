package green.atm.util;

import java.util.stream.Collectors;

import green.atm.extrato.Extrato;
import green.atm.extrato.ProcessadorExtratoSicoob;
import green.atm.extrato.Transacao;

public class TesteProcessadorExtrato {

	public static void main(String[] args) throws Exception {
		ProcessadorExtratoSicoob processador = new ProcessadorExtratoSicoob();
		//Extrato extrato = processador.construirExtrato("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (01-05-2019 19-37-03).pdf");
		Extrato extrato = processador.construirExtrato("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (20-05-2019 16-52-42)$MAIO$.pdf");
		
		
		System.out.println("RESUMO: ");
		double creditos = extrato.getTransacoes().stream().filter( t -> t.getValor() > 0.0)
				.mapToDouble(Transacao::getValor).sum();
		System.out.println("Creditos: " + creditos);
		double debitos = extrato.getTransacoes().stream().filter( t -> t.getValor() < 0.0)
				.mapToDouble(Transacao::getValor).sum();
		
		System.out.println("Debitos: " + debitos);
		
		System.out.println("Saldo anterior: " + extrato.getSaldoAnterior());
		System.out.println("Saldo maximo: " + extrato.getSaldoMaximo());
		System.out.println("Saldo minimo: " + extrato.getSaldoMinimo());
		System.out.println("Saldo restante: " + (extrato.getSaldoAnterior() + creditos + debitos));
		System.out.println("Transacoes agrupadas (modalidade - quantidade - valor): ");
		extrato.getTransacoes().stream().collect(Collectors.groupingBy(Transacao::getTextoIdentificador))
		.forEach((s,l) -> System.out.println(s + " - " + l.size() + " - " + l.stream().mapToDouble( t -> t.getValor()).sum() ));
		
		int i = 0;
	}

}
