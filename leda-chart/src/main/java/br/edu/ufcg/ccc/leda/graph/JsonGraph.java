package br.edu.ufcg.ccc.leda.graph;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.edu.ufcg.ccc.leda.util.Algorithm;
import br.edu.ufcg.ccc.leda.util.PathsEnum;

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
		jsonCoordinate.put("xaxis", coord.getKey());
		

		return jsonCoordinate;
	}
	
	@SuppressWarnings("unchecked")
	public void createJson(String baseDir) {
		try {
			String data = "data = " + coordinatesArray.toJSONString();
			
			OutputStream os = new FileOutputStream(PathsEnum.JSON_SOURCE.getPath(baseDir));
			
			for(int i = 0 ; i < data.length(); i++) {
				os.write(data.charAt(i));
			}
			
			os.close();
			
			
		} catch (IOException ex) {
			System.out
					.println("Would'nt be able to find the path you passed on to save the JSON" + ex);
		}
	}
}
