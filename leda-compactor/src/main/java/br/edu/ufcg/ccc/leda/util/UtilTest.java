package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class UtilTest {
	public static void main(String[] args) throws URISyntaxException, IOException {
		//Path file = new File("D:\\trash2\\BSTVerifiable.java").toPath();
		//Path file = Paths.get(FileAttributeUtil.class.getResource("README.md").toURI()).toAbsolutePath();
	    //UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
	    //List<String> att = view.list();
	    //att.forEach(s -> System.out.println(s));
	    //Util.writeAuthor(file,"123456");
		//System.out.println(Util.readAuthor(file));
		
		File notUFT8 = new File("D:\\trash2\\skip2\\src\\main\\java\\adt\\skipList\\SkipList.java");
		InputStreamReader r = new InputStreamReader(new FileInputStream(notUFT8));
		System.out.println(r.getEncoding());
		r.close();
		
		Util.changeEncoding(notUFT8, "ISO-8859-1");
		InputStreamReader r1 = new InputStreamReader(new FileInputStream(notUFT8));
		System.out.println(r1.getEncoding());
		r1.close();
		
		//Util.changeEncodingToUFT8(notUFT8, "UTF-8");
		//InputStreamReader r2 = new InputStreamReader(new FileInputStream(notUFT8));
		//System.out.println(r2.getEncoding());
		//r2.close();
		
		/*List<File> files = Util.getFiles(new File("D:\\trash2\\skip2\\src\\main\\java"), ".java");
		files.forEach(f -> {
			UserDefinedFileAttributeView view = Files.getFileAttributeView(f.toPath(), UserDefinedFileAttributeView.class);
			try {
				System.out.println(view.list());
				System.out.println(f.getAbsolutePath() + " tem atributo author: " + (view.list().contains(Util.AUTOR_MATRICULA)?Util.readAuthor(f.toPath()):"null"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});*/
		
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
