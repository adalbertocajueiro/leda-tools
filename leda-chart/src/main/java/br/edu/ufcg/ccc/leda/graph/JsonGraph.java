package br.edu.ufcg.ccc.leda.graph;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.edu.ufcg.ccc.leda.util.Algorithm;
import br.edu.ufcg.ccc.leda.util.Paths;

/**
 * 
 * @author Gustavo Oliveira
 */
public class JsonGraph {
	private JSONObject graph;

	private JSONArray coordinatesArray;

	public JsonGraph() {
		graph = new JSONObject();
		coordinatesArray = new JSONArray();
	}

	public void addCoordinates(Coordinate<Double, Double> coord,
			String algorithm) {
		coordinatesArray.add(createPointObject(coord, algorithm, Algorithm.getCode(algorithm)));
	}

	@SuppressWarnings("unchecked")
	private JSONObject createPointObject(Coordinate<Double, Double> coord,
			String algorithm, int algorithmCode) {
		JSONObject jsonCoordinate = new JSONObject();
		jsonCoordinate.put("algorithmCode", algorithmCode);
		jsonCoordinate.put("algorithm", algorithm);
		jsonCoordinate.put("yaxis", coord.getValue());
		jsonCoordinate.put("xaxis", coord.getKey());//.toString().replace(".", ","));
		

		return jsonCoordinate;
	}
	
	@SuppressWarnings("unchecked")
	public void createJson() {
		graph.put("", coordinatesArray);
		try {
			FileWriter file = new FileWriter(Paths.JSON_SOURCE.getPath());
			file.write("data = " + coordinatesArray.toJSONString());
			file.close();
		} catch (IOException ex) {
			System.out
					.println("Would'nt be able to find the path you passed on to save the JSON");
		}
	}
}
