package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public abstract class Sender {

	protected File arquivo;
	protected String roteiro;
	protected String url;

	public Sender(File arquivo, String roteiro, String url) {
		super();
		this.arquivo = arquivo;
		this.roteiro = roteiro;
		this.url = url;
	}
	public abstract void send() throws ClientProtocolException, IOException;
	
	void writeTicket(String logFileName, String content) throws IOException{
		
		File file = new File(logFileName);
		if(!file.exists()){
			file.createNewFile();
		}
		FileWriter fr = new FileWriter(file);
		fr.write(content);
		fr.close();
	}
	
	public File getArquivo() {
		return arquivo;
	}

	public void setArquivo(File arquivo) {
		this.arquivo = arquivo;
	}

	public String getRoteiro() {
		return roteiro;
	}

	public void setRoteiro(String roteiro) {
		this.roteiro = roteiro;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}