package green.atm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;

import green.atm.extrato.Extrato;
import green.atm.extrato.ProcessadorExtratoSicoob;
import green.atm.extrato.Transacao;

public class TesteProcessadorExtrato {

	public static void main(String[] args) throws Exception {
		ProcessadorExtratoSicoob processador = new ProcessadorExtratoSicoob();
		//Extrato extrato = processador.construirExtrato("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (01-05-2019 19-37-03).pdf");
		//Extrato extrato = processador.construirExtrato("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (22-05-2019 16-38-48)$MAIO$.pdf");
		Extrato extrato = processador.construirExtrato("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (22-05-2019 16-40-48)$ABRIL$.pdf");
		
		
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
		
		File folder = new File(Util.FOLDER_CONFIG);
		File file = new File(folder,"transacoes.json");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write((new GsonBuilder().setPrettyPrinting().create()).toJson(extrato.getTransacoes()));
		bw.close();
		
		int i = 0;
	}

}
