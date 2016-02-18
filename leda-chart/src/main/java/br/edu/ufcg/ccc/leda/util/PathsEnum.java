package br.edu.ufcg.ccc.leda.util;

/**
 * Enum para especificar os caminhos que são usados para
 * criaçãoo ou leitura do arquivo JSON
 * 
 * @author Gustavo
 */
public enum PathsEnum {
	JSON_SOURCE("src/main/resources/web/data.json"),
	
	DATA_SOURCE("src/main/resources/data/"),
	
	HTML_SOURCE("src/main/resources/web/");

	String path;

	private PathsEnum(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
