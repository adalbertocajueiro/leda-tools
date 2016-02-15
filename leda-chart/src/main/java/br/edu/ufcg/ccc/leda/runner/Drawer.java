package br.edu.ufcg.ccc.leda.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.management.timer.Timer;

import br.edu.ufcg.ccc.leda.graph.Graph;
import br.edu.ufcg.ccc.leda.sorting.Sorting;

/**
 * Classe responsável pela captação e execução das implementações
 * de Sorting e por gerar os dados necessários para criaçao dos 
 * graficos de desempenho. 
 * @author Gustavo
 *
 */
public class Drawer {
	
	/**
	 * Lista com os fullyQualifiedNames das implementações a serem
	 * gerado os graficos
	 */
	private String[] sortingList;
	
	/**
	 *  Objeto Graph que irá gerar o grafico
	 */
	Graph graph;
	
	/**
	 *  Construtor Default 
	 */
	public Drawer() {
		graph = new Graph();
	}
	
	/**
	 * Adiciona as implementações
	 * @param sortList
	 * 				Lista de String contendo os fullyqualifiedName das
	 * 				Classes a serem executadas
	 */
	public void addSortingImplementation(String[] sortList) {
		if (sortList != null) {
			sortingList = sortList;
		}

	}

	/**
	 * Extrai as implementações e gera instancias delas para serem
	 * executadas e gerarem o gráfico.
	 */
	public void extractImplemantation() {
		for (String sort : sortingList) {
			Sorting sortClass;
			try {
				sortClass = (Sorting) Class.forName(sort).newInstance();
				draw(sortClass);
			} catch (InstantiationException | IllegalAccessException
					| SecurityException | ClassNotFoundException
					| NumberFormatException e) {
				System.out
						.println("An error occurred while capturing implemantation "
								+ e.getMessage());
			}

		}
	}

	/**
	 * Executa as instancias dos sortings para arquivos com diferentes
	 * quantidades de dados a serem ordenados e coleta o tempo gasto para
	 * ordenaçao de cada arquivo, gerando ,assim, os dados necessarios para
	 * criaçao do gráfico. 
	 * 
	 * @param implementation
	 * 				Uma implementação do tipo Sorting. 
	 */
	private void draw(Sorting implementation) {
		File file = new File("settings/randomFiles/");

		graph.openSerie();
		for (File fi : file.listFiles()) {
			Integer[] a = new Integer[0];
			
			try {
				a = extractFromFile(new FileReader(
						"settings/randomFiles/" + fi.getName()));
			} catch (NumberFormatException | IOException e) {
				System.out
				.println("An error occurred while exctracting Files to be sorted "
						+ e.getMessage());
			}

			long start = System.nanoTime();
			implementation.sort(a);
			double elapsedTime = (System.nanoTime() - start) / 1000000000F;

			//System.out.println(elapsedTime);

			graph.addCoordinate((double) a.length, elapsedTime);
			for (int i : a) {
			}
		}
		String implemantationName = implementation.getClass().getSimpleName();
		graph.closeSerie(implemantationName);
		graph.draw();

	}
	
	/**
	 * Extrai de arquivos a lista de numeros a serem ordenados, essas
	 * listas serao usadas para medir o tempo de ordenaçao por cada algoritmo
	 * @param file
	 * 			Arquivo contendo os dados que serão ordenados
	 * @return um Array de inteiros 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private Integer[] extractFromFile(FileReader file)
			throws NumberFormatException, IOException {

		BufferedReader bufferedRead = new BufferedReader(file);

		int numOfItens = Integer.parseInt(bufferedRead.readLine());
		String[] listOfItens = bufferedRead.readLine().split(",");

		Integer[] auxiliarList = new Integer[numOfItens];

		for (int i = 0; i < numOfItens; i++) {
			auxiliarList[i] = Integer.parseInt(listOfItens[i]);
		}

		return auxiliarList;
	}
}