package br.edu.ufcg.ccc.leda.submission.util;

import java.net.MalformedURLException;
import java.net.URL;

public class LinkVideoAula {
	private URL url;
	private String texto;
	/**
	 * o parametro precisa ter o formato: texto-$-URL, onde texto é o texto a 
	 * ser mostrado no link html a ser gerado,
	 * 
	 * @param textoURL
	 * @throws MalformedURLException 
	 */
	public LinkVideoAula(String textoURL) {
		if(textoURL != null){
			String[] partes = textoURL.split("---");
			if(partes != null && partes.length >= 2){
				try {
					this.texto = partes[0].trim();
					this.url = new URL(partes[1].trim());
				} catch (MalformedURLException e) {
					//nao adiciona nenhum link
					e.printStackTrace();
				}
			}
		}
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	
	
}
