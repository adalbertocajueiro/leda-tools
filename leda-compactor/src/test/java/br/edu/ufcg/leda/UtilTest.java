package br.edu.ufcg.leda;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.edu.ufcg.leda.util.Util;

public class UtilTest {

	@Test
	public void testUtil01(){
		List<File> files = Util.getFiles(new File("D:\\trash2\\p3\\subs\\115111901-Alice"), ".java");
		files.forEach(f -> {
			UserDefinedFileAttributeView view = Files.getFileAttributeView(f.toPath(), UserDefinedFileAttributeView.class);
			try {
				System.out.println(view.list());
				System.out.println(f.getAbsolutePath() + " tem atributo author: " + (view.list().contains(Util.AUTOR_MATRICULA)?Util.readAuthor(f.toPath()):"null"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
