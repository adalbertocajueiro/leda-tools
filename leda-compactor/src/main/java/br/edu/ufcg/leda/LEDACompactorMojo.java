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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
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
@Mojo(name = "compact", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class LEDACompactorMojo extends AbstractMojo {

	@Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Parameter(property = "matricula", required = true)
	private String matricula;

	@Parameter(property = "roteiro", required = true)
	private String roteiro;

	@Parameter(property = "urlSemesters", required = true)
	private String urlSemesters;

	@Parameter(property = "urlGetAllStudents", required = true)
	private String urlGetAllStudents;

	@Parameter(property = "urlSubmit", required = true)
	private String urlSubmit;

	private StudentSubmissionSender sender;

	private static final Logger logger = LogManager.getLogger(LEDACompactorMojo.class);

	public void execute() throws MojoFailureException {

		logger.info("COMPACTING FOLDER: " + project.getBuild().getSourceDirectory());

		// faz validação para ver se estudante esta cadastrado e na turma correta
		logger.info("Checking matricula and turma");
		List<Student> alunos = new LinkedList<Student>();
		String currentSemester = "";
		try {
			logger.info("URL DO GET SEMESTERS: " + urlSemesters);
			currentSemester = Util.getCurrentSemester(urlSemesters);
			logger.info("CURRENT SEMESTER: " + currentSemester);
			logger.info("URL TO GET STUDENTS: " + urlGetAllStudents);
			alunos = Util.getAllStudents(currentSemester, urlGetAllStudents)
					.values()
					.stream()
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
			logger.info("NUMBER OF STUDENTS: " + alunos.size());

			Student aluno = alunos.stream().filter(a -> a.getMatricula().equals(matricula)).findFirst().orElse(null);
			Integer turma = Integer.parseInt(roteiro.substring(4));
			if (aluno == null) {
				throw new MojoFailureException("Aluno " + matricula + " nao cadastrado");
			} else if (aluno.getTurma() != turma) {
				throw new MojoFailureException("Aluno " + matricula + " nao pertence a turma " + turma);
			}

			Compactor compactor = new Compactor();
			File srcFolder = new File(project.getBuild().getSourceDirectory());

			// Talvez precise isntanciar aqui um header com dados do usuario professor
			// logado.

			File destZipFile = new File(project.getBuild().getDirectory(),
					matricula + ".zip");
			compactor.zipFolder(srcFolder, destZipFile);
			logger.info("FILE TO SEND: " + destZipFile.getName());
			logger.info("END POINT TO SUBMIT: " + urlSubmit);

			sender = new StudentSubmissionSender(destZipFile, matricula,
					currentSemester, roteiro, urlSubmit);

			sender.send();
			logger.debug("File send! Please check your log file to see the confirmation from the server (last record)");
		} catch (IOException e) {
			logger.warn("ERROR: " + e.getMessage());
			throw new MojoFailureException("\n ERROR: " + e.getMessage(), e);
		} catch (URISyntaxException e) {
			logger.warn("URL ERROR: " + e.getMessage());
			throw new MojoFailureException("\n ERROR in URL: " + e.getMessage(), e);
		} catch (NumberFormatException e) {
			// e.printStackTrace();
			logger.warn("NUMBER FORMAT ERROR WHEN EXTRACTING CLASS: " + e.getMessage());
			throw new MojoFailureException("\n ERROR WHEN EXTRACTING CLASS", e);
		}
	}
}
