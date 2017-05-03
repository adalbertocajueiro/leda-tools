package br.edu.ufcg.ccc.leda.util;

import java.util.Arrays;

/**
 * Adequacao/observancia de codigo ao que foi pedido. Possui tres niveis: NENHUMA,PARCIAL,TOTAL. 
 * Isso impactará a nota dos testes apenas. NENHUMA faz a nota dos testes ser 0, independente
 * do que o aluno acertou. PARCIAL penaliza a nota dos testes em 50%, TOTAL não altera a nota 
 * dos testes.
 * @author Adalberto
 *
 */
public enum CodeAdequacy {
	BAIXA("Baixa"),MEDIA("Media"),ALTA("Alta");

	private String adequacao;

	CodeAdequacy(String adequacao) {
		this.adequacao = adequacao;
	}
	
	public String getAdequacao() {
		return adequacao;
	}

	public void setAdequacao(String adequacao) {
		this.adequacao = adequacao;
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(CodeAdequacy.values()));
		CodeAdequacy c = Enum.valueOf(CodeAdequacy.class,"BOM");
		System.out.println(c);
		int i = 0;
	}
}
