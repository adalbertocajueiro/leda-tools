package br.edu.ufcg.ccc.leda.submission.util;

public class ConfigurationException extends Exception {

	private static String mensagem = "Problema na inicializacao das configuracoes!";
	
	public ConfigurationException() {
		super(mensagem);
	}

	public ConfigurationException(String message) {
		super(mensagem);
		// TODO Auto-generated constructor stub
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	public ConfigurationException(String message, Throwable cause) {
		super(mensagem, cause);
	}

	public ConfigurationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(mensagem, cause, enableSuppression, writableStackTrace);
	}

}
