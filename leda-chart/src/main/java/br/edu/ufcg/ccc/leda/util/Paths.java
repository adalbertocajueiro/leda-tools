package br.edu.ufcg.ccc.leda.util;

/**
 * Enum para especificar os caminhos que são usados para
 * criação ou leitura do arquivo JSON
 * 
 * @author Gustavo
 */
public enum Paths {
	JSON_SOURCE("src/main/webapp/data.json"),
	
	HTML_SOURCE("src/main/webapp/index.html");

	String path;

	private Paths(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
