package br.edu.ufcg.ccc.leda.util;

/**
 * Enum para especificar os caminhos que são usados para
 * criaçãoo ou leitura do arquivo JSON
 * 
 * @author Gustavo
 */
public enum PathsEnum {
	JSON_SOURCE("%s/resources/web/data.json"),
	
	DATA_SOURCE("src/main/resources/data/"),
	
	HTML_SOURCE("%s/resources/web/index.html");

	String path;

	private PathsEnum(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
	public String getPath(String baseDir) {
		return String.format(path, baseDir);
	}

}
