package br.edu.ufcg.ccc.leda.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.management.timer.Timer;

import br.edu.ufcg.ccc.leda.graph.Graph;
import br.edu.ufcg.ccc.leda.sorting.Sorting;

public class Drawer {

	private Sorting[] sortingList;
	Graph graph;

	
	public Drawer() {
		graph = new Graph();
	}
	
	public void addSortingImplementation(Sorting[] sortList) {
		if (sortList != null) {
			sortingList = sortList;
		}

	}

	public void draw() throws NumberFormatException, IOException {
		for (Sorting sort : sortingList) {
			String implemantationName = sort.getClass().getSimpleName();
			
			
			
			System.out.println(implemantationName);
			run(sort);
		}
	}

	public void run(Sorting implementation) throws NumberFormatException, IOException{
		File file = new File("settings/randomFiles/");
		
		graph.openSerie();
		for(File fi : file.listFiles()){
			Integer[] a = extractFromFile(new FileReader("settings/randomFiles/" + fi.getName()));

			long start = System.nanoTime();    
			implementation.sort(a);
			double elapsedTime = (System.nanoTime() - start)/1000000000F;
			
			System.out.println(elapsedTime);
			
			graph.addCoordinate((double)a.length, elapsedTime);
			for(int i : a){
//				System.out.println(i);
			}
//			break;
		}
		String implemantationName = implementation.getClass().getSimpleName();
		graph.closeSerie(implemantationName);
		graph.draw();
		
	}
	
	private Integer[] extractFromFile (FileReader file) throws NumberFormatException, IOException {
		
		BufferedReader bufferedRead = new BufferedReader(file);
		
		int numOfItens =  Integer.parseInt(bufferedRead.readLine());
		String[] listOfItens = bufferedRead.readLine().split(",");
		
		Integer[] auxiliarList = new Integer[numOfItens];
		
		for(int i = 0; i < numOfItens; i ++) { 
			auxiliarList[i] = Integer.parseInt(listOfItens[i]);
		}
		
		return auxiliarList;
	}
}
