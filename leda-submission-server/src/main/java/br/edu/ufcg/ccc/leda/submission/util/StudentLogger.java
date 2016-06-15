package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentLogger{
	private Logger logger;
	private FileHandler fh;
	private String studentName; 
	private File logFolder;
	
	public StudentLogger(String studentName){
		logFolder = new File("D:\\trash"); //TODO
		this.studentName = studentName;
		try {
			fh = new FileHandler(logFolder.getAbsolutePath() + File.separator + studentName + ".log",true);
			fh.setFormatter(new XMLLogFormatter());
			logger = Logger.getLogger(studentName); //cada estudante terá um logger associado
			logger.setLevel(Level.FINE);
			logger.addHandler(fh);  
			
		} catch (SecurityException e) {
			System.out.println("Secutiry exception Logger was not created");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO exception Logger was not created");
			e.printStackTrace();
		}
	}

	
	public void log(String content){
		logger.fine(content);
	}
	
	public void close(){
		fh.close();
	}
	
	public static void main(String[] args) {
		StudentLogger sl = new StudentLogger("Adalberto");
		sl.log("teste: " + System.nanoTime() + "\n");
		System.out.println("end");
	}
	
}
