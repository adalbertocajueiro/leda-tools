package green.atm.util;



import green.atm.extrato.Transacao;

public class Predicates {
	public static boolean isSeguranca(Transacao t){
		return t.getDescricao().contains("Optimus");
	}
	
	public static boolean isFolhaRH(Transacao t){
		return t.getDescricao().contains("Optimus");
	}
}
