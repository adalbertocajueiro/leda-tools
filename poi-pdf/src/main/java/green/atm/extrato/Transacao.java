package green.atm.extrato;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

enum TipoTransacao {
	SALDO_CORRENTE("Saldo Corrente"), 

	RECEITA_OPERACIONAL("Receita Operacional"), 
	TAXA_CONDOMINIO("Taxas de Condominio"),
	TAXA_EXTRA("Taxas Extras"),
	TAXA_SALAO_FESTA("Taxas Salao de Festas"),
	MULTAS_JUROS("Multas e Juros"),
	TAXA_AGUA("Taxa de Agua"),

	RECUPERACAO_ATIVOS ("Recuperacao de Ativos"),
	MULTA_JURO_CORRECAO_COBRANCA("Multa, Juros e Correcao de Cobranca"),
	OUTRAS_RECEITAS("Outras receitas"),
	
	DESPESAS_PESSOAL("Despesas com pessoal"),
	TERCEIRIZACAO_FUNCIONARIOS("Terceirizacao de Funcionarios"),
	VIGILANCIA("Vigilancia"),
	SALARIO_FUNCIONARIOS_ORGANICOS("Salario dos funcionarios organicos"),
	ADIANTAMENTO_SALARIAL_FUNCIONARIOS_ORGANICOS("Adiantamento salarial dos funcionarios organicos"),
	AVISO_DE_FERIAS("Aviso de ferias"),
	INSS("INSS funcionarios e vigilancia"),
	FGTS("FGTS"),
	PIS("PIS"),
	ISS("ISS"),
	BENEFICIO_SOCIAL("Beneficio social dos funcionarios organicos"),
	OUTRAS_DESPESAS_PESSOAL("Outras despesas com pessoal"),
	
	DESPESAS_ADMINISTRATIVAS("Despesas Administrativas"),
	ENERGISA("Energisa"),
	CAGEPA("CAGEPA"),
	COMPRA_MATERIAL("Compra de material"),
	ADMINISTRACAO_CONDOMINIO("Administracao do condominio"),
	MANUTENCAO("Manutencao realizada"),
	ABASTECIMENTO("Abastecimentos"),
	SERVICOS_TERCEIROS("Servicos realizados por terceiros"),
	IRRF("IRRF"),
	TARIFAS_BANCARIAS("Tarifas e taxas bancarias"),
	OUTRAS_DESPESAS_ADMINISTRATIVAS("Outras despesas administrativas"),
	
	APLICACAO("Aplicacao");
	
	private String tipo;
	
	private TipoTransacao(String tipoTranscacao) {
		this.tipo = tipoTranscacao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
}
public class Transacao {
	private GregorianCalendar data;
	private String textoIdentificador;
	private double valor;
	private String descricao;
	private String numeroDOC;
	private ArrayList<TipoTransacao> tipos;
	
	public Transacao(GregorianCalendar data, String textoIdentificador, double valor, String descricao,
			String numeroDOC) {
		super();
		this.data = data;
		this.textoIdentificador = textoIdentificador;
		this.valor = valor;
		this.descricao = descricao;
		this.numeroDOC = numeroDOC;
		this.tipos = new ArrayList<TipoTransacao>();
		preencheTipos();
	}
	
	private void preencheTipos() {
		//baseado no identificador, valor, no tipo, etc ja classifica inicialmente uma transacao
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

	public ArrayList<TipoTransacao> getTipos() {
		return tipos;
	}

	public void setTipos(ArrayList<TipoTransacao> tipos) {
		this.tipos = tipos;
	}
	
	public static void main(String[] args) {
		
		TipoTransacao t = TipoTransacao.valueOf("SALDO_CORRENTE");
		System.out.println(TipoTransacao.SALDO_CORRENTE);
		System.out.println(t);
	}
}
