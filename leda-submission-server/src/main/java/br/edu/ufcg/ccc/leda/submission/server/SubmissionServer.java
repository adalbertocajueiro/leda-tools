package br.edu.ufcg.ccc.leda.submission.server;

import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Upload;

import br.edu.ufcg.ccc.leda.submission.util.ConfigurationException;
import br.edu.ufcg.ccc.leda.submission.util.FileUtilities;
import br.edu.ufcg.ccc.leda.submission.util.StudentException;
import br.edu.ufcg.ccc.leda.submission.util.UploadConfiguration;

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
	
	get("/roteiro",(req,resp) -> {
		String rId = req.param("numeroRoteiro").value();
		System.out.println("Id do roteiro: " + rId);
		File fileToSend = new File("D:\\trash\\roteiros\\Rot-SimpleSorting-Bidirectional-Bubble-environment.zip");
		resp.type(MediaType.octetstream);
	    resp.download(fileToSend);
	});
	
  }

  {
	post("/submit", req -> "File " + req.param("bin") + "  was received from " + req.param("name"));  
	
	
	
	post("/submit3",(req,resp) -> {
	      String matricula = req.param("matricula").value();
	      String semestre = req.param("semestre").value();
	      String turma = req.param("turma").value();
	      String roteiro = req.param("roteiro").value();
	      Upload upload = req.param("arquivo").toUpload();
		  //System.out.println("upload " + upload);
		  UploadConfiguration config = new UploadConfiguration(matricula, semestre, turma, roteiro);
		  File uploaded = upload.file();
		  String result = "default response";
		  try {
			result = FileUtilities.saveUpload(uploaded, config);
			
		} catch (StudentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage();
			
		}catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage();
			
		}
		resp.send(result);  
	    });
  }
  
  public static void main(final String[] args) throws Throwable {
    run(SubmissionServer::new, args);
  }

}
