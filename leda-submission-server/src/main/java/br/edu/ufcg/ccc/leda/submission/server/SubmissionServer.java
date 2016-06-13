package br.edu.ufcg.ccc.leda.submission.server;

import org.jooby.Jooby;
import org.jooby.Upload;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;

/**
 * @author jooby generator
 */
public class SubmissionServer extends Jooby {
	
	public Path  saveUpload(File f, String folder) throws IOException{
		File fout = new File(folder + File.separator + f.getName().substring(f.getName().indexOf(".") + 1));
		return Files.move(f.toPath(), fout.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

  {
    get("/", () -> "Hello World!");
	
	get("/teste", () -> "Hello World Teste!");
	
  }

  {
	post("/submit", req -> "File " + req.param("bin") + "  was received from " + req.param("name"));  
	
	post("/submit2",(req,resp) -> {
      String name = req.param("name").value();
      Upload upload = req.param("bin").toUpload();
	  System.out.println("upload " + upload);
	  File f = upload.file();
	  System.out.println("file: " + f.getName().substring(f.getName().indexOf(".") + 1));
	  Path moved = saveUpload(f,"D:\\tmp");
	  System.out.println("moved to: " + moved);
      resp.send(name + " " + upload.name() + " " + upload.type());
    });
  }
  
  public static void main(final String[] args) throws Throwable {
    run(SubmissionServer::new, args);
  }

}
