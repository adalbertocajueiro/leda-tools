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

import br.edu.ufcg.ccc.leda.util.Compactor;
import br.edu.ufcg.ccc.leda.util.StudentSubmissionSender;

import java.io.File;
import java.io.IOException;

/**
 * Goal which compacts a student's submission.
 *
 * @goal compact
 * 
 * @phase process-sources
 */
public class LEDACompactorMojo extends AbstractMojo {

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
	private String matricula;

	/**
	 * @parameter
	 * @required
	 */
	private String semestre;

	/**
	 * @parameter
	 * @required
	 */
	private String roteiro;

	/**
	 * @parameter
	 * @required
	 */
	private String url;

	private StudentSubmissionSender sender;

	public void execute() throws MojoExecutionException {

		System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
		System.out.println("Folder to be compacted: "
				+ project.getBuild().getSourceDirectory());

		Compactor compactor = new Compactor();
		File srcFolder = new File(project.getBuild().getSourceDirectory());
		// System.out.println("Source folder: " + srcFolder);
		File destZipFile = new File(project.getBuild().getDirectory(),
				matricula + ".zip");
		try {
			compactor.zipFolder(srcFolder, destZipFile);
			System.out.println("Compaction sucess: " + destZipFile.getName());
			sender = new StudentSubmissionSender(destZipFile, matricula,
					semestre, roteiro, url);
			System.out.println("Submitting file " + destZipFile.getName()
					+ " to " + url);
			sender.send();
			System.out
					.println("Please check your log file to see the confirmation from the server (last record)");
		} catch (ClientProtocolException e) {
			throw new MojoExecutionException("Send error", e);
		} catch (IOException e) {
			// e.printStackTrace();
			throw new MojoExecutionException("Compaction error", e);
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	}
}
