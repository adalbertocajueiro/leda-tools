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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import br.edu.ufcg.ccc.leda.util.Compactor;
import br.edu.ufcg.ccc.leda.util.StudentSubmissionSender;
import br.edu.ufcg.ccc.leda.util.Util;
import br.edu.ufcg.ccc.leda.util.Student;

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

	private String pathAllStudents = "alunosJson";
	
	public void execute() throws MojoExecutionException {

		System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
		System.out.println("Folder to be compacted: "
				+ project.getBuild().getSourceDirectory());

		//faz validação para ver se estudante esta cadastrado e na turma correta
		System.out.println("Checking matricula and turma");
		String urlAllStudents = url.substring(0,url.lastIndexOf('/') + 1) + pathAllStudents;
		Map<String,Student> alunos = new HashMap<String,Student>();
		try {
			alunos = Util.getAllStudents(urlAllStudents);
		} catch (IOException e2) {
			throw new MojoExecutionException(e2.getMessage(),e2);
		}
		//se acontecer da matricula nao estiver cadastrada nem o aluno cadastrado na turma correta
		Student aluno = alunos.get(matricula);
		String turma = roteiro.substring(4);
		if(aluno == null){
			throw new MojoExecutionException("Aluno " + matricula + " nao cadastrado");
		}else if (!aluno.getTurma().equals(turma)){
			throw new MojoExecutionException("Aluno " + matricula + " nao pertence a turma " + turma);			
		}
		Compactor compactor = new Compactor();
		File srcFolder = new File(project.getBuild().getSourceDirectory());
		//TODO poderia fazer algumas validacoes na matricula e turma antes de compactar
		System.out.println("Injecting author information...");
		List<File> files = Util.getFiles(srcFolder, ".java");
		try {
			Util.addAuthorToFiles(files, aluno.getMatricula(), aluno.getNome());
		} catch (IOException e1) {
			System.out.println("Author information could not be injected: " + e1.getMessage());
			//e1.printStackTrace();
		}
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
