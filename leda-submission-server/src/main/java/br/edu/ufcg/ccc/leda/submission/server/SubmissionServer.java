package br.edu.ufcg.ccc.leda.submission.server;

import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Upload;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import br.edu.ufcg.ccc.leda.submission.util.ConfigurationException;
import br.edu.ufcg.ccc.leda.submission.util.FileUtilities;
import br.edu.ufcg.ccc.leda.submission.util.ProfessorUploadConfiguration;
import br.edu.ufcg.ccc.leda.submission.util.RoteiroException;
import br.edu.ufcg.ccc.leda.submission.util.StudentException;
import br.edu.ufcg.ccc.leda.submission.util.StudentUploadConfiguration;

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
	
	get("/conf", (req,resp) -> {
	    //Config conf = req.require(Config.class);
		
	    Config conf = ConfigFactory.load("application");
	    String myprop = conf.getString("port");
	    System.out.println(myprop);
	    resp.send("porta capturada");
	  });
	
	get("/downloadRoteiro",(req,resp) -> {
		String roteiro = req.param("roteiro").value();
		//System.out.println("Id do roteiro: " + rId);
		//pega os roteiros de um mapeamento e devolve o arquivo environment para os alunos
		File fileToSend = null;
		try {
			fileToSend = FileUtilities.getEnvironment(roteiro);
			resp.type(MediaType.octetstream);
		    resp.download(fileToSend);
		} catch (ConfigurationException | IOException | RoteiroException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			resp.send(e.getMessage());
		}
	});
	
  }

  {
	post("/uploadRoteiro", (req,resp) -> {
		//toda a logica para receber um roteiro e guarda-lo por completo e mante-lo no mapeamento
		System.out.println("pedido de upload de roteiro recebido");
		String roteiro = req.param("roteiro").value();
	    String semestre = req.param("semestre").value();
	    String turma = req.param("turma").value();
	    Upload uploadAmbiente = req.param("arquivoAmbiente").toUpload();
	    Upload uploadCorrecao = req.param("arquivoCorrecao").toUpload();
		  //System.out.println("upload " + upload);
		ProfessorUploadConfiguration config = new ProfessorUploadConfiguration(semestre,turma,roteiro);
		File uploadedAmbiente = uploadAmbiente.file();
		File uploadedCorrecao = uploadCorrecao.file();
		String result = "default response";
		try {
			result = FileUtilities.saveProfessorSubmission(uploadedAmbiente, uploadedCorrecao, config);
			
		} catch (ConfigurationException e) {
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
	
	
	post("/submitRoteiro",(req,resp) -> {
	      String matricula = req.param("matricula").value();
	      String semestre = req.param("semestre").value();
	      String turma = req.param("turma").value();
	      String roteiro = req.param("roteiro").value();
	      Upload upload = req.param("arquivo").toUpload();
		  //System.out.println("upload " + upload);
		  StudentUploadConfiguration config = new StudentUploadConfiguration(matricula, semestre, turma, roteiro);
		  File uploaded = upload.file();
		  String result = "default response";
		  try {
			result = FileUtilities.saveStudentSubmission(uploaded, config);
			
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
