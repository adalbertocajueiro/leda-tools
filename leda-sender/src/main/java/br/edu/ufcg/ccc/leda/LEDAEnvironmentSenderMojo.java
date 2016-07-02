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

import org.apache.http.client.ClientProtocolException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import br.edu.ufcg.ccc.leda.util.ProfessorSender;
import br.edu.ufcg.ccc.leda.util.Sender;

import java.io.File;
import java.io.IOException;


/**
 * Goal which compacts a student's submission.
 *
 * @goal send-environment
 * 
 * @phase install
 */
public class LEDAEnvironmentSenderMojo extends AbstractMojo {
	
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
    private String idRoteiro;
    
    /**
     * @parameter 
     * @required
     */
    private String url;
    
    private Sender sender;
    
    public void execute() throws MojoExecutionException {
    	
    	System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
    	File targetFolder = new File(project.getBuild().getOutputDirectory());
    	String environmentName = project.getArtifactId() + "-environment.zip";
    	//System.out.println("Source folder: " + srcFolder);
    	File envZipFile = new File(targetFolder,environmentName);
    	try {
			sender = new ProfessorSender(envZipFile,idRoteiro,url);
			System.out.println("Submitting environment file: " + envZipFile.getAbsolutePath());
			sender.send();
			System.out.println("Please check your log file to see the confirmation from the server (last record)");
		} catch (ClientProtocolException e){
			throw new MojoExecutionException("Send error", e);
		} catch (IOException e) {
			//e.printStackTrace();
			throw new MojoExecutionException("Compaction error", e);
		} 
    	System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }
}
