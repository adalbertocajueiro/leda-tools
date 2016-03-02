package br.edu.ufcg.ccc.leda.util;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufcg.ccc.leda.runner.Drawer;


public class Sample {

	public static void main(String[] args) throws IOException, NumberFormatException, ClassNotFoundException {
		// Set of Coordinate
		List<String> bubble = new ArrayList<String>();
		
		bubble.add("br.edu.ufcg.ccc.leda.util.Bubblesort");
		//bubble.add(new Insertionsort<Integer>().getClass().getName());
		
		File targetDir = new File("/home/gustavooliveira/workspace/Roteiro-OrdenacaoPorComparacao-I-correction-proj-T1/Roteiro-OrdenacaoPorComparacao-I-correction-env-T1/target");
		
		Drawer chartDrawer = new Drawer(targetDir);
		//chartDrawer.addSortingImplementations(bubble);
		//chartDrawer.extractImplemantation();
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
