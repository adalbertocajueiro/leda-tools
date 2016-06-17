package br.edu.ufcg.ccc.leda.submission.util;

public class StudentException extends Exception {

	private static String mensagem = "Estudante nao cadastrado na turma!";
	
	public StudentException() {
		super(mensagem);
	}

	public StudentException(String message) {
		super(mensagem);
		// TODO Auto-generated constructor stub
	}

	public StudentException(Throwable cause) {
		super(cause);
	}

	public StudentException(String message, Throwable cause) {
		super(mensagem, cause);
	}

	public StudentException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(mensagem, cause, enableSuppression, writableStackTrace);
	}

}
