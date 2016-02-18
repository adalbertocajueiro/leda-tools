package br.edu.ufcg.ccc.leda.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gustavo
 *
 */
public enum Algorithm {
	BUBBLE(1,"Bubblesort"),
	
	INSERTION(2,"Insertionsort"),
	
	SELECTION(3,"Insertionsort");
	
	private int code;
	private String name;
	
	private static final Map<String, Algorithm> lookup = new HashMap<String, Algorithm>();
	
	private Algorithm(int code, String name) {
		this.code = code;
		this.name = name;
	}
	
	static {
		for (Algorithm alg : Algorithm.values()){
			lookup.put(alg.getName(), alg);
		}
	}
	
	public int getCode(){
		return code;
	}
	
	public static int getCode(String algorithmName) {
		return lookup.get(algorithmName).getCode();
	}
	
	public String getName(){
		return name;
	}

}
