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

	/**
	 * @parameter
	 * @required
	 */
	private boolean generateGraph;

	private List<Class<?>> classes;

	public void execute() throws MojoExecutionException {
		System.out.println("GENERATE CHART: " + generateGraph);
		if (generateGraph) {
			// String[] listOfNames = new String[qualifiedNames.size()];
			classes = new ArrayList<Class<?>>();

			System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
			// File targetFolder = new
			// File(this.project.getBuild().getDirectory() + File.separator +
			// Utilities.WEB_FOLDER);
			File targetFolder = new File(this.project.getBuild().getDirectory());
			System.out.println("Target folder: "
					+ targetFolder.getAbsolutePath());
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}
			ClassLoader loader = getClassesPath();
			Drawer drawer = new Drawer(targetFolder);

			for (String string : qualifiedNames) {
				try {

					Class<?> loaded = Class.forName(string, true, loader);
					classes.add(loaded);
					// System.out.println("Classes loaded: " +
					// loaded.getSimpleName());
				} catch (ClassNotFoundException e) {
					// e.printStackTrace();
					throw new MojoExecutionException(
							"Informed class could not be instantiated", e);
				} catch (IllegalArgumentException e) {
					// e.printStackTrace();
					throw new MojoExecutionException(
							"Illegal argument. Problems in arguments in method call",
							e);
				}
			}

			// System.out.println("Classes loaded: " + classes.size());
			try {
				drawer.setSortingList(classes);
				drawer.instantiateAndRunImplementations();
				File webFolder = Utilities.createWebFolder(targetFolder);
				Utilities.addDataToFinalJavaScript(webFolder, drawer
						.getGraphData().toString());
				openBrowser(webFolder);
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new MojoExecutionException("Instantiation error", e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new MojoExecutionException(
						"Illegal Access Error. Problems executing newInstance()",
						e);
			} catch (IOException e) {
				e.printStackTrace();
				throw new MojoExecutionException(
						"IO error. Problems creating folder and files", e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new MojoExecutionException(
						"Invocation error. Problems invoking method", e);
			}
		}

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
					throw new MojoExecutionException(element
							+ " is an invalid classpath element", e);
				}
			}

			URLClassLoader loader = new URLClassLoader(
					projectClasspathList.toArray(new URL[0]));

			return loader;

		} catch (DependencyResolutionRequiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void openBrowser(File webFolder) {
		Desktop desktop = null;
		File file = new File(webFolder, Utilities.HTML_FILE_NAME);
		if (Desktop.isDesktopSupported()) {
			desktop = Desktop.getDesktop();
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(file.toURI());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out
						.println("Desktop is null ("
								+ desktop
								+ ") or desktop.isSupported(Desktop.Action.BROWSE) returned false!");
			}
		} else {
			// System.out.println("Desktop.isDesktopSupported() returned false!");
			// provavelmetne nao eh prataforma windows. tentando abrir em mac
			String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("mac") >= 0) {
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("open " + file.toURI());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// no linux se nao suportar desktop entao o aluno tera que abrir o
			// index.html mesmo
		}
	}
}
