package br.edu.ufcg.ccc.leda.util;

/**
 * @author Gustavo
 *
 */
public enum Algorithm {
	BUBBLE("Bubble Sort"),
	
	INSERTION("Insertion Sort");
	
	private String name;
	
	private Algorithm(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

}
