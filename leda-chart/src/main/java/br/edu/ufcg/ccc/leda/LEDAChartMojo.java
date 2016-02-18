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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import br.edu.ufcg.ccc.leda.runner.Drawer;


/**
 * Goal which generates a performance chart of student's sorting
 * implementations at compile.
 *
 * @goal generateChart
 * 
 */
//@Mojo( name = "generateChart")
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
	
	/**
	 * @parameter
	 * @required
	 */
	private String sortingInterface;


	public void execute() throws MojoExecutionException {
		String[] listOfNames = new String[qualifiedNames.size()]; 
		
		System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
    	System.out.println("Folder to be compacted: " + project.getBuild().getDirectory());
		
		for (int i = 0; i < listOfNames.length; i++) {
			listOfNames[i] = qualifiedNames.get(i);
			//System.out.println(listOfNames[i]);
			try {
				System.out.println(getClassesPath().loadClass(listOfNames[i]).getName());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		File targetFolder = new File(this.project.getBuild().getDirectory() + "/target/");
		
		
		
		Drawer drawer  = new Drawer(targetFolder);
		
		drawer.addSortingImplementation(listOfNames);
		drawer.extractImplemantation(sortingInterface,getClassesPath());
	}
	
	private URLClassLoader getClassesPath() throws MojoExecutionException{
		List<String> classesPath = null;
		try {
			classesPath =		project.getCompileClasspathElements();
			
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
}
