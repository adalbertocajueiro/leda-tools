package br.edu.ufcg.ccc.leda.graph;
import java.util.Set;
import java.util.TreeSet;

import br.edu.ufcg.ccc.leda.util.Algorithm;

public class Graph {
		private JsonGraph json;
		
		Set<Coordinate<Double,Double>> setOfCoordinates;

		public Graph(){
			json = new JsonGraph();
			openSerie();
		}
	
		public void addCoordinate(Double x, Double y){
			setOfCoordinates.add(new Coordinate<Double, Double>(x, y));
		}
		
		public void closeSerie(String algorithm){
			for(Coordinate<Double, Double> coord : setOfCoordinates) {
				json.addCoordinates(coord, algorithm);
			}
		}
		
		public void openSerie() {
			setOfCoordinates = new TreeSet<Coordinate<Double,Double>>();
		}
		
		public void draw(String baseDir) {
			json.createJson(baseDir);
		}
}