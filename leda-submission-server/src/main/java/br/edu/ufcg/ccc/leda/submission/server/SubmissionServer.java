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
import java.util.function.Predicate;
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
import br.edu.ufcg.ccc.leda.submission.util.Professor;
import br.edu.ufcg.ccc.leda.submission.util.ProfessorUploadConfiguration;
import br.edu.ufcg.ccc.leda.submission.util.Roteiro;
import br.edu.ufcg.ccc.leda.submission.util.SimilarityMatrix;
import br.edu.ufcg.ccc.leda.submission.util.AtividadeException;
import br.edu.ufcg.ccc.leda.submission.util.Student;
import br.edu.ufcg.ccc.leda.submission.util.StudentException;
import br.edu.ufcg.ccc.leda.submission.util.StudentUploadConfiguration;
import br.edu.ufcg.ccc.leda.submission.util.Util;
import br.edu.ufcg.ccc.leda.util.CorrectionClassification;
import br.edu.ufcg.ccc.leda.util.CorrectionReport;
import br.edu.ufcg.ccc.leda.util.CorrectionReportItem;
import br.edu.ufcg.ccc.leda.util.TestReport;
import br.edu.ufcg.ccc.leda.util.TestReportItem;
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
		assets("/*.gif");
		assets("/*.png");
		assets("jquery/**");
		assets("plotly/**");
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
        Map<String,Student> alunos = Configuration.getInstance().getStudents();
        View html = Results.html("alunos");
        html.put("semestre",Constants.CURRENT_SEMESTER);
        html.put("alunos",alunos.values().stream()
        		.sorted((a1,a2) -> a1.getNome().compareTo(a2.getNome()))
        		.collect(Collectors.toList()));
        Map<String,List<Atividade>> atividadesAgrupadas = 
        		Configuration.getInstance().getAtividades().values().stream()
        		.sorted( (a1,a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
        		.collect(Collectors.groupingBy( Atividade::getTurma));
        html.put("turmas",atividadesAgrupadas.keySet());
        
        return html;
    });
	
	get("/cronograma", (req) -> {
        Map<String,Atividade> atividades = Configuration.getInstance().getAtividades();
        View html = Results.html("cronograma");
        Map<String,List<Atividade>> atividadesAgrupadas = atividades.values()
        		.stream()
        		.sorted( (a1,a2) -> {
        			if(a1.getDataHora().compareTo(a2.getDataHora()) == 0){
						return a1.getNome().compareTo(a2.getNome());
					}else{
						return a1.getDataHora().compareTo(a2.getDataHora());
					}
        			//a1.getDataHora().compareTo(a2.getDataHora())        		
        		}).collect(Collectors.groupingBy( Atividade::getTurma));
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
        if(atividade instanceof Roteiro){
       		html.put("corretor",((Roteiro) atividade).getCorretor());
        }
        return html;
    });
	
	get("/surefireReport", (req) -> {
		String matricula = req.param("matricula").value("");
		String id = req.param("id").value("");
		View html = Results.html("surefire-report");
		if(!matricula.equals("")){
			html.put("submission",Util.getSubmissionForStudent(id,matricula));
			html.put("id",id);
		}
		return html;
    });
	
	get("/commentPanel", (req) -> {
		String id = req.param("id").value("");
		String matriculaAluno = req.param("matricula").value("");
		String corretorPar = req.param("corretor").value("");
		Session session = req.session();
		
		Student aluno = Configuration.getInstance().getStudents().get(matriculaAluno);
		

		View html = Results.html("comment-panel");
		html.put("id",id);
		html.put("matricula", matriculaAluno);
		html.put("aluno",aluno);
		html.put("corretorMat",corretorPar);
		html.put("corretor", session.get("corretor"));
		html.put("classificacao",CorrectionClassification.values());
		
		CorrectionReport report = Util.loadCorrectionReport(id);
		
		if(report != null){
			//if(report.getMatriculaCorretor().equals("")){
			report.setMatriculaCorretor(corretorPar);
			Util.writeCorrectionReport(report, id);
			//}
			CorrectionReportItem correctionItem = report.getCorrectionReportItemforStudent(matriculaAluno);
			if(correctionItem != null){
				html.put("correctionReportItem", correctionItem);
			}
		}

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
		List<Student> alunos = Configuration.getInstance().getStudents().values()
				.stream().filter(a -> a.getTurma().equals(turma))
				.sorted((a1,a2) -> a1.getNome().compareTo(a2.getNome()))
				.collect(Collectors.toList());
		TestReport report = Util.loadTestReport(id);
		HashMap<String,TestReportItem> items = new HashMap<String,TestReportItem>();
		if(report != null){
			report.getReportItems().forEach(item -> items.put(item.getMatricula(), item ));
		}
		Atividade atividade = Configuration.getInstance().getAtividades().get(id);
		
		View html = Results.html("menuLeftCorrecao");
        html.put("id",id);
        html.put("alunos", alunos);
        html.put("reportItems", items);
        
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
	get("/compactAll", (req,resp) -> {
		Util.compactAllData();
        
		resp.send("Compactacao dos dados iniciada...");
	  });
	
	get("/compact", (req,resp) -> {
		String id = req.param("id").value();
		
		File fileToSend = Util.compact(id);
        
		if(fileToSend != null){
			resp.type(MediaType.octetstream);
	    	resp.download(fileToSend);
		}else{
			resp.send("Arquivo compactado nao gerado para atividade " + id);
		}
		
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
	
	get("/submissao", (req,resp) -> {
		String id = req.param("id").value();

		File folder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		List<Submission> submissoes = Util.submissions(folder);

		StringBuilder sb  = new StringBuilder();
		sb.append("Submissoes: " + submissoes.size() + "\n<br>\n");
		submissoes.forEach(s -> {
			sb.append(s.getAluno().getNome() + "\n<br>");
		});
		
		resp.send(sb.toString());
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
	
	get("/requestReload", (req) -> {
		View html = Results.html("modal-reload");
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
	  });
	
	get("/faltas", (req) -> {

		Map<String,List<Submission>> submissoes = Util.allSubmissions(true);
		Map<String,Map<String,Boolean>> totalFaltas = 
				Util.totalizacaoFaltas();

        Map<String,List<Atividade>> atividadesAgrupadas = 
        		Configuration.getInstance().getAtividades().values().stream()
        		.sorted( (a1,a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
        		.collect(Collectors.groupingBy( Atividade::getTurma));

        View html = Results.html("faltas");
		html.put("submissoes",submissoes);
		html.put("totalFaltas",totalFaltas);
        html.put("turmas",atividadesAgrupadas.keySet());
        html.put("atividades", Configuration.getInstance().getAtividades());
        html.put("semestre",Constants.CURRENT_SEMESTER);
        html.put("alunos",Configuration.getInstance().getStudents().values().stream()
        		.sorted( (a1,a2) -> a1.getNome().compareTo(a2.getNome()))
        		.collect(Collectors.toList()));

        return html;
	  });
	
	get("/mediaProvasPraticas", (req) -> {
		Gson gson = new Gson();
		return gson.toJson(Util.buildMediasProvasPraticas());
	  }).produces("json");
	
	get("/notas", (req) -> {

		Map<String,List<Atividade>> atividadesAgrupadas = 
				Configuration.getInstance().getAtividades().values()
        		.stream()
        		.sorted( (a1,a2) -> {
        			if(a1.getDataHora().compareTo(a2.getDataHora()) == 0){
						return a1.getNome().compareTo(a2.getNome());
					}else{
						return a1.getDataHora().compareTo(a2.getDataHora());
					}
        			//a1.getDataHora().compareTo(a2.getDataHora())        		
        		}).collect(Collectors.groupingBy( Atividade::getTurma));
		
       

        View html = Results.html("notas");
        Map<String,CorrectionReport> correctionReports = Util.loadCorrectionReports(new Predicate<String>() {
			//predicado para filtrar o que mostrar nas notas (provas e roteiros
			@Override
			public boolean test(String t) {
				return Constants.PATTERN_PROVA.matcher(t).matches()
						|| Constants.PATTERN_ROTEIRO.matcher(t).matches();
			}
		});
                
		html.put("correctionReports",correctionReports); 
        html.put("atividadesAgrupadas",atividadesAgrupadas);
        html.put("atividades", Configuration.getInstance().getAtividades());
        html.put("semestre",Constants.CURRENT_SEMESTER);
        html.put("alunos",Configuration.getInstance().getStudents().values().stream()
        		.sorted( (a1,a2) -> a1.getNome().compareTo(a2.getNome()))
        		.collect(Collectors.toList()));
        html.put("mediasProvasPraticas", Util.buildMediasProvasPraticas());
        html.put("notasDaFinal", Util.getNotasDaFinal());
        html.put("mediasComFinal", Util.buildMediasLEDAComFinal());
        html.put("mediasProvasTeoricas", Util.loadSpreadsheetsMediasEDA());
        html.put("mediasRoteiros", Util.buildMediasRoteiros());
        return html;
	  });
	
	get("/requestDownload",(req) -> {
		String id = req.param("id").value();

		View html = Results.html("modal-download");
	    html.put("id",id);
	    
	    return html;

	});
	
	get("/exportToExcel",(req,resp) -> {
		String id = req.param("id").value();

		File fileToSend = null;
		try {
			fileToSend = Util.exportRoteiroToExcel(id);
			if(fileToSend != null){
				resp.type(MediaType.octetstream);
		    	resp.download(fileToSend);
			}else{
				resp.send("Arquivo excel nao gerado para atividade " + id);
			}
		} catch (ConfigurationException | IOException | AtividadeException e) {
			resp.send(e.getMessage());
		}

	});
	
	get("/exportToExcelNotasSemestre",(req,resp) -> {
		String turma = req.param("turma").value();

		File fileToSend = null;
		try {
			fileToSend = Util.exportPlaninhaGeralToExcel(turma);
			if(fileToSend != null){
				resp.type(MediaType.octetstream);
		    	resp.download(fileToSend);
			}else{
				resp.send("Arquivo excel nao gerado para a turma " + turma);
			}
		} catch (ConfigurationException | IOException | AtividadeException e) {
			resp.send(e.getMessage());
		}

	});
	
	get("/exportToExcelFaltasSemestre",(req,resp) -> {
		String turma = req.param("turma").value();

		File fileToSend = null;
		try {
			fileToSend = Util.exportPlaninhaGeralFaltasToExcel(turma);
			if(fileToSend != null){
				resp.type(MediaType.octetstream);
		    	resp.download(fileToSend);
			}else{
				resp.send("Arquivo excel nao gerado para a turma " + turma);
			}
		} catch (ConfigurationException | IOException | AtividadeException e) {
			resp.send(e.getMessage());
		}

	});
	
	get("/correct", (req,resp) -> {
		String id = req.param("id").value();
		AutomaticCorrector corr = new AutomaticCorrector();
		corr.corrigirAtividade(id);
		resp.send("Correction started");
	});
	
	get("/report", (req) -> {

		String id = req.param("id").value();
        View html = Results.html("report");
        html.put("id",id);
        html.put("atividade", Configuration.getInstance().getAtividades().get(id));
        TestReport testReport = Util.loadTestReport(id);
        if(testReport != null){
        	html.put("report",testReport);
        }
        CorrectionReport corrReport = Util.loadCorrectionReport(id);
        html.put("correctionReport",corrReport);
        if(corrReport != null){
        	SimilarityMatrix similarityMatrix = Util.buildSimilarityMatrix(id);
        	html.put("sMatrix", similarityMatrix);
        //html.put("studentsTestList", similarityMatrix.getStudentsTestList());
        //html.put("matrix",similarityMatrix.getSimilarities());
        }
        //html.put(values);

        return html;
        
		//resp.send(result.toString());
	  });
	
	get("/logoutCorretor", (req,resp) -> {
		String id = req.param("id").value();
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

	  post("/reload", (req,resp) -> {
			boolean reloadIPs = Boolean.valueOf(req.param("reloadIPs").value());
			//System.out.println("RELOAD IPS: " + reloadIPs);
			Configuration.getInstance().reload(reloadIPs);
			//StringBuilder result = new StringBuilder();
			//result.append("Configuration instance reloaded! New values bellow...<br>");
			//result.append(reloadIPs);
			
			//resp.send(result.toString());
			//View html = Results.html("configuration");
	        //html.put("config",Configuration.getInstance());
	        
			resp.redirect("menu");
	        //return html;
			
		});
	  
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
		Corretor corretor = ((Roteiro) atividade).getCorretor();
		//pegar tambem os professores para que eles tenham acesso direto.
		
		//String matriculaCorretor = corretor.getMatricula();
		//System.out.println("MATRICULA CORRETOR: " + matriculaCorretor);
		//System.out.println("SENHA CORRETOR: " + senha);
		if(corretor.getSenha().equals(senha) || 
				Configuration.getInstance().getMonitores().stream()
				.filter(c -> (c instanceof Professor) && c.getSenha().equals(senha))
				.findFirst().isPresent()){
			
			Session session = req.session();
			session.set("corretor", ((Roteiro) atividade).getCorretor().getMatricula());
			
			List<Student> alunos = Configuration.getInstance().getStudents().values()
					.stream().filter(a -> a.getTurma().equals(turma))
					.sorted((a1,a2) -> a1.getNome().compareTo(a2.getNome()))
					.collect(Collectors.toList());
			TestReport report = Util.loadTestReport(id);
			HashMap<String,TestReportItem> items = new HashMap<String,TestReportItem>();
			if(report != null){
				report.getReportItems().forEach(item -> items.put(item.getMatricula(), item ));
			}
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
		String classificacaoStr = req.param("classificacaoAluno").value();
		String notaDesignStr = req.param("notaDesign").value();
		String comentario = req.param("comentario").value();

		Session session = req.session();
		String matriculaCorretor = session.get("corretor").value("");
		if(matriculaCorretor == null || matriculaCorretor.equals("")){
			//throw new RuntimeException("Corretor nao logado!");
			resp.redirect("commentPanel?id=" + id + "&matricula=&corretor="+matriculaCorretorPar);
		}else{
			Util.writeCorrectionComment(id, matriculaAluno, notaDesignStr, classificacaoStr, comentario);
			resp.redirect("commentPanel?id=" + id + "&matricula=" + matriculaAluno + "&corretor=" + matriculaCorretorPar); 
		}
		
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
