package br.edu.ufcg.leda;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;


public class TestCompactorMojo {

	@Test
	public void testCompact01() throws IOException, MojoExecutionException{

		MavenProject mp = new MavenProject();
        mp.getBuild().setDirectory("/Users/adalbertocajueiro/Downloads/tmp/Rot-HeapBinaria-environment/target");
        mp.getBuild().setSourceDirectory("/Users/adalbertocajueiro/Downloads/tmp/Rot-HeapBinaria-environment/src");
		mp.setArtifactId("submission");

		LEDACompactorMojo mojo = new LEDACompactorMojo();
		mojo.setProject(mp);
		mojo.setMatricula("118210879");
		mojo.setRoteiro("R12-01");
		mojo.setUrlCurrentSemester("http://localhost:8080/geral/getCurrentSemester");
		mojo.setUrlGetAllStudents("http://localhost:8080/alunos//allStudentsGroupedByClass");
		mojo.setUrlSubmit("http://localhost:8080/submissoes/saveSubmission");
		mojo.execute();
	}
}
