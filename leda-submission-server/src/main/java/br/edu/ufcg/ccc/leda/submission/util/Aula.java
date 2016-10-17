package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;

public class Aula extends Atividade{

	public Aula(String id, String nome, String descricao,
			GregorianCalendar dataHora, List<URL> linksVideoAulas,
			List<Monitor> monitores) {
		super(id, nome, descricao, dataHora, linksVideoAulas, monitores);
	}

	
}
