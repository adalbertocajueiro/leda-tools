package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;
import java.util.Map;

public class UtilTest {
	public static void main(String[] args) throws URISyntaxException, IOException {
		//Path file = new File("D:\\trash2\\BSTVerifiable.java").toPath();
		//Path file = Paths.get(FileAttributeUtil.class.getResource("README.md").toURI()).toAbsolutePath();
	    //UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
	    //List<String> att = view.list();
	    //att.forEach(s -> System.out.println(s));
	    //Util.writeAuthor(file,"123456");
		//System.out.println(Util.readAuthor(file));
		List<File> files = Util.getFiles(new File("/home/ubuntu/leda-upload/2016.1/R15-02/subs/115110912-ITALO AGUIAR DANTAS/src/main/java"), ".java");
		files.forEach(f -> {
			UserDefinedFileAttributeView view = Files.getFileAttributeView(f.toPath(), UserDefinedFileAttributeView.class);
			try {
				System.out.println(view.list());
				System.out.println(f.getAbsolutePath() + " tem atributo author: " + (view.list().contains(Util.AUTOR_MATRICULA)?Util.readAuthor(f.toPath()):"null"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
/*		Util.addAuthorToFiles(files, "43210", "Fulanode tal");//nao vai mudar se ja contiver a informação de autor
		files.forEach(f -> {
			UserDefinedFileAttributeView view = Files.getFileAttributeView(f.toPath(), UserDefinedFileAttributeView.class);
			try {
				System.out.println(f.getAbsolutePath() + " tem atributo author: " + (view.list().contains(Util.AUTOR_MATRICULA)?Util.readAuthor(f.toPath()):"null"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
*/		
		//Map<String,Student> alunos = Util.getAllStudents("http://localhost:8888/alunosJson");
		//System.out.println(alunos.size());
	}

}
