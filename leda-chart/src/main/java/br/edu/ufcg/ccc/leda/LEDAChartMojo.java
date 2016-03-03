package br.edu.ufcg.ccc.leda;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import br.edu.ufcg.ccc.leda.runner.Drawer;
import br.edu.ufcg.ccc.leda.util.Utilities;

/**
 * Goal which generates a performance chart of student's sorting implementations
 * at compile.
 *
 * @goal generateChart
 * 
 */
// @Mojo( name = "generateChart")
public class LEDAChartMojo extends AbstractMojo {

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter
	 * @required
	 */
	private List<String> qualifiedNames;

	/*
	 * @parameter
	 * @required
	 */
	//private String sortingInterface;

	private List<Class<?>> classes;
	
	public void execute() throws MojoExecutionException {
		//String[] listOfNames = new String[qualifiedNames.size()];
		classes = new ArrayList<Class<?>>();
		
		System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
		//File targetFolder = new File(this.project.getBuild().getDirectory() + File.separator + Utilities.WEB_FOLDER);
		File targetFolder = new File(this.project.getBuild().getDirectory());
		System.out.println("Target folder: " + targetFolder.getAbsolutePath());
		if(!targetFolder.exists()){
			targetFolder.mkdirs();
		}
		ClassLoader loader = getClassesPath();
		Drawer drawer = new Drawer(targetFolder);

		
		for (String string : qualifiedNames) {
			try {
				
				Class<?> loaded = Class.forName(string,true,loader);
				classes.add(loaded);
				drawer.setSortingList(classes);
				drawer.instantiateAndRunImplementations();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new MojoExecutionException("Informed class could not be instantiated", e);
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new MojoExecutionException("Instantiation error", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new MojoExecutionException("Illegal Access Error. Problems executing newInstance()", e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new MojoExecutionException("IO error. Problems creating folder and files", e);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new MojoExecutionException("Illegal argument. Problems in arguments in method call", e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new MojoExecutionException("Invocation error. Problems invoking method", e);
			}
		}
		
		 try {
			Utilities.createWebFolder(targetFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//openBrowser(targetFolder);
	}

	private URLClassLoader getClassesPath() throws MojoExecutionException {
		List<String> classesPath = null;
		try {
			classesPath = project.getCompileClasspathElements();

			List<URL> projectClasspathList = new ArrayList<URL>();
			for (String element : classesPath) {
				try {
					projectClasspathList.add(new File(element).toURI().toURL());
				} catch (Exception e) {
					throw new MojoExecutionException(element + " is an invalid classpath element", e);
				}
			}

			URLClassLoader loader = new URLClassLoader(projectClasspathList.toArray(new URL[0]));

			return loader;

		} catch (DependencyResolutionRequiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void openBrowser(File targetFolder){
		Desktop desktop = null;
		if(Desktop.isDesktopSupported()){
			desktop = Desktop.getDesktop();
			if(desktop != null && desktop.isSupported(Desktop.Action.BROWSE)){
				try {
		        	File file = new File(targetFolder,Utilities.HTML_FILE_NAME);
		            desktop.browse(file.toURI());
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}
		}
	}
}
