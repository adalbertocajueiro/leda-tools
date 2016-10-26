package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.List;

public class Prova extends Roteiro{

	
	public Prova(String id, String nome, String descricao, GregorianCalendar dataHoraLiberacao,
			List<LinkVideoAula> linksVideoAulas, GregorianCalendar dataHoraLimiteEnvioNormal,
			GregorianCalendar dataHoraLimiteEnvioAtraso, List<Monitor> monitores, Monitor monitorCorretor, GregorianCalendar dataInicioCorrecao,
			GregorianCalendar dataLimiteCorrecao, File arquivoAmbiente, File arquivoProjetoCorrecao) {
		
		super(id, nome, descricao, dataHoraLiberacao, linksVideoAulas, 
				dataHoraLimiteEnvioNormal, dataHoraLimiteEnvioAtraso,
				monitores, monitorCorretor, dataInicioCorrecao, 
				dataLimiteCorrecao, arquivoAmbiente, arquivoProjetoCorrecao);

	}

/*	@Override
	public String toString() {
		if(this.dataHora != null){
			return this.id + " - " + Util.formatDate(this.dataHora);
		}else{
			return this.id;
		}
		
	}
*/
	
}
