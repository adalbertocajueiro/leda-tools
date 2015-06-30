package br.edu.ufcg.ccc.leda.chart;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which corrects a student's submission.
 *
 * @goal drawchart
 * 
 * @phase process-sources
 */
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
    private String studentName;
    
    public void execute() throws MojoExecutionException {
    	
    	System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
    	System.out.println("Folder to be compacted: " + project.getBuild().getSourceDirectory());
    	System.out.println("Student name: " + this.studentName);
    	System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n");
    	
    	/*
    	Compactor compactor = new Compactor();
    	File srcFolder = new File(project.getBuild().getSourceDirectory());
    	//System.out.println("Source folder: " + srcFolder);
    	File destZipFile = new File(project.getBuild().getDirectory(),studentName + ".zip");
    	try {
			compactor.zipFolder(srcFolder, destZipFile);
			System.out.println("File to be sent: " + destZipFile);
		} catch (Exception e) {
			//e.printStackTrace();
			throw new MojoExecutionException("Compaction error", e);
		}
    	*/
    }
}
