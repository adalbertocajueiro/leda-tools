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
import java.io.IOException;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import br.edu.ufcg.leda.sender.ProfessorSender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Mojo(name = "enviar-roteiro",defaultPhase = LifecyclePhase.INSTALL)
public class LEDARoteiroSenderMojo extends AbstractMojo {

	@Parameter(property = "project",defaultValue = "${project}",required = true, readonly = true)
	private MavenProject project;

	@Parameter(property = "roteiro",required = true)
	private String roteiro;

	@Parameter(property = "semestre",required = true)
	private String semestre;

	@Parameter(property = "defaultSend",required = true)
	private boolean defaultSend;

	@Parameter(property = "url",required = true)
	private String url;

	@Parameter(property = "userName",required = true)
	private String userName;

	private ProfessorSender sender;

	public void execute() throws MojoExecutionException {
		if (defaultSend) {
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");
			File targetFolder = new File(project.getBuild().getDirectory());
			String environmentName = project.getArtifactId()
					+ "-environment.zip";
			String correctionProjectName = project.getArtifactId()
					+ "-correction-proj.zip";

			File guiaCorrecaoFile = new File(project.getBuild().getOutputDirectory(), "Guia de correcao.pdf");

			File envZipFile = new File(targetFolder, environmentName);
			File corrProjZipFile = new File(targetFolder, correctionProjectName);
			try {

				sender = new ProfessorSender(envZipFile, corrProjZipFile,roteiro, url, semestre,guiaCorrecaoFile,userName);
				System.out.println("Submitting environment file: "
						+ envZipFile.getAbsolutePath() + " to " + url);
				System.out.println("Submitting correction file: "
						+ corrProjZipFile.getAbsolutePath() + " to " + url);
				sender.send();
				System.out
						.println("Please check your log file to see the confirmation from the server (last record)");
			}  catch (IOException e) {
				// e.printStackTrace();
				throw new MojoExecutionException("Sender error", e);
			}
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}

	}
}
