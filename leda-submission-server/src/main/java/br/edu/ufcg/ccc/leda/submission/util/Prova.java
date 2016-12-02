package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.util.GregorianCalendar;
import java.util.List;

public class Prova extends Roteiro{

	
	public Prova(String id, String nome, String descricao, GregorianCalendar dataHoraLiberacao,
			List<LinkVideoAula> linksVideoAulas, GregorianCalendar dataHoraLimiteEnvioNormal,
			List<Monitor> monitores, Professor corretor, GregorianCalendar dataInicioCorrecao,
			GregorianCalendar dataLimiteCorrecao, File arquivoAmbiente, File arquivoProjetoCorrecao) {
		//nas provas a hora de envio com atraso eh a hora de envio normal mesmo
		super(id, nome, descricao, dataHoraLiberacao, linksVideoAulas, 
				dataHoraLimiteEnvioNormal, dataHoraLimiteEnvioNormal,
				monitores, corretor, dataInicioCorrecao, 
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
