package br.edu.ufcg.ccc.leda.graph;

import java.util.ArrayList;
import java.util.Iterator;

public class GraphData<Tx,Ty> {

	private ArrayList<Serie<Tx,Ty>> series;

	public GraphData() {
		series = new ArrayList<Serie<Tx,Ty>>();
	}

	public void addSerie(Serie<Tx,Ty> serie){
		series.add(serie);
	}

	@Override
	public String toString() {
		return "[" + toStringSeries() + "];";
	}
	
	private String toStringSeries(){
		
			StringBuilder sb = new StringBuilder();
			for (Iterator iterator = this.series.iterator(); iterator.hasNext();) {
				Serie serie = (Serie) iterator.next();
				sb.append(serie.toString());
				if(iterator.hasNext()){
					sb.append(",");
				}
			}
			
			return sb.toString();

	}
	
}
