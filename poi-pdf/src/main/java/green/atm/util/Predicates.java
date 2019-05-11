package green.atm.util;



import java.util.List;

import green.atm.extrato.Transacao;

public class Predicates {
	public static boolean isSeguranca(Transacao t){
		boolean result = false;
		ModalidadeTransacao mod = Configuration.getInstance().getModalidades().get(t.getTextoIdentificador());
		if(mod != null) {
			if (mod.equals(ModalidadeTransacao.RH_ORGANICO_SEGURANCA)) {
				result = t.getDescricao().contains(Util.NOME_EMPRESA_SEGURANCA);
			}
		}
		return result;
	}
	
	public static boolean isRHOrganico(Transacao t){
		boolean result = false;
		ModalidadeTransacao mod = Configuration.getInstance().getModalidades().get(t.getTextoIdentificador());
		List<String> funcionarios = Configuration.getInstance().getFuncionarios();
		if(mod != null) {
			if (mod.equals(ModalidadeTransacao.RH_ORGANICO_SEGURANCA)) {
				result = funcionarios.stream().filter(s -> t.getDescricao().contains(s)).findFirst().isPresent();
			}
		}
		return result;
	}
	
	public static boolean isAgua(Transacao t){
		boolean result = false;
		ModalidadeTransacao mod = Configuration.getInstance().getModalidades().get(t.getTextoIdentificador());
		if(mod != null) {
			result = mod.equals(ModalidadeTransacao.AGUA);
		}
		return result;
	}
	
	public static boolean isEnergia(Transacao t){
		boolean result = false;
		ModalidadeTransacao mod = Configuration.getInstance().getModalidades().get(t.getTextoIdentificador());
		if(mod != null) {
			if(mod.equals(ModalidadeTransacao.PGTO_BOLETO)) {
				result = t.getDescricao().contains(Util.NOME_ENERGIA);						
			}
		}
		return result;
	}
	public static boolean isFGTS(Transacao t){
		boolean result = false;
		ModalidadeTransacao mod = Configuration.getInstance().getModalidades().get(t.getTextoIdentificador());
		if(mod != null) {
			result = mod.equals(ModalidadeTransacao.FGTS);
		}
		return result;
	}
	public static boolean isINSS(Transacao t){
		boolean result = false;
		ModalidadeTransacao mod = Configuration.getInstance().getModalidades().get(t.getTextoIdentificador());
		if(mod != null) {
			result = mod.equals(ModalidadeTransacao.INSS);
		}
		return result;
	}
	public static boolean isIRPF(Transacao t){
		boolean result = false;
		ModalidadeTransacao mod = Configuration.getInstance().getModalidades().get(t.getTextoIdentificador());
		if(mod != null) {
			result = mod.equals(ModalidadeTransacao.IRPF);
		}
		return result;
	}
}
