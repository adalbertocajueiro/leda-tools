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
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.jdom2.JDOMException;

import br.edu.ufcg.ccc.leda.util.MavenUtility;

/**
 * Goal which corrects a student's submission.
 *
 * @goal correct
 * 
 * @phase process-sources
 */
public class LEDACorrectionMojo extends AbstractMojo {
	
	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	
	/**
     * Directory where all submissions (zip files) are located). This directory will be used to 
     * unpack all submissions (working directory).
     * @parameter submissionsDirectory
     * @required
     */
    private File submissionsDirectory;

    /**
     * File containing the environment given to the students. It is used to generate the basic directory 
     * for any submission (project directory of any submission). 
     * @parameter environmentZipFile
     * @required
     */
    private File correctionEnvZipFile;
   
    /**
     * Maven installation directory. 
     * @parameter
     * @required
     */
    private File mavenHomeFolder;
    
    /**
     * @parameter 
     * @required
     */
    private String testCasesJarFileName;
    
    /**
     * The suite class name;
     * @parameter 
     * @required
     */
    private String testSuiteClassName;
    
    /**
     * A list of file names to overwrite old ones.
     * @parameter 
     * @required
     */
    private List<String> fileNames;
    
    public void execute() throws MojoExecutionException {
    	
    	System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
    	System.out.println("Project artifactId: " + this.project.getArtifactId());
    	System.out.println("Project name: " + this.project.getName());
    	System.out.println("Submissions directory: " + submissionsDirectory.getAbsolutePath());
    	System.out.println("Environment zip file: " + this.correctionEnvZipFile.getAbsolutePath());
    	System.out.println("Test cases Jar file: " + this.testCasesJarFileName);
    	System.out.println("Test suite class name: " + this.testSuiteClassName);
    	System.out.println("Maven home folder: " + mavenHomeFolder.getAbsolutePath());
    	System.out.println("Files to overwrite:");
    	for (String fileName : fileNames) {
			System.out.println(fileName);
		}
    	System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n");
    	System.out.println("Generating project folder");
    	
    	
    	MavenUtility mu = new MavenUtility(submissionsDirectory, mavenHomeFolder, 
    									   this.project.getArtifactId(), 
    									   this.project.getName(),
    									   this.testCasesJarFileName,
    									   this.testSuiteClassName);
    	
    	File[] submissions = submissionsDirectory.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				boolean result = false;
				result = pathname.getName().endsWith("zip") || pathname.getName().endsWith("ZIP");
				return result;
			}
		});
    	
    	if(submissions.length > 0){
    		for (int i = 0; i < submissions.length; i++) {
    			File studZipFile = submissions[i];
    			
    			try {
        			//File projectFolder = mu.createCompleteProjectFolder(environmentZipFile, studentZipFile, fileNames);
    				File projectFolder = mu.createCompleteProjectFolder(correctionEnvZipFile, studZipFile, fileNames);
    			
        			//run maven
        			mu.executeMaven(projectFolder);
        		} catch (Exception e) {
        			e.printStackTrace();
        			throw new MojoExecutionException("Error creating project folder", e);
        		}
			}
    		File targetFolder = new File(this.project.getBuild().getDirectory());
			try {
				mu.generateReport(submissionsDirectory, targetFolder);
			} catch (IOException | JDOMException e) {
				e.printStackTrace();
				throw new MojoExecutionException("Error geenrating report", e);
			}
    		 
    	} else {
    		throw new MojoExecutionException("Submissions folder does not contain zip files");
    	}
		
    }
}