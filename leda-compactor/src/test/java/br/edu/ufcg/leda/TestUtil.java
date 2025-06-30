package br.edu.ufcg.leda;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import br.edu.ufcg.leda.util.Util;

public class TestUtil {

	@Test
	public void testUtil01() throws IOException, URISyntaxException{
		String semester = Util.getCurrentSemester("http://localhost:8080/api/geral/getCurrentSemester");
		System.out.println("Semester returned: " + semester);
	}
}
