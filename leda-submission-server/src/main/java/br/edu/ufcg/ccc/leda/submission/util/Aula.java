package br.edu.ufcg.ccc.leda.submission.util;

import java.util.GregorianCalendar;
import java.util.List;

public class Aula extends Atividade{

	public Aula(String id, String nome, String descricao,
			GregorianCalendar dataHora, List<LinkVideoAula> linksVideoAulas,
			List<Monitor> monitores) {
		super(id, nome, descricao, dataHora, linksVideoAulas, monitores);
	}

	
}
