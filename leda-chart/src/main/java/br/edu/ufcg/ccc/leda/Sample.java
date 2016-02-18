package br.edu.ufcg.ccc.leda;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import br.edu.ufcg.ccc.leda.graph.Graph;
import br.edu.ufcg.ccc.leda.runner.Drawer;
import br.edu.ufcg.ccc.leda.util.*;
import br.edu.ufcg.ccc.leda.sorting.Sorting;


public class Sample {

	public static void main(String[] args) throws IOException, NumberFormatException, ClassNotFoundException {
		// Set of Coordinate
		String[] bubble = new String[2];
		
		bubble[0] = "br.edu.ufcg.ccc.leda.util.Bubblesort";
		bubble[1] = new Insertionsort<Integer>().getClass().getName();
		
		Drawer chartDrawer = new Drawer();
		chartDrawer.addSortingImplementation(bubble);
		chartDrawer.extractImplemantation();
		openBrowser();
	}

	private static void openBrowser(){
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	        	File file = new File(System.getProperty("user.dir").replace("\\", "/") + "/" + PathsEnum.HTML_SOURCE.getPath());
	            desktop.browse(file.toURI());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
}
