package br.edu.ufcg.ccc.leda.graph;

import java.util.Map.Entry;

/**
 * Class that represents a specific point in a graph
 * 
 * @param <K>
 *            K represents the X-coordinate
 * @param <V>
 *            V Represents the Y-coordinate
 * 
 * @author Gustavo
 */
public class Coordinate<Tx,Ty> {
	private Tx xValue;
	private Ty yValue;
	private String algorithmName;
	private int algorithmCode;

	public Coordinate(Tx xValue, Ty yValue) {
		super();
		this.xValue = xValue;
		this.yValue = yValue;
	}

	
	public Coordinate(Tx xValue, Ty yValue, String algorithmName,
			int algorithmCode) {
		super();
		this.xValue = xValue;
		this.yValue = yValue;
		this.algorithmName = algorithmName;
		this.algorithmCode = algorithmCode;
	}


	public void setxValue(Tx xValue) {
		this.xValue = xValue;
	}
	public Ty getyValue() {
		return yValue;
	}
	public void setyValue(Ty yValue) {
		this.yValue = yValue;
	}
	public Tx getxValue() {
		return xValue;
	}
	
	public String getAlgorithmName() {
		return algorithmName;
	}


	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}


	public int getAlgorithmCode() {
		return algorithmCode;
	}
	public void setAlgorithmCode(int algorithmCode) {
		this.algorithmCode = algorithmCode;
	}
}
