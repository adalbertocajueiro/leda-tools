package br.edu.ufcg.ccc.leda.submission.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooby.Jooby;
import org.jooby.MediaType;
import org.jooby.Results;
import org.jooby.Session;
import org.jooby.Upload;
import org.jooby.View;
import org.jooby.ftl.Ftl;

import com.google.gdata.util.ServiceException;
import com.google.gson.Gson;

import br.edu.ufcg.ccc.leda.submission.util.Submission;
import br.edu.ufcg.ccc.leda.submission.util.AutomaticCorrector;
import br.edu.ufcg.ccc.leda.submission.util.Configuration;
import br.edu.ufcg.ccc.leda.submission.util.ConfigurationException;
import br.edu.ufcg.ccc.leda.submission.util.CorrectionManager;
import br.edu.ufcg.ccc.leda.submission.util.Corretor;
import br.edu.ufcg.ccc.leda.submission.util.FileUtilities;
import br.edu.ufcg.ccc.leda.submission.util.ProfessorUploadConfiguration;
import br.edu.ufcg.ccc.leda.submission.util.Roteiro;
import br.edu.ufcg.ccc.leda.submission.util.AtividadeException;
import br.edu.ufcg.ccc.leda.submission.util.Student;
import br.edu.ufcg.ccc.leda.submission.util.StudentException;
import br.edu.ufcg.ccc.leda.submission.util.StudentUploadConfiguration;
import br.edu.ufcg.ccc.leda.submission.util.Util;
import br.edu.ufcg.ccc.leda.util.CorrectionReport;
import br.edu.ufcg.ccc.leda.util.TestReport;
import br.edu.ufcg.ccc.leda.util.TestReportItem;
import br.edu.ufcg.ccc.leda.util.Utilities;
import br.edu.ufcg.ccc.leda.submission.util.FileCopy;
import br.edu.ufcg.ccc.leda.submission.util.Atividade;
import br.edu.ufcg.ccc.leda.submission.util.Constants;

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
		assets("css/**");
		assets("/*.ico");
		assets("/*.svg");
		assets("jquery/**");
		assets("bootstrap/**");
		assets("tether/**");
		
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
	
	get("/addIp", (req,resp) -> {
		String novoIp = req.param("ip").value();
		Configuration.getInstance().getIpsAutorizados().add(novoIp);
		resp.send("IP: " + novoIp + " adicionado. Verifique as configurações");
	});
	
	get("/alunosJson", req -> {
		Gson gson = new Gson();
		
	    return gson.toJson(Configuration.getInstance().getStudents());
	}).produces("json");
	
	get("/alunos", (req) -> {
        //List<String> alunos = new ArrayList<String>();
        Map<String,Student> alunos = Configuration.getInstance().getStudents();
        View html = Results.html("alunos");
        html.put("semestre",Constants.CURRENT_SEMESTER);
        //html.put("alunos",alunos.values().stream().sorted((a1,a2) -> a1.getTurma().compareTo(a2.getTurma()) == 0 ? a1.getNome().compareTo(a2.getNome()) : a1.getTurma().compareTo(a2.getTurma())).collect(Collectors.toList()));
        html.put("alunos",alunos.values().stream().sorted((a1,a2) -> a1.getNome().compareTo(a2.getNome())).collect(Collectors.toList()));
        Map<String,List<Atividade>> atividadesAgrupadas = Configuration.getInstance().getAtividades().values().stream().sorted( (a1,a2) -> a1.getDataHora().compareTo(a2.getDataHora())).collect(Collectors.groupingBy( Atividade::getTurma));
        html.put("turmas",atividadesAgrupadas.keySet());
        
        return html;
    });
	
	get("/cronograma", (req) -> {
        Map<String,Atividade> atividades = Configuration.getInstance().getAtividades();
        View html = Results.html("cronograma");
        //int turmas = atividades.values().stream().sorted( (a1,a2) -> a1.getDataHora().compareTo(a2.getDataHora())).collect(Collectors.groupingBy( Atividade::getTurma)).size();
        //System.out.println("Numero turmas: " + turmas);
        //List<String> turmas = new ArrayList<String>();
        //for (int i = 1; i <= Constants.TURMAS; i++) {
		//	turmas.add(String.valueOf(i));
		//}
        //html.put("turmas", turmas);
        //html.put("atividades", atividades.values().stream().sorted( (a1,a2) -> a1.getDataHora().compareTo(a2.getDataHora())).collect(Collectors.toList()));
        Map<String,List<Atividade>> atividadesAgrupadas = atividades.values().stream().sorted( (a1,a2) -> a1.getDataHora().compareTo(a2.getDataHora())).collect(Collectors.groupingBy( Atividade::getTurma));
        html.put("atividades", atividadesAgrupadas);
        
        return html;
    });
	
	get("/menu", (req) -> {
        View html = Results.html("menu-frames");
        return html;
    });
	
	get("/menuLeft", (req) -> {
        View html = Results.html("menu");
        return html;
    });
	
	get("/menuCorrecao", (req) -> {
		String id = req.param("id").value();	
		Atividade atividade = Configuration.getInstance().getAtividades().get(id);
		//System.out.println("%%%%ATIVIDADE " + atividade);
		View html = Results.html("menu-frames-correcao");
        html.put("id", id);
        html.put("sessionId", req.session().id());
        if(atividade instanceof Roteiro){
        	html.put("corretor",((Roteiro) atividade).getCorretor());
        	//System.out.println("%%%% CORRETOR: " + ((Roteiro) atividade).getCorretor());
        }
        return html;
    });
	
	get("/surefireReport", (req) -> {
		/*String id = req.param("id").value();
		String matricula = req.param("matricula").value();
		TestReport testReport = Util.loadTestReport(id);
		TestReportItem item = testReport.getReportItems().stream()
				.filter(tri -> tri.getMatricula().equals(matricula)).findFirst().orElse(null);
		//retorna o link para o relatorio em si se ele existe
		//os relatorios se encotnram em public/reports/ID/subs/MATR-NOME/target/site/project-reports.html
		//este é o campo completeReport do TestReportItem 
		if(item.getCompleteReport() != null){
			String renderer = item.getCompleteReport().getAbsolutePath();
			int indexOfId = renderer.indexOf(id);
			renderer = renderer.substring(indexOfId); //precisa tirar o .html do nome do arquivo
			int indexOfDot = 
		}*/
		//ou entao retorna uma tela com a mensagem de que o relatorio nao foi gerado.

		//resp.send("Relatorio surefire do aluno escolhido");
		View html = Results.html("surefire-report");
		html.put("sessionId", req.session().id());
		
		return html;
    });
	
	get("/commentPanel", (req) -> {
		String id = req.param("id").value("");
		String matriculaAluno = req.param("matricula").value("");
		String corretorPar = req.param("corretor").value("");
		Session session = req.session();
		
		View html = Results.html("comment-panel");
		html.put("id",id);
		html.put("matricula", matriculaAluno);
		html.put("corretorMat",corretorPar);
		html.put("sessionId", req.session().id());
		html.put("corretor", session.get("corretor"));
		
		CorrectionReport report = Util.loadCorrectionReport(id);
		if(report.getMatriculaCorretor().equals("")){
			report.setMatriculaCorretor(corretorPar);
			Util.writeCorrectionReport(report, id);
		}
		//System.out.println("MATRICULA " + matriculaAluno);
		if(!matriculaAluno.equals("")){
			html.put("comentario",report.getComentario(matriculaAluno).trim());
		}
		
		return html;
		//resp.send("Painel para comentario do codigo do aluno: " + matricula);
    });
	
	get("/menuLeftCorrecao", (req) -> {
		String id = req.param("id").value();
		String turma = id.substring(4).trim();
		Session session = req.session();
		String corretor = session.get("corretor").toOptional().orElse(null);
		//System.out.println("CORETOR DA SESSAO: " + corretor);
		List<Student> alunos = Configuration.getInstance().getStudents().values()
				.stream().filter(a -> a.getTurma().equals(turma))
				.sorted((a1,a2) -> a1.getNome().compareTo(a2.getNome()))
				.collect(Collectors.toList());
		TestReport report = Util.loadTestReport(id);
		HashMap<String,TestReportItem> items = new HashMap<String,TestReportItem>();
		report.getReportItems().forEach(item -> items.put(item.getMatricula(), item ));
		
		Atividade atividade = Configuration.getInstance().getAtividades().get(id);
		
		View html = Results.html("menuLeftCorrecao");
        html.put("id",id);
        html.put("alunos", alunos);
        html.put("reportItems", items);
        html.put("sessionId", req.session().id());
        if(atividade instanceof Roteiro){
        	html.put("corretor",((Roteiro) atividade).getCorretor());
        	//System.out.println("%%%% CORRETOR: " + ((Roteiro) atividade).getCorretor());
        }
        if(corretor != null && !corretor.equals("")){
        	html.put("corretorSessao",corretor);
        }
		return html;
    });
	

	/*get("/loginCorretor", (req) -> {
		String id = req.param("id").value();
		String turma = id.substring(4).trim();
		//validar senha e colocar corretor na sessao
		
		List<Student> alunos = Configuration.getInstance().getStudents().values()
				.stream().filter(a -> a.getTurma().equals(turma))
				.sorted((a1,a2) -> a1.getNome().compareTo(a2.getNome()))
				.collect(Collectors.toList());
		TestReport report = Util.loadTestReport(id);
		HashMap<String,TestReportItem> items = new HashMap<String,TestReportItem>();
		report.getReportItems().forEach(item -> items.put(item.getMatricula(), item ));
		
		Atividade atividade = Configuration.getInstance().getAtividades().get(id);
		
		View html = Results.html("menuLeftCorrecao");
        html.put("id",id);
        html.put("alunos", alunos);
        html.put("reportItems", items);
        html.put("sessionId", req.session().id());
        if(atividade instanceof Roteiro){
        	html.put("corretor",((Roteiro) atividade).getCorretor());
        	//System.out.println("%%%% CORRETOR: " + ((Roteiro) atividade).getCorretor());
        }
		return html;
    });*/
	
	get("/listCopies", (req) -> {
		String id = req.param("id").value();
		List<List<FileCopy>> copias = Util.listAllCopies(id);
        View html = Results.html("copias");
        html.put("copias",copias);
        html.put("id",id);
        
        return html;
		
        
		//resp.send(copies.toString());
	  });
	
	get("/submissoes", (req) -> {
		boolean showAll = Boolean.valueOf(req.param("showAll").value());
		Map<String,List<Submission>> submissoes = Util.allSubmissions(showAll);
		
		Collection<String> orderedKeys = submissoes.keySet().stream().sorted(Util.comparatorProvas()).collect(Collectors.toList());
		View html = Results.html("submissoes");
		html.put("submissoes",submissoes);
		html.put("chavesOrdenadas",orderedKeys);
	
		return html;
		/*String id = req.param("id").value();
		StringBuffer submissions = FileUtilities.listSubmissions(id);
		resp.send("Submissoes: <br>\n" + submissions.toString());*/
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
	
	get("/reload", (req) -> {
		Configuration.getInstance().reload();
		//chain.next("config",req,resp);
		
		//StringBuilder result = new StringBuilder();
		//result.append("Configuration instance reloaded! New values bellow...<br>");
		//result.append(Configuration.getInstance().toString());
		
		//resp.send(result.toString());
		View html = Results.html("configuration");
        html.put("config",Configuration.getInstance());
        
        return html;
		
	});
	//get("/report/", req -> Results.html("report/generated-report"));
	
	get("/config", (req) -> {

		//StringBuilder result = new StringBuilder();
		//result.append("Configuration information! <br>");
		//result.append(Configuration.getInstance().toString());

        View html = Results.html("configuration");
        Configuration config = Configuration.getInstance();
        //System.out.println(config.getIpsAutorizados());
        html.put("config",config);
        //System.out.println(config.toString());
        return html;
        
		//resp.send(result.toString());
	  }).name("config");
	
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
	
	
	
	get("/requestDownload",(req) -> {
		String id = req.param("id").value();

		View html = Results.html("modal-download");
	    html.put("id",id);
	    
	    return html;

	});
	
	
	
	get("/correct", (req,resp) -> {
		String id = req.param("id").value();
		AutomaticCorrector corr = new AutomaticCorrector();
		corr.corrigirAtividade(id);
		resp.send("Correction started");
	});
	
	get("/report", (req) -> {

		//StringBuilder result = new StringBuilder();
		//result.append("Configuration information! <br>");
		//result.append(Configuration.getInstance().toString());
		String id = req.param("id").value();
        View html = Results.html("report");
        //Configuration config = Configuration.getInstance();
        //System.out.println(config.getIpsAutorizados());
        html.put("id",id);
        html.put("report",Util.loadTestReport(id));
        //System.out.println(config.toString());
        return html;
        
		//resp.send(result.toString());
	  });
	
	get("/logoutCorretor", (req,resp) -> {
		String id = req.param("id").value();
		View html = Results.html("historyBack");
		Session session = req.session();
		session.set("corretor", "");
		resp.redirect("menuLeftCorrecao?id=" + id);
		//return html;
	});
  }

  {
		/*post("/downloadProva",(req,resp) -> {
			String prova = req.param("prova").value();
			String matricula = req.param("matricula").value();

			//verifica se a amtricula é de aluno cadastrado e a prova que pede eh da turma 
			//do aluno
			File fileToSend = null;
			try {
				fileToSend = FileUtilities.getEnvironmentProva(prova, matricula);
				resp.type(MediaType.octetstream);
			    resp.download(fileToSend);
			} catch (ConfigurationException | IOException | AtividadeException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				resp.send(e.getMessage());
			}
		});*/

		post("/download",(req,resp) -> {
			String id = req.param("id").value();
			String matricula = req.param("matricula").value();

			//verifica se a amtricula é de aluno cadastrado e a prova que pede eh da turma 
			//do aluno
			File fileToSend = null;
			try {
				fileToSend = FileUtilities.getEnvironmentAtividade(id, matricula);
				resp.type(MediaType.octetstream);
			    resp.download(fileToSend);
			} catch (ConfigurationException | IOException | AtividadeException e) {
				resp.send(e.getMessage());
			}
		});

		 post("/uploadAtividade", (req,resp) -> {
				//toda a logica para receber um roteiro e guarda-lo por completo e mante-lo no mapeamento
				//System.out.println("pedido de upload de roteiro recebido");
				String id = req.param("roteiro").value();
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
				ProfessorUploadConfiguration config = new ProfessorUploadConfiguration(id,semestre,turma,numeroTurmas);
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

	
		 		
		post("/submitAtividade",(req,resp) -> {
			      String matricula = req.param("matricula").value();
			      String semestre = req.param("semestre").value();
			      String id = req.param("roteiro").value();
			      String ip = req.param("ip").value();
			      String files = req.param("filesOwners").value();
			      Upload upload = req.param("arquivo").toUpload();
			      
			      
			      //R0X-0X
			      String turma = id.substring(4);
				  
			      //se o id do roteiro for PPX, entao é submissao de prova e deve validar o ip e salvar
			      //em outra pasta. Isso vai ser feito pelo FileUtilities.saveStudentSubmission
			      
			      //System.out.println("Request received from " + ip);
				  
			      StudentUploadConfiguration config = new StudentUploadConfiguration(id,semestre, turma, matricula,ip,files);
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
					
				} catch (AtividadeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = e.getMessage();
				}
				resp.send(result);  
			    });

	post("/loginCorretor",(req) -> {
		String id = req.param("id").value();
		String matricula = req.param("matricula").value();
		String turma = id.substring(4).trim();
		String senha = req.param("senha").value();
		
		View html = Results.html("menuLeftCorrecao");
		
		Atividade atividade = Configuration.getInstance().getAtividades().get(id);
		String matriculaCorretor = ((Roteiro) atividade).getCorretor().getMatricula();
		//System.out.println("MATRICULA CORRETOR: " + matriculaCorretor);
		//System.out.println("SENHA CORRETOR: " + senha);
		if(matriculaCorretor.equals(senha)){
			Session session = req.session();
			session.set("corretor", ((Roteiro) atividade).getCorretor().getMatricula());
			
			List<Student> alunos = Configuration.getInstance().getStudents().values()
					.stream().filter(a -> a.getTurma().equals(turma))
					.sorted((a1,a2) -> a1.getNome().compareTo(a2.getNome()))
					.collect(Collectors.toList());
			TestReport report = Util.loadTestReport(id);
			HashMap<String,TestReportItem> items = new HashMap<String,TestReportItem>();
			report.getReportItems().forEach(item -> items.put(item.getMatricula(), item ));
			
	        html.put("id",id);
	        html.put("alunos", alunos);
	        html.put("reportItems", items);
	        html.put("sessionId", req.session().id());
	        if(atividade instanceof Roteiro){
	        	html.put("corretor",((Roteiro) atividade).getCorretor());
	        	//System.out.println("%%%% CORRETOR: " + ((Roteiro) atividade).getCorretor());
	        }
	        html.put("corretorSessao",((Roteiro) atividade).getCorretor().getMatricula());
			html.put("id",id);
		}else{
			html = Results.html("erro-senha");
			html.put("matricula",matricula);
		}
		
	    
	    return html;

	});
	
	post("/addComment",(req,resp) -> {
		String id = req.param("id").value();
		String matriculaAluno = req.param("matricula").value();
		String matriculaCorretorPar = req.param("corretor").value();
		String comentario = req.param("comentario").value();

		Session session = req.session();
		String matriculaCorretor = session.get("corretor").value("");
		if(matriculaCorretor == null || matriculaCorretor.equals("")){
			//throw new RuntimeException("Corretor nao logado!");
			resp.redirect("commentPanel?id=" + id + "?matricula=''&corretor="+matriculaCorretorPar);
		}
		
		Util.writeCorrectionComment(id, matriculaAluno, comentario);
		resp.redirect("commentPanel?id=" + id + "?matricula=" + matriculaAluno + "&corretor=" + matriculaCorretorPar); 
		//Gson gson = new Gson();	
	    //return gson.toJson("Comentario salvo");
	});
	//use(new Auth().basic("*", MyUserClientLoginValidator.class));
	//new SimpleTestTokenAuthenticator(){
	//	@override
	//};
	//use(new Auth().basic());
	//use(new Auth().basic("/config2/**",MyUserClientLoginValidator.class));
	
	
	
  }
  
  /*
  static class MyUserClientLoginValidator implements UsernamePasswordAuthenticator{

	@Override
	public void validate(UsernamePasswordCredentials arg0) {
		if(!arg0.getUsername().equals("leda") 
				|| !arg0.getPassword().equals("ledaserver")){
			throw new CredentialsException("Login ou senha incoretos");
		}
	}
  }
  
  
  public static void main(final String[] args) throws Throwable {
    run(SubmissionServer::new, args);
  }
	*/
}
