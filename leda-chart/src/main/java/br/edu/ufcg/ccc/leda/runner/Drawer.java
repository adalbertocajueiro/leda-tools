package br.edu.ufcg.ccc.leda.runner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import br.edu.ufcg.ccc.leda.graph.GraphData;
import br.edu.ufcg.ccc.leda.graph.Point;
import br.edu.ufcg.ccc.leda.graph.Serie;
import br.edu.ufcg.ccc.leda.util.InputGenerator;
import br.edu.ufcg.ccc.leda.util.Utilities;

public class Drawer {

	private List<Class<?>> sortingList;

	private List<Object> sortingImplementations;

	/**
	 * Objeto Graph que irá gerar o grafico
	 */
	private GraphData<Integer, Double> graphData;

	private File targetFolder;

	private ClassLoader classLoader;

	/**
	 * Construtor Default
	 */
	public Drawer(File targetFolder) {
		setTargetFolder(targetFolder);
		sortingImplementations = new ArrayList<Object>();
		graphData = new GraphData<Integer, Double>();
	}

	public List<Class<?>> getSortingList() {
		return sortingList;
	}

	public void setSortingList(List<Class<?>> sortingList) {
		this.sortingList = sortingList;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void instantiateAndRunImplementations()
			throws InstantiationException, IllegalAccessException, IOException,
			IllegalArgumentException, InvocationTargetException {
		int colorCode = 0;
		// System.out.println("Sorting list size: " + sortingList.size());
		for (Class<?> sortingClass : sortingList) {
			Object sortImplementation = (Object) sortingClass.newInstance();
			Serie<Integer, Double> serie = new Serie<Integer, Double>(
					sortImplementation.getClass().getSimpleName(), colorCode);
			executionData(sortImplementation, serie);
			graphData.addSerie(serie);
			colorCode++;
		}
		File webFolder = Utilities.createWebFolder(targetFolder);
		// System.out.println("Web folder created: " +
		// webFolder.getAbsolutePath());
		Utilities.addDataToFinalJavaScript(webFolder, graphData.toString());

		// graph.createJson(webFolder);
	}

	public void executionData(Object sortImplementation,
			Serie<Integer, Double> serie) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		InputGenerator generator = new InputGenerator();
		List<List<Integer>> inputs = generator.generateWorstCases();

		Method sortMethod = getSortMethod(sortImplementation);
		// System.out.println("NUMBER OF INPUTS: " + inputs.size());
		for (List<Integer> list : inputs) {
			long start = System.nanoTime();
			// sortMethod.invoke(sortImplementation, new Object[] {
			// list.toArray(new Integer[0]) });
			double elapsedTime = (System.nanoTime() - start) / 1000000F;
			try {
				SortTimeoutExecutor
						.invoke(sortImplementation, sortMethod, list);
				// sortMethod.invoke(sortImplementation, new Object[] {
				// list.toArray(new Integer[0]) });
				elapsedTime = (System.nanoTime() - start) / 1000000F;
				// TODO no futuro as excexoes podem sumir.
			} catch (UnsupportedOperationException e) {
				System.out.println("UnsupportedOperationException");
				elapsedTime = 0;
			} catch (InterruptedException e) {
				// e.printStackTrace();
			} catch (ExecutionException e) {
				System.out.println("ExecutionException: " + e.getMessage());
				// e.printStackTrace();
				elapsedTime = 0;
			} catch (TimeoutException e) {
				// e.printStackTrace();
				elapsedTime = SortTimeoutExecutor.TIMEOUT;
			}

			Point<Integer, Double> point = new Point<Integer, Double>(
					list.size(), elapsedTime);
			serie.add(point);
		}
	}

	private void invoke(Object sortImplementation, Method sortMethod,
			List<Integer> list) throws IllegalAccessException,
			InvocationTargetException {
		sortMethod.invoke(sortImplementation,
				new Object[] { list.toArray(new Integer[0]) });
	}

	private Method getSortMethod(Object sortImplementation) {
		Method result = null;
		Method[] methods = sortImplementation.getClass().getSuperclass()
				.getDeclaredMethods();

		for (Method mt : methods) {
			// String mtName = mt.toGenericString();
			String name = mt.getName();
			int parameters = mt.getParameterCount();

			if (name.startsWith("sort") && parameters == 1) {
				result = mt;
				break;
			}
		}
		if (result == null) {
			throw new RuntimeException("Method sort not found in object: "
					+ sortImplementation.getClass().getName());
		}
		return result;
	}

	private Parameter[] getParameterType(Method method) {
		return method.getParameters();
	}

	public GraphData<Integer, Double> getGraphData() {
		return graphData;
	}

	public void setGraphData(GraphData<Integer, Double> graphData) {
		this.graphData = graphData;
	}

	public File getTargetFolder() {
		return targetFolder;
	}

	public void setTargetFolder(File targetFolder) {
		this.targetFolder = targetFolder;
	}

}