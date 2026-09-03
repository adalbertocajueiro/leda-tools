package br.edu.ufcg.leda;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import br.edu.ufcg.leda.commons.user.Student;
import br.edu.ufcg.leda.util.Util;

public class TestUtil {

	@Test
	public void testUtil01() throws IOException, URISyntaxException {
		String semester = Util.getCurrentSemester("http://localhost:8080/api/geral/getCurrentSemester");
		System.out.println("Semester returned: " + semester);
	}
	
	@Test
	public void testUtil02() throws IOException, URISyntaxException{
		Map<Integer, List<Student>>  alunos = Util.getAllStudents("2026.2", "http://localhost:8080/api/alunos/allStudentsGroupedByClass");
		System.out.println("Students returned: " + alunos);
	}
}
