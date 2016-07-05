package br.edu.ufcg.ccc.leda.submission.util;

public class StudentException extends Exception {

	public StudentException() {
		super();
	}

	public StudentException(String message) {
		super(message);
	}

	public StudentException(Throwable cause) {
		super(cause);
	}

	public StudentException(String message, Throwable cause) {
		super(message, cause);
	}

	public StudentException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
