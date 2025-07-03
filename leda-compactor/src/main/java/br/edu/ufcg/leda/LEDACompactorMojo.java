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
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import br.edu.ufcg.leda.commons.user.Student;
import br.edu.ufcg.leda.sender.StudentSubmissionSender;
import br.edu.ufcg.leda.util.Compactor;
import br.edu.ufcg.leda.util.Util;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Mojo(name = "compact",defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class LEDACompactorMojo extends AbstractMojo {

	@Parameter(property = "project",defaultValue = "${project}",required = true, readonly = true)
	private MavenProject project;

	@Parameter(property = "matricula", required = true)
	private String matricula;

	@Parameter(property = "roteiro", required = true)
	private String roteiro;

	@Parameter(property = "urlCurrentSemester", required = true)
	private String urlCurrentSemester;

	@Parameter(property = "urlGetAllStudents", required = true)
	private String urlGetAllStudents;

	@Parameter(property = "urlSubmit", required = true)
	private String urlSubmit;

	private StudentSubmissionSender sender;

	
	public void execute() throws MojoExecutionException {

		System.out.println("%%%%%%%%%% Parameters %%%%%%%%%%");
		System.out.println("Folder to be compacted: "
				+ project.getBuild().getSourceDirectory());

		//faz validação para ver se estudante esta cadastrado e na turma correta
		System.out.println("Checking matricula and turma");
		List<Student> alunos = new LinkedList<Student>();
		String currentSemester = "";
		try {
			currentSemester = Util.getCurrentSemester(urlCurrentSemester);
			System.out.println("MOJO: current semester received: " + currentSemester);
			System.out.println("MOJO: getting all students: " + urlGetAllStudents);
			alunos = Util.getAllStudents(currentSemester,urlGetAllStudents)
					.values()
					.stream()
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
			System.out.println("MOJO: all students received: " + alunos.size());
		} catch (IOException e2) {
			throw new MojoExecutionException("\n CONNECTION ERROR: " + e2.getMessage(),e2);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//se acontecer da matricula nao estiver cadastrada nem o aluno cadastrado na turma correta
		Student aluno = alunos.stream().findFirst().orElse(null);
		Integer turma = Integer.parseInt(roteiro.substring(4));
		if(aluno == null){
			throw new MojoExecutionException("Aluno " + matricula + " nao cadastrado");
		}else if (aluno.getTurma() != turma){
			throw new MojoExecutionException("Aluno " + matricula + " nao pertence a turma " + turma);			
		}
		Compactor compactor = new Compactor();
		File srcFolder = new File(project.getBuild().getSourceDirectory());

		//Talvez precise isntanciar aqui um header com dados do usuario professor logado.

		//TODO poderia fazer algumas validacoes na matricula e turma antes de compactar
		//System.out.println("Injecting author information...");
		//List<File> files = Util.getFiles(srcFolder, ".java");
		//Map<String,String> filesOwners = new HashMap<String,String>();
		//try {
		//	filesOwners = Util.addAuthorToFiles(files, aluno.getMatricula(), aluno.getNome());
		//} catch (IOException e1) {
		//	System.out.println("Author information could not be injected: " + e1.getMessage());
		//}
		// System.out.println("Source folder: " + srcFolder);
		File destZipFile = new File(project.getBuild().getDirectory(),
				matricula + ".zip");
		try {
			compactor.zipFolder(srcFolder, destZipFile);
			System.out.println("Compaction sucess: " + destZipFile.getName());
			sender = new StudentSubmissionSender(destZipFile, matricula,
					currentSemester, roteiro, urlSubmit);
			System.out.println("Submitting file " + destZipFile.getName()
					+ " to " + urlSubmit);
			sender.send();
			System.out
					.println("Please check your log file to see the confirmation from the server (last record)");
		} catch (IOException e) {
			// e.printStackTrace();
			throw new MojoExecutionException("\n COMPACTION ERROR", e);
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	}
}
