package br.edu.ufcg.ccc.leda.graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.edu.ufcg.ccc.leda.util.Utilities;

public class JsonGraph {
	private JSONArray coordinatesArray;

	public JsonGraph() {
		coordinatesArray = new JSONArray();
	}

	public void addCoordinate(Coordinate<Integer,Double> coord){
		coordinatesArray.add(createPointObject(coord, coord.getAlgorithmName(), coord.getAlgorithmCode()));
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject createPointObject(Coordinate<Integer, Double> coord, String algorithm, int algorithmCode) {
		
		JSONObject jsonCoordinate = new JSONObject();
		jsonCoordinate.put("algorithmCode", algorithmCode);
		jsonCoordinate.put("algorithm", algorithm);
		jsonCoordinate.put("yaxis", coord.getyValue());
		jsonCoordinate.put("xaxis", coord.getxValue());
		

		return jsonCoordinate;
	}
	
	@SuppressWarnings("unchecked")
	public void createJson(File baseDir) {
		try {
			String data = "data = " + coordinatesArray.toJSONString();
			
			File file = new File(baseDir,Utilities.JSON_FILE_NAME);
			FileWriter writer = new FileWriter(file);
	        writer.write(data);
	        writer.flush();
	        writer.close();

		} catch (IOException ex) {
			System.out.println("Would'nt be able to find the path you passed on to save the JSON" + ex);
		}
	}
}
