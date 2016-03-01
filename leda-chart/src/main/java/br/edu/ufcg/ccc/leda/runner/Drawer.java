package br.edu.ufcg.ccc.leda.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.management.timer.Timer;

import br.edu.ufcg.ccc.leda.graph.Graph;
import br.edu.ufcg.ccc.leda.sorting.Sorting;
import br.edu.ufcg.ccc.leda.util.DataStream;

/**
 * Classe responsável pela captação e execução das implementações de Sorting e
 * por gerar os dados necessários para criaçao dos graficos de desempenho.
 * 
 * @author Gustavo
 *
 */
public class Drawer {

	/**
	 * Lista com os fullyQualifiedNames das implementações a serem gerado os
	 * graficos
	 */
	private List<String> sortingList;

	/**
	 * Objeto Graph que irá gerar o grafico
	 */
	private Graph graph;

	private String baseDir;

	/**
	 * Construtor Default
	 */
	public Drawer(File targetFolder) {

		setBaseDir(targetFolder.getPath());
		graph = new Graph();
	}

	/**
	 * Adiciona as implementações
	 * 
	 * @param sortList
	 *            Lista de String contendo os fullyqualifiedName das Classes a
	 *            serem executadas
	 * @param urlClassLoader
	 */
	public void addSortingImplementation(List<String> sortList) {
		if (sortList != null) {
			sortingList = sortList;
		}

	}

	/**
	 * Extrai as implementações e gera instancias delas para serem executadas e
	 * gerarem o gráfico.
	 * 
	 * @param sortingInterface
	 * @param urlClassLoader
	 * @throws InvocationTargetException
	 */
	public void extractImplemantation(String sortingInterface, URLClassLoader urlClassLoader) {
		
		for (String sort : sortingList) {
			Class<?> loader = null;
			
			Object sortClass = null;
			
			try {
				loader = urlClassLoader.loadClass(sort);
				sortClass = loader.newInstance();
				
				Method[] methods = loader.getSuperclass().getDeclaredMethods();
				
				for(Method mt : methods){
					String mtName = mt.toGenericString();
					System.out.println(mtName);
					
					if(mtName.contains("public")){
						draw(mt,  sortClass, loader.getSimpleName());
					}
				}				
			} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException | InvocationTargetException  e) {
				System.out.println("[ERROR] An error occurred while capturing implemantation "
								+ e.getStackTrace());
				
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
	 *            Uma implementação do tipo Sorting.
	 * @throws IOException
	 * @throws InvocationTargetException
	 */
	private void draw(Method method, Object obj, String accessedClassName)
			throws IOException, InvocationTargetException {
		List<BufferedReader> listOfFiles = new ArrayList<BufferedReader>();

		try {
			listOfFiles = DataStream.getData();
		} catch (IOException e1) {
			System.out.println("[ERROR] Error while streaming data "
					+ e1.getMessage());
		}

		graph.openSerie();

		for (BufferedReader dataFile : listOfFiles) {
			Integer[] a = new Integer[0];

			try {
				a = extractDataFromFile(dataFile);
			} catch (NumberFormatException | IOException e) {
				System.out
						.println("[ERROR] An error occurred while exctracting Files to be sorted "
								+ e.getMessage());
			}

			long start = System.nanoTime();
			try {
				method.invoke(obj, new Object[] { a });
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double elapsedTime = (System.nanoTime() - start) / 1000000000F;

			System.out.println(elapsedTime);

			graph.addCoordinate((double) a.length, elapsedTime);
			for (int i : a) {
			}

		}

		DataStream.closeStream();
		graph.closeSerie(accessedClassName);
		graph.draw(baseDir);

	}

	/**
	 * Extrai de arquivos a lista de numeros a serem ordenados, essas listas
	 * serao usadas para medir o tempo de ordenaçao por cada algoritmo
	 * 
	 * @param file
	 *            Arquivo contendo os dados que serão ordenados
	 * @return um Array de inteiros
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private Integer[] extractDataFromFile(BufferedReader file)
			throws NumberFormatException, IOException {

		int numOfItens = Integer.parseInt(file.readLine());
		String[] listOfItens = file.readLine().split(",");

		Integer[] auxiliarList = new Integer[numOfItens];

		for (int i = 0; i < numOfItens; i++) {
			auxiliarList[i] = Integer.parseInt(listOfItens[i]);
		}

		return auxiliarList;
	}

	/**
	 * 
	 * @param baseDir
	 */
	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
}