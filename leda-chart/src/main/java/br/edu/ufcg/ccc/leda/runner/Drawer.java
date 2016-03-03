package br.edu.ufcg.ccc.leda.runner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import sorting.Sorting;
import br.edu.ufcg.ccc.leda.graph.Coordinate;
import br.edu.ufcg.ccc.leda.graph.JsonGraph;
import br.edu.ufcg.ccc.leda.util.InputGenerator;
import br.edu.ufcg.ccc.leda.util.Utilities;

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
	private List<Class<?>> sortingList;

	private List<Sorting> sortingImplementations;
	
	/**
	 * Objeto Graph que irá gerar o grafico
	 */
	private JsonGraph graph;

	private File baseDir;
	
	private ClassLoader classLoader;

	/**
	 * Construtor Default
	 */
	public Drawer(File targetFolder) {
		setBaseDir(targetFolder);
		sortingImplementations = new ArrayList<Sorting>();
		graph = new JsonGraph();
	}


	public List<Class<?>> getSortingList() {
		return sortingList;
	}


	public void setSortingList(List<Class<?>> sortingList){
		this.sortingList = sortingList;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}


	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}


	public File getBaseDir() {
		return baseDir;
	}

	public void instantiateAndRunImplementations() throws InstantiationException, IllegalAccessException, IOException, IllegalArgumentException, InvocationTargetException{
		int algorithmCode = 0;
		for (Class<?> sortingClass : sortingList) {
			Object sortImplementation = (Object) sortingClass.newInstance();
			executionData(sortImplementation, algorithmCode);
			algorithmCode++;
		}
		File webFolder = Utilities.createWebFolder(baseDir);
		System.out.println("CREATING JSON FILE");
		graph.createJson(webFolder);
	}
	
	public void collectExecutionData(Sorting sortImplementation, int algorithmCode) {
		InputGenerator generator = new InputGenerator();
		List<List<Integer>> inputs = generator.generateWorstCases();
		//graph.openSerie();

		Coordinate<Integer,Double> initialCoord = new Coordinate<Integer,Double>(0,0.0, 
				sortImplementation.getClass().getSimpleName(), algorithmCode);
		
		graph.addCoordinate(initialCoord);
		int counter = 0;
		for (List<Integer> list : inputs) {
			if(counter > 0){
				long start = System.nanoTime();
				sortImplementation.sort(list.toArray(new Integer[0]));
				double elapsedTime = (System.nanoTime() - start) / 1000000F;
				Coordinate<Integer,Double> coord = new Coordinate<Integer,Double>(list.size(), elapsedTime, 
						sortImplementation.getClass().getSimpleName(), algorithmCode);
				graph.addCoordinate(coord);
				//graph.addCoordinate((double) list.size(), elapsedTime);
			}
			counter++;
		}

	}
	
	public void executionData(Object sortImplementation, int algorithmCode) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		InputGenerator generator = new InputGenerator();
		List<List<Integer>> inputs = generator.generateWorstCases();
		//graph.openSerie();

		Coordinate<Integer,Double> initialCoord = new Coordinate<Integer,Double>(0,0.0, 
				sortImplementation.getClass().getSimpleName(), algorithmCode);
		
		graph.addCoordinate(initialCoord);
		int counter = 0;
		for (List<Integer> list : inputs) {
			if(counter > 0){
				Method sortMethod = getSortMethod(sortImplementation);
				Parameter[] paramtype = getParameterType(sortMethod);
				long start = System.nanoTime();
				//sortImplementation.sort(list.toArray(new Integer[0]));
				sortMethod.invoke(sortImplementation, new Object[] { list.toArray(new Integer[0]) });
				double elapsedTime = (System.nanoTime() - start) / 1000000F;
				Coordinate<Integer,Double> coord = new Coordinate<Integer,Double>(list.size(), elapsedTime, 
						sortImplementation.getClass().getSimpleName(), algorithmCode);
				graph.addCoordinate(coord);
				//graph.addCoordinate((double) list.size(), elapsedTime);
			}
			counter++;
		}

	}
	
	private Method getSortMethod(Object sortImplementation){
		Method result = null;
		Method[] methods = sortImplementation.getClass().getSuperclass().getDeclaredMethods();
		
		for(Method mt : methods){
			//String mtName = mt.toGenericString();
			String name = mt.getName();
			int parameters = mt.getParameterCount();
			
			if(name.startsWith("sort") && parameters == 1){
				result = mt;
				break;
			}
		}
		if(result == null){
			throw new RuntimeException("Method sort not found in object: " + sortImplementation.getClass().getName());
		}
		return result;
	}
	
	private Parameter[] getParameterType(Method method){
		return method.getParameters();
	}


	public void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}
}