package br.edu.ufcg.ccc.leda.runner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class DrawerTest {
	public static void main(String[] args) throws IllegalArgumentException, InvocationTargetException {
		String folderName = "D:\\tmp\\jsonData";
		File folder = new File(folderName);
		if(!folder.exists()){
			folder.mkdirs();
		}
		Drawer drawer = new Drawer(folder);
		
		String[] qualifiedNames = {"sorting.simpleSorting.BubbleSort","sorting.simpleSorting.SelectionSort","sorting.simpleSorting.InsertionSort"};
		List<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader loader = DrawerTest.class.getClassLoader();
		
		for (String string : qualifiedNames) {
			try {
				Class<?> loaded = Class.forName(string,true,loader);
				classes.add(loaded);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
		drawer.setSortingList(classes);
		try {
			drawer.instantiateAndRunImplementations();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
