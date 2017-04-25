package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.List;

public class RoteiroEspecial extends Roteiro{

	
	public RoteiroEspecial(String id, String nome, String descricao, GregorianCalendar dataHoraLiberacao,
			List<LinkVideoAula> linksVideoAulas, GregorianCalendar dataHoraLimiteEnvioNormal,
			GregorianCalendar dataHoraLimiteEnvioAtraso, List<Monitor> monitores, Corretor monitorCorretor, GregorianCalendar dataInicioCorrecao,
			GregorianCalendar dataLimiteCorrecao, File arquivoAmbiente, File arquivoProjetoCorrecao) {
		
		super(id, nome, descricao, dataHoraLiberacao, linksVideoAulas, dataHoraLimiteEnvioNormal, dataHoraLimiteEnvioAtraso,
				monitores, monitorCorretor, dataInicioCorrecao, dataLimiteCorrecao, arquivoAmbiente, arquivoProjetoCorrecao);

	}
}
