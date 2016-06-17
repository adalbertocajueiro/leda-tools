package br.edu.ufcg.ccc.leda.submission.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentLogger{
	private Logger logger;
	private FileHandler fh;
	private String studentName; 
	private File logFolder;
	
	public StudentLogger(String studentName, File logFolder){
		this.logFolder = logFolder;
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

	
	public String completeLog(String content) throws IOException{
		StringBuilder sb = new StringBuilder();
		
		logger.fine(content);
		sb = readLogFile();
		fh.close();
		
		return sb.toString();
	}
	
	private StringBuilder readLogFile() throws IOException{
		StringBuilder sb = new StringBuilder();
		File studentLogFile = new File(logFolder.getAbsolutePath() + File.separator + studentName + ".log");
		FileReader fr = new FileReader(studentLogFile);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		while( (line = br.readLine()) != null){
			sb.append(line + "\n");
		}
		br.close();
		fr.close();
		
		return sb;
	}
		
	public static void main(String[] args) {
		//StudentLogger sl = new StudentLogger("Adalberto");
		//sl.log("teste: " + System.nanoTime() + "\n");
		//System.out.println("end");
	}
	
}
