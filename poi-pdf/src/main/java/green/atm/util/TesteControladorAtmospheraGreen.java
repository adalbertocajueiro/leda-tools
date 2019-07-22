package green.atm.util;

import static org.junit.Assume.assumeNoException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;

import green.atm.extrato.ControladorAtmospheraGreen;
import green.atm.extrato.TipoTransacao;
import green.atm.extrato.Transacao;


public class TesteControladorAtmospheraGreen {
	public static void main(String[] args) throws Exception {
		String extratoMaio = "/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (26-05-2019 16-13-30)$MAIO$.pdf";
		ControladorAtmospheraGreen controlador = new ControladorAtmospheraGreen();
		//controlador.loadArquivoExtrato(extratoMaio);
		//controlador.carregarTodosExtratos(new File("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/"));
		
		System.out.println("RESUMO: ");
		ArrayList<Double> tarifas = new ArrayList<Double>();
		
		for (int i = 1; i <= 12; i++) {
			System.out.println("Saldo inicial em " + i + "/2018: " + controlador.obterSaldoInicial(i, 2018));
			System.out.println("Saldo minimo em " + i + "/2018: " + controlador.obterSaldoMinimo(i, 2018));
			System.out.println("Saldo maximo em " + i + "/2018: " + controlador.obterSaldoMaximo(i, 2018));
			System.out.println("Receita em " + i + "/2018: " + controlador.calcularReceitas(i, 2018));
			System.out.println("Despesa em " + i + "/2018: " + controlador.calcularDespesas(i, 2018));
			System.out.println("Saldo restante em " + i + "/2018: " + controlador.calcularSaldoRestante(i, 2018));
			System.out.println("Valor aplicado em " + i + "/2018: " + controlador.obterValorAplicado(i, 2018));
			double valorTarifas = controlador.filtrarTransacoes(i, 2018, TipoTransacao.TARIFAS_BANCARIAS)
				.stream().mapToDouble(Transacao::getValor)
				.sum();
			tarifas.add(valorTarifas);
			System.out.println("Gasto com tarifa bancaria em " + i + "/2018: " + valorTarifas);
			System.out.println("");
		}
		
		for (int i = 1; i <= 5; i++) {
			System.out.println("Saldo inicial em " + i + "/2019: " + controlador.obterSaldoInicial(i, 2019));
			System.out.println("Saldo minimo em " + i + "/2019: " + controlador.obterSaldoMinimo(i, 2019));
			System.out.println("Saldo maximo em " + i + "/2019: " + controlador.obterSaldoMaximo(i, 2019));
			System.out.println("Receita em " + i + "/2019: " + controlador.calcularReceitas(i, 2019));
			System.out.println("Despesa em " + i + "/2019: " + controlador.calcularDespesas(i, 2019));
			System.out.println("Saldo restante em " + i + "/2019: " + controlador.calcularSaldoRestante(i, 2019));
			System.out.println("Valor aplicado em " + i + "/2019: " + controlador.obterValorAplicado(i, 2019));
			double valorTarifas = controlador.filtrarTransacoes(i, 2019, TipoTransacao.TARIFAS_BANCARIAS)
					.stream().mapToDouble(Transacao::getValor)
					.sum();
			tarifas.add(valorTarifas);
			System.out.println("Gasto com tarifa bancaria em " + i + "/2018: " + valorTarifas);
			System.out.println("");
		}
		
		System.out.println("Media das tarifas desde 01/2018: " + tarifas.stream().mapToDouble(d -> d).sum()/tarifas.size());
		/*System.out.println("Saldo inicial em 12/2018: " + controlador.obterSaldoInicial(12, 2018));
		System.out.println("Saldo minimo em 12/2018: " + controlador.obterSaldoMinimo(12, 2018));
		System.out.println("Saldo maximo em 12/2018: " + controlador.obterSaldoMaximo(12, 2018));
		System.out.println("Receita em 12/2018: " + controlador.calcularReceitas(12, 2018));
		System.out.println("Despesa em 12/2018: " + controlador.calcularDespesas(12, 2018));
		System.out.println("Saldo restante em 12/2018: " + controlador.calcularSaldoRestante(12, 2018));
		System.out.println("");
		System.out.println("Saldo inicial em 01/2019: " + controlador.obterSaldoInicial(1, 2019));
		System.out.println("Saldo minimo em 01/2019: " + controlador.obterSaldoMinimo(1, 2019));
		System.out.println("Saldo maximo em 01/2019: " + controlador.obterSaldoMaximo(1, 2019));
		System.out.println("Receita em 01/2019: " + controlador.calcularReceitas(1, 2019));
		System.out.println("Despesa em 01/2019: " + controlador.calcularDespesas(1, 2019));
		System.out.println("Saldo restante em 01/2019: " + controlador.calcularSaldoRestante(1, 2019));
		System.out.println("");
		System.out.println("Saldo inicial em 02/2019: " + controlador.obterSaldoInicial(2, 2019));
		System.out.println("Saldo minimo em 02/2019: " + controlador.obterSaldoMinimo(2, 2019));
		System.out.println("Saldo maximo em 02/2019: " + controlador.obterSaldoMaximo(2, 2019));
		System.out.println("Receita em 02/2019: " + controlador.calcularReceitas(2, 2019));
		System.out.println("Despesa em 02/2019: " + controlador.calcularDespesas(2, 2019));
		System.out.println("Saldo restante em 02/2019: " + controlador.calcularSaldoRestante(2, 2019));
		System.out.println("");
		System.out.println("Saldo inicial em 03/2019: " + controlador.obterSaldoInicial(3, 2019));
		System.out.println("Saldo minimo em 03/2019: " + controlador.obterSaldoMinimo(3, 2019));
		System.out.println("Saldo maximo em 03/2019: " + controlador.obterSaldoMaximo(3, 2019));
		System.out.println("Receita em 03/2019: " + controlador.calcularReceitas(3, 2019));
		System.out.println("Despesa em 03/2019: " + controlador.calcularDespesas(3, 2019));
		System.out.println("Saldo restante em 03/2019: " + controlador.calcularSaldoRestante(3, 2019));
		System.out.println("");
		System.out.println("Saldo inicial em 04/2019: " + controlador.obterSaldoInicial(4, 2019));
		System.out.println("Saldo minimo em 04/2019: " + controlador.obterSaldoMinimo(4, 2019));
		System.out.println("Saldo maximo em 04/2019: " + controlador.obterSaldoMaximo(4, 2019));
		System.out.println("Receita em 04/2019: " + controlador.calcularReceitas(4, 2019));
		System.out.println("Despesa em 04/2019: " + controlador.calcularDespesas(4, 2019));
		System.out.println("Saldo restante em 04/2019: " + controlador.calcularSaldoRestante(4, 2019));
		
		System.out.println("");
		System.out.println("Saldo inicial em 05/2019: " + controlador.obterSaldoInicial(5, 2019));
		System.out.println("Saldo minimo em 05/2019: " + controlador.obterSaldoMinimo(5, 2019));
		System.out.println("Saldo maximo em 05/2019: " + controlador.obterSaldoMaximo(5, 2019));
		System.out.println("Receita em 05/2019: " + controlador.calcularReceitas(5, 2019));
		System.out.println("Despesa em 05/2019: " + controlador.calcularDespesas(5, 2019));
		System.out.println("Saldo restante em 05/2019: " + controlador.calcularSaldoRestante(5, 2019));
		System.out.println("Valor aplicado em 05/2019: " + controlador.obterValorAplicado(5, 2019));*/
		
		/*double creditos = Configuration.getInstance().getTransacoes().stream()
				.filter(t -> t.getData().get(GregorianCalendar.MONTH) == 4)
				.filter(t -> t.getData().get(GregorianCalendar.YEAR) == 2019)
				.filter( t -> t.getValor() > 0.0)
				.mapToDouble(Transacao::getValor).sum();
		System.out.println("Creditos: " + creditos);
		double debitos = Configuration.getInstance().getTransacoes().stream()
				.filter(t -> t.getData().get(GregorianCalendar.MONTH) == 4)
				.filter(t -> t.getData().get(GregorianCalendar.YEAR) == 2019)
				.filter( t -> t.getValor() < 0.0)
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
		bw.close();*/
	}
}
