package br.edu.ufcg.ccc.leda;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import br.edu.ufcg.ccc.leda.graph.Graph;
import br.edu.ufcg.ccc.leda.runner.Drawer;
import br.edu.ufcg.ccc.leda.util.Algorithm;
import br.edu.ufcg.ccc.leda.util.Paths;
import br.edu.ufcg.ccc.leda.simpleSorting.*;
import br.edu.ufcg.ccc.leda.sorting.Sorting;


public class Sample {

	public static void main(String[] args) throws IOException {
		// Set of Coordinate
		Sorting[] bubble = new Sorting[2];
		
		bubble[1] = new Bubblesort<Integer>();
		bubble[0] = new Insertionsort<Integer>();
		
		Drawer chartDrawer = new Drawer();
		chartDrawer.addSortingImplementation(bubble);
		chartDrawer.draw();
		/*Graph graph = new Graph();
		
		//Create series 1 
		graph.openSerie();
		createCoordinates(graph);
		graph.closeSerie(Algorithm.BUBBLE);

		//Create Series 2
		graph.openSerie();
		createCoordinates3(graph);
		graph.closeSerie( Algorithm.INSERTION);
		
		graph.draw();
*/		
		openBrowser();
	}

	private static void createCoordinates(Graph graph) {
		graph.addCoordinate(0.0,0.0);
		graph.addCoordinate(1.0,1.0);
		graph.addCoordinate(2.0,5.0);
		graph.addCoordinate(3.0,3.0);
		graph.addCoordinate(4.0,3.0);
	}
	
	
	private static void createCoordinates3(Graph graph) {
		for(double i = 0; i < 5; i ++) {
			double y = i * i + 2*i + 1;
			
			graph.addCoordinate(i,y);
		}
	}
	
	private static void openBrowser(){
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	        	File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/" + Paths.HTML_SOURCE.getPath());
	            desktop.browse(file.toURI());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
}
