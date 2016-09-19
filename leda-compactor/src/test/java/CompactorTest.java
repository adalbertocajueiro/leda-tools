import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;

import br.edu.ufcg.ccc.leda.util.Util;
import junit.framework.TestCase;

public class CompactorTest extends TestCase {
	@VisibleForTesting
	public void test(){
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
	}
}
