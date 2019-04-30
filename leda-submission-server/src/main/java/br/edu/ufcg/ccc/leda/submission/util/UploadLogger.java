package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadLogger {

	public static String logSubmission(File submitted, StudentUploadConfiguration config, String studentName) throws IOException{
		String result = "default value for log";
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-YYYY HH*mm*ss");
		long time = submitted.lastModified();
	    Date d = new Date(time);
		//cria/append um log com a matricula do estudante na pasta correta 
		//e depois retorna uma confirmação para o aluno do envio. O valor retornado
		//pode ser o log completo do servidor.
		File folder = submitted.getParentFile();
		StudentLogger logger = new StudentLogger(studentName,folder);
		String content = "File " + submitted.getName() + " was received. It was sent by " 
				+ studentName + " from IP " + config.getIp()
				+ " at " + df.format(d); 
		result = logger.completeLog(content);
		return result;
	}
}
