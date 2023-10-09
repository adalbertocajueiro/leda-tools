package br.edu.ufcg.leda;

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
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jdom2.JDOMException;

import br.edu.ufcg.leda.commons.user.Student;
import br.edu.ufcg.leda.util.MavenUtility;
import br.edu.ufcg.leda.util.Util;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Mojo(name = "single-correct",defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class LEDAIndividualCorrectionMojo extends AbstractMojo {

	@Parameter(property = "project",defaultValue = "${project}",required = true, readonly = true)
	private MavenProject project;

	@Parameter(property = "submissionsDirectory", required = true)
	private File submissionsDirectory;

	@Parameter(property = "correctionEnvZipFile", required = true)
	private File correctionEnvZipFile;

	@Parameter(property = "matricula", required = true)
	private File matricula;

	@Parameter(property = "mavenHomeFolder", required = true)
	private File mavenHomeFolder;

	@Parameter(property = "testCasesJarFileName", required = true)
	private String testCasesJarFileName;

	@Parameter(property = "testSuiteClassName", required = true)
	private String testSuiteClassName;

	@Parameter(property = "fileNames", required = true)
	private List<String> fileNames;

	@Parameter(property = "urlCurrentSemester", required = true)
	private String urlCurrentSemester;

	@Parameter(property = "urlGetAllStudents", required = true)
	private String urlGetAllStudents;

	private Map<Integer,List<Student>> alunos;
	
	public void execute() throws MojoExecutionException {

		System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
		System.out.println("Project artifactId: "
				+ this.project.getArtifactId());
		System.out.println("Project name: " + this.project.getName());
		System.out.println("Submissions directory: "
				+ submissionsDirectory.getAbsolutePath());
		System.out.println("Environment zip file: "
				+ this.correctionEnvZipFile.getAbsolutePath());
		System.out.println("Test cases Jar file: " + this.testCasesJarFileName);
		System.out.println("Test suite class name: " + this.testSuiteClassName);
		System.out.println("Maven home folder: "
				+ mavenHomeFolder.getAbsolutePath());
		System.out.println("Files to overwrite:");
		for (String fileName : fileNames) {
			System.out.println(fileName);
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n");
		String currentSemester = "";
		try {
			currentSemester = Util.getCurrentSemester(urlCurrentSemester);
			System.out.println("At time: " + (new GregorianCalendar()).getTime());
			alunos = new HashMap<Integer,List<Student>>();
			try {
				alunos = Util.getAllStudents(currentSemester,urlGetAllStudents);
			} catch (IOException e2) {
				throw new MojoExecutionException(e2.getMessage(),e2);
			}

		} catch (Exception e) {
			throw new MojoExecutionException(e);
		} 
		System.out.println("GENERATING MAVEN PROJECTS FOR EACH STUDENT");

		MavenUtility mu = new MavenUtility(submissionsDirectory,
				mavenHomeFolder, this.project.getArtifactId(),
				this.project.getName(), this.testCasesJarFileName,
				this.testSuiteClassName);

		if(submissionsDirectory.exists()){
			File[] submissions = submissionsDirectory.listFiles(new FileFilter() {
	
				@Override
				public boolean accept(File pathname) {
					boolean result = false;
					result = pathname.getName().endsWith("zip")
							|| pathname.getName().endsWith("ZIP");
					return result;
				}
			});
			ArrayList<File> folders = new ArrayList<File>();
			if (submissions.length > 0) {
				for (int i = 0; i < submissions.length; i++) {
					File studZipFile = submissions[i];
	
					try {
						// File projectFolder =
						// mu.createCompleteProjectFolder(environmentZipFile,
						// studentZipFile, fileNames);
						File projectFolder = mu.createCompleteProjectFolder(
								correctionEnvZipFile, studZipFile, fileNames);
						System.out
								.println("CREATING MAVEN PROJECT FOLDER: "
										+ projectFolder.getAbsolutePath());
						folders.add(projectFolder);
						// run maven
						//mu.executeMaven(projectFolder);
					} catch (Exception e) {
						e.printStackTrace();
						throw new MojoExecutionException(
								"Error creating project folder", e);
					}
				}
				for (File projectFolder : folders) {
					try {
						System.out
								.println("RUNNING MAVEN FOR FOLDER: "
										+ projectFolder.getAbsolutePath());
						// run maven
						mu.executeMaven(projectFolder);
					} catch (Exception e) {
						e.printStackTrace();
						throw new MojoExecutionException(
								"Error creating project folder", e);
					}
				}
				
				System.out.println("GENERATING TEST REPORT IN JSON");
				File targetFolder = new File(this.project.getBuild().getDirectory());
				try {
					mu.generateReport(submissionsDirectory, targetFolder,alunos);
				} catch (IOException | JDOMException e) {
					e.printStackTrace();
					throw new MojoExecutionException("Error generating report", e);
				}
	
			} else {
				throw new MojoExecutionException(
						"Submissions folder does not contain zip files");
			}
		}else{
			throw new MojoExecutionException(
					"Submission directory: " + submissionsDirectory.getAbsolutePath() + "does not exist");
		}

	}
}
