package green.atm.extrato;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import green.atm.util.Util;

public class Transacao implements Comparable<Transacao>{
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

		//DESPESAS COM PESSOAL
		// se descricao contiver nome da optimus entao entra como despesa pessoal e
		// vigilancia
		if (this.descricao.contains(Util.TOKEN_EMPRESA_SEGURANCA) || this.descricao.contains(Util.TOKEN_EMPRESA_SEGURANCA.toUpperCase())) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.VIGILANCIA);
		} else if (this.descricao.contains(Util.TOKEN_MAIS_CONDOMINIO) && this.descricao.contains(Util.TOKEN_TERCEIRIZACAO_MAIS_CONDOMINIO)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.TERCEIRIZACAO_FUNCIONARIOS);
		} else if (this.descricao.contains(Util.TOKEN_SALARIO)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.SALARIO_FUNCIONARIOS_ORGANICOS);
		} else if (this.descricao.contains(Util.TOKEN_ADIANTAMENTO_SALARIAL)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.ADIANTAMENTO_SALARIAL_FUNCIONARIOS_ORGANICOS);
		} else if (this.descricao.contains(Util.TOKEN_FERIAS)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.FERIAS);
		} else if (this.descricao.contains(Util.TOKEN_BENEFICIO_SOCIAL)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.BENEFICIO_SOCIAL);
		} else if (this.textoIdentificador.equals(Util.TOKEN_FGTS)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.FGTS);
		} else if (this.textoIdentificador.equals(Util.TOKEN_INSS)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.INSS);
		} else if (this.textoIdentificador.equals(Util.TOKEN_IRPF)) {
			this.tipos.add(TipoTransacao.DESPESAS_PESSOAL);
			this.tipos.add(TipoTransacao.IRPF);
		}
		//DESPESAS ADMINISTRATIVAS
		else if (this.textoIdentificador.equals(Util.TOKEN_CONVENIO_SANEAMENTO)) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.CAGEPA);
		} else if (this.textoIdentificador.contains(Util.TOKEN_TARIFA)) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.TARIFAS_BANCARIAS);
		} else if (this.descricao.contains(Util.TOKEN_ENERGIA)) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.ENERGISA);
		} else if (this.descricao.contains(Util.TOKEN_MAIS_CONDOMINIO) && !this.descricao.contains(Util.TOKEN_TERCEIRIZACAO_MAIS_CONDOMINIO)) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.ADMINISTRACAO_CONDOMINIO);
		} else if (this.descricao.contains(Util.TOKEN_COMPRA) ) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.COMPRA);
		} else if (this.descricao.contains(Util.TOKEN_MANUTENCAO) ) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.MANUTENCAO);
		} else if (this.descricao.contains(Util.TOKEN_ATM) ) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.SERVICOS_TERCEIROS);
		} else if (this.descricao.contains(Util.TOKEN_ABASTECIMENTO) ) {
			this.tipos.add(TipoTransacao.DESPESAS_ADMINISTRATIVAS);
			this.tipos.add(TipoTransacao.ABASTECIMENTO);
		} else if (this.valor >= 0) {
			this.tipos.add(TipoTransacao.OUTRAS_RECEITAS);
		} else if (this.valor < 0) {
			this.tipos.add(TipoTransacao.OUTROS);
		}

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

	@Override
	public int compareTo(Transacao o) {
		
		int diff = this.data.get(GregorianCalendar.YEAR) - o.data.get(GregorianCalendar.YEAR);
		int result = diff;
		if (diff == 0) {
			diff = this.data.get(GregorianCalendar.MONTH) - o.data.get(GregorianCalendar.MONTH);
			result = diff;
			if(diff == 0) {
				diff = this.data.get(GregorianCalendar.DATE) - o.data.get(GregorianCalendar.DATE);
				result = diff;
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if(obj instanceof Transacao) {
			result = this.data.get(GregorianCalendar.YEAR) == ((Transacao) obj).getData().get(GregorianCalendar.YEAR)
					&& this.data.get(GregorianCalendar.MONTH) == ((Transacao) obj).getData().get(GregorianCalendar.MONTH)
					&& this.data.get(GregorianCalendar.DATE) == ((Transacao) obj).getData().get(GregorianCalendar.DATE)
					&& this.valor == ((Transacao) obj).getValor()
					&& this.numeroDOC.equals(((Transacao) obj).getNumeroDOC());
					
		}
		return result;
	}
	
	
}
