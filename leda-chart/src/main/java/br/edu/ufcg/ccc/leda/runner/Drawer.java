package br.edu.ufcg.ccc.leda.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.management.timer.Timer;

import br.edu.ufcg.ccc.leda.graph.Graph;
import br.edu.ufcg.ccc.leda.sorting.Sorting;

public class Drawer {

	private String[] sortingList;
	Graph graph;

	
	public Drawer() {
		graph = new Graph();
	}
	
	public void addSortingImplementation(String[] sortList) {
		if (sortList != null) {
			sortingList = sortList;
		}

	}

	public void draw() {
		for (String sort : sortingList) {
			Sorting sortClass;
			try {
				Object  sorting = Class.forName(sort).newInstance();
				
				if (sorting instanceof Sorting) {
					sortClass = (Sorting) sorting;
				};
				sortClass = (Sorting) Class.forName(sort).newInstance();
				System.out.println(Class.forName(sort).getConstructor());
				
				run(sortClass);
			} catch (InstantiationException | IllegalAccessException
					| NoSuchMethodException | SecurityException
					| ClassNotFoundException | NumberFormatException | IOException  e) {
				System.out.println("An error occurred while generating the chart " + e.getMessage());
			}
			
			
			
			
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
