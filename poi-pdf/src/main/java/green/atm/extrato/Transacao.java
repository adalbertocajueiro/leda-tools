package green.atm.extrato;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

import green.atm.util.Configuration;
import green.atm.util.Util;

public class Transacao {
	private GregorianCalendar data;
	private String textoIdentificador;
	private double valor;
	private String descricao;
	private String numeroDOC;
	private LinkedList<TipoTransacao> tipos;

	public Transacao(GregorianCalendar data, String textoIdentificador, double valor, String descricao,
			String numeroDOC) {
		super();
		this.data = data;
		this.textoIdentificador = textoIdentificador;
		this.valor = valor;
		this.descricao = descricao;
		this.numeroDOC = numeroDOC;
		this.tipos = new LinkedList<TipoTransacao>();
		preencheTipos();
	}

	private void preencheTipos() {
		// baseado no identificador, valor, no tipo, etc ja classifica inicialmente uma
		// transacao
		HashMap<String, TipoTransacao> tiposTransacao = Configuration.getInstance().getTiposTransacao();
		Object tipo = (tiposTransacao.get(this.textoIdentificador));
		// se descricao contiver nome da optimus entao entra como despesa pessoal e
		// vigilancia
		if (this.descricao.contains(Util.NOME_EMPRESA_SEGURANCA) || this.descricao.contains(Util.NOME_EMPRESA_SEGURANCA.toUpperCase())) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.VIGILANCIA);
		}
		if (this.descricao.contains(Util.TOKEN_MAIS_CONDOMINIO) && this.descricao.contains(Util.TOKEN_TERCEIRIZACAO_MAIS_CONDOMINIO)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.TERCEIRIZACAO_FUNCIONARIOS);
		}
		if (this.descricao.contains(Util.TOKEN_MAIS_CONDOMINIO) && !this.descricao.contains(Util.TOKEN_TERCEIRIZACAO_MAIS_CONDOMINIO)) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.ADMINISTRACAO_CONDOMINIO);
		}
		// se descricao contem nome de algum trabalhador entao eh despesa com pessoal
		if (this.descricao.contains(Util.TOKEN_SALARIO)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.SALARIO_FUNCIONARIOS_ORGANICOS);
		}
		if (this.descricao.contains(Util.TOKEN_ADIANTAMENTO_SALARIAL)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.ADIANTAMENTO_SALARIAL_FUNCIONARIOS_ORGANICOS);
		}
		if (tipo == null) {

		} else {

		} // nao tem mapeamento inicial entao é calssificado como outros

	}

	@Override
	public String toString() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY");

		return df.format(this.data.getTime()) + " - " + this.valor + " - " + this.numeroDOC;
	}

	public GregorianCalendar getData() {
		return data;
	}

	public void setData(GregorianCalendar data) {
		this.data = data;
	}

	public String getTextoIdentificador() {
		return textoIdentificador;
	}

	public void setTextoIdentificador(String textoIdentificador) {
		this.textoIdentificador = textoIdentificador;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNumeroDOC() {
		return numeroDOC;
	}

	public void setNumeroDOC(String numeroDOC) {
		this.numeroDOC = numeroDOC;
	}

	public LinkedList<TipoTransacao> getTipos() {
		return tipos;
	}

	public void setTipos(LinkedList<TipoTransacao> tipos) {
		this.tipos = tipos;
	}

	public static void main(String[] args) {

		TipoTransacao t = TipoTransacao.valueOf("SALDO_CORRENTE");
		System.out.println(TipoTransacao.SALDO_CORRENTE);
		System.out.println(t);
	}
}
