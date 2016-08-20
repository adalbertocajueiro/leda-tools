package br.edu.ufcg.ccc.leda.submission.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Results;
import org.jooby.Upload;
import org.jooby.View;
import org.jooby.ftl.Ftl;
import org.jooby.jade.Jade;

import br.edu.ufcg.ccc.leda.submission.util.AutomaticCorrector;
import br.edu.ufcg.ccc.leda.submission.util.Configuration;
import br.edu.ufcg.ccc.leda.submission.util.ConfigurationException;
import br.edu.ufcg.ccc.leda.submission.util.CorrectionManager;
import br.edu.ufcg.ccc.leda.submission.util.FileUtilities;
import br.edu.ufcg.ccc.leda.submission.util.ProfessorUploadConfiguration;
import br.edu.ufcg.ccc.leda.submission.util.RoteiroException;
import br.edu.ufcg.ccc.leda.submission.util.StudentException;
import br.edu.ufcg.ccc.leda.submission.util.Student;
import br.edu.ufcg.ccc.leda.submission.util.StudentUploadConfiguration;
import br.edu.ufcg.ccc.leda.submission.util.Util;

import com.google.gdata.util.ServiceException;
import com.google.gson.Gson;

/**
 * @author jooby generator
 */
public class SubmissionServer extends Jooby {
	
	//private static CorrectionManager correctionManager;
	//private static Configuration configuration;
	
	static{
		try {
			Configuration.getInstance();
			//correctionManager = CorrectionManager.getInstance();
			Thread.sleep(2000);
			CorrectionManager.getInstance();
		} catch (ConfigurationException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	
	public Path  saveUpload(File f, String folder) throws IOException{
		File fout = new File(folder + File.separator + f.getName().substring(f.getName().indexOf(".") + 1));
		return Files.move(f.toPath(), fout.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	{
		assets("/reports/**");
		assets("/site/**");
		assets("/*.ico");
		assets("bootstrap-3.3.7-dist/**");
		assets("bootstrap4/**");
		
	}
  {
	use(new Ftl());
	//use(new Jade());
	
	get("/", (req,resp) -> {
		resp.send("Hello World!");
	});
	
	get("/horaAtual", (req,resp) -> {
		resp.send("Data e hora atual do servidor: " + Util.formatDate(new GregorianCalendar()));
	});
	
	get("/alunos", (req) -> {
        //List<String> alunos = new ArrayList<String>();
        Map<String,Student> alunos = Configuration.getInstance().getStudents();
        View html = Results.html("alunos");
        html.put("pageName", "Alunos cadastrados em LEDA");
        
        html.put("alunos",alunos.values().stream().sorted((a1,a2) -> a1.getTurma().compareTo(a2.getTurma()) == 0 ? a1.getNome().compareTo(a2.getNome()) : a1.getTurma().compareTo(a2.getTurma())).collect(Collectors.toList()));
        
        return html;
    });
	
	get("/alunosOrdenados", (req) -> {
        //List<String> alunos = new ArrayList<String>();
        Map<String,Student> alunos = Configuration.getInstance().getStudents();
        View html = Results.html("alunosOrdenados");
        html.put("pageName", "Alunos cadastrados em LEDA");
        
        //html.put("listaEstudantes",jsonContent);
        html.put("alunos",alunos.values().stream().sorted((a1,a2) -> a1.getTurma().compareTo(a2.getTurma()) == 0 ? a1.getNome().compareTo(a2.getNome()) : a1.getTurma().compareTo(a2.getTurma())).collect(Collectors.toList()));
        
        return html;
    });

	get("/listSubmissions", (req,resp) -> {
		String id = req.param("id").value();
		StringBuffer submissions = FileUtilities.listSubmissions(id);
		resp.send("Submissoes: <br>\n" + submissions.toString());
	});
	
	get("/correctionThreads", (req,resp) -> {
		ArrayList<Thread> threads = CorrectionManager.getInstance().getExecuting();
		StringBuilder result = new StringBuilder();
		if(threads.size() == 0){
			result.append("Sem threads ativos de correcao");
		}else{
			threads.forEach(t -> result.append(t.getName() + " - STATE: " + t.getState().name() + "<br>\n"));
		}
		//result.append("Configuration instance reloaded! New values bellow...<br>");
		//result.append(Configuration.getInstance().toString());
		
		resp.send(result.toString());
	});
	
	get("/reload", (req,resp) -> {
		Configuration.getInstance().reload();
		StringBuilder result = new StringBuilder();
		result.append("Configuration instance reloaded! New values bellow...<br>");
		result.append(Configuration.getInstance().toString());
		
		resp.send(result.toString());
	});
	//get("/report/", req -> Results.html("report/generated-report"));
	
	get("/config", (req,resp) -> {
	    //Config conf = req.require(Config.class);
		//StringBuilder sb = new StringBuilder();
	    //ConfigFactory.load("application.conf").entrySet().stream().forEach(v -> sb.append(v.toString()));
	    //String myprop = conf.getString("port");
	    //System.out.println(myprop);
	    //resp.send(sb.toString());
		StringBuilder result = new StringBuilder();
		result.append("Configuration information! <br>");
		result.append(Configuration.getInstance().toString());
		
		resp.send(result.toString());
	  });
	
	/*get("/redirect", (req,resp) -> {
	    //req.flash("success", "The item has been created");
	    //return Results.redirect("http://www.google.com");
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet("http://www.ufcg.edu.br");
		CloseableHttpResponse response = httpclient.execute(httpget);
		HttpEntity resEntity = response.getEntity();
		String content = "";
		System.out.println("Entity: " + resEntity);
		if (resEntity != null) {
            //System.out.println("Response content length: " + resEntity.getContentLength());
            InputStreamReader isr = new InputStreamReader(resEntity.getContent());
            BufferedReader br =  new BufferedReader(isr);
            
            while( (content = br.readLine()) != null){

            }
        }
        EntityUtils.consume(resEntity);
        response.close();
        httpclient.close();
        resp.send(content);
	  });*/
	
	get("/correct", (req,resp) -> {
		String roteiro = req.param("roteiro").value();
		AutomaticCorrector corr = new AutomaticCorrector();
		corr.corrigirRoteiro(roteiro);
		resp.send("Correction started");
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
	
	get("/downloadProva",(req,resp) -> {
		String prova = req.param("prova").value();
		//System.out.println("Id do roteiro: " + rId);
		//pega os roteiros de um mapeamento e devolve o arquivo environment para os alunos
		File fileToSend = null;
		try {
			fileToSend = FileUtilities.getEnvironmentProva(prova);
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
		//System.out.println("pedido de upload de roteiro recebido");
		String roteiro = req.param("roteiro").value();
		//System.out.println(roteiro);
	    String semestre = req.param("semestre").value();
	    //System.out.println(semestre);
	    String turma = req.param("turma").value();
	    //System.out.println(turma);
	    int numeroTurmas = Integer.parseInt(req.param("numeroTurmas").value());
	    //System.out.println(numeroTurmas);
	    Upload uploadAmbiente = req.param("arquivoAmbiente").toUpload();
	    Upload uploadCorrecao = req.param("arquivoCorrecao").toUpload();
	    
		  //System.out.println("upload " + upload);
		ProfessorUploadConfiguration config = new ProfessorUploadConfiguration(semestre,turma,roteiro,numeroTurmas);
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
	
	post("/uploadProva", (req,resp) -> {
		//toda a logica para receber uma prova e guarda-lo por completo e mante-la no mapeamento
		//de provas onde se jabe a data de liberacao e a data maxima de envio
		String prova = req.param("roteiro").value(); //aqui sera o id da prova P0X
		//System.out.println(roteiro);
	    String semestre = req.param("semestre").value();
	    //System.out.println(semestre);
	    String turma = req.param("turma").value();
	    //System.out.println(turma);
	    int numeroTurmas = Integer.parseInt(req.param("numeroTurmas").value());
	    //System.out.println(numeroTurmas);
	    Upload uploadAmbiente = req.param("arquivoAmbiente").toUpload();
	    Upload uploadCorrecao = req.param("arquivoCorrecao").toUpload();
	    
		  //System.out.println("upload " + upload);
		ProfessorUploadConfiguration config = new ProfessorUploadConfiguration(semestre,turma,prova,numeroTurmas);
		File uploadedAmbiente = uploadAmbiente.file();
		File uploadedCorrecao = uploadCorrecao.file();
		String result = "default response";
		try {
			result = FileUtilities.saveProfessorTestSubmission(uploadedAmbiente, uploadedCorrecao, config);
			
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
	      String roteiro = req.param("roteiro").value();
	      String ip = req.param("ip").value();
	      Upload upload = req.param("arquivo").toUpload();
	      
	      //R0X-0X
	      String turma = roteiro.substring(4);
		  
	      //se o id do roteiro for PPX, entao é submissao de prova e deve validar o ip e salvar
	      //em outra pasta. Isso vai ser feito pelo FileUtilities.saveStudentSubmission
	      
	      //System.out.println("Request received from " + ip);
		  
	      StudentUploadConfiguration config = new StudentUploadConfiguration(semestre, turma, roteiro, matricula,ip);
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
			
		} catch (RoteiroException e) {
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
