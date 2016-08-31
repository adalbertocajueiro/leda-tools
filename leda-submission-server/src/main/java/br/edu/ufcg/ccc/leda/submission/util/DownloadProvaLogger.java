package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadProvaLogger{
	private Logger logger;
	private FileHandler fh;
	private File logFolder;
	
	public DownloadProvaLogger(File logFolder){
		this.logFolder = logFolder;
		
		try {
			fh = new FileHandler(logFolder.getAbsolutePath() + File.separator + logFolder.getName() + ".log",true);
			fh.setFormatter(new XMLLogFormatter());
			logger = Logger.getLogger(logFolder.getName());
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

	
	public void log(String content) throws IOException{
		logger.fine(content);
		fh.close();
	}
	
}
