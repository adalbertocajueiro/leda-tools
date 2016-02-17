package br.edu.ufcg.ccc.leda.util;

/**
 * Enum para especificar os caminhos que s�o usados para
 * cria��o ou leitura do arquivo JSON
 * 
 * @author Gustavo
 */
public enum Paths {
	JSON_SOURCE("src/main/resources/web/data.json"),
	
	DATA_SOURCE("src/main/resources/data/"),
	
	HTML_SOURCE("src/main/resources/web/index.html");

	String path;

	private Paths(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
