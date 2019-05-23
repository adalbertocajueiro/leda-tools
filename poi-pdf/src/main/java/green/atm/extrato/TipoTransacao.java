package green.atm.extrato;

public enum TipoTransacao {
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
