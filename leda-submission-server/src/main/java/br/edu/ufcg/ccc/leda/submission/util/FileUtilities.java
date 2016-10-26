package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.read.biff.BiffException;

import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gdata.util.ServiceException;

public class FileUtilities {

	
	public static File getEnvironmentAmbiente(String id, String matricula) throws ConfigurationException, IOException, AtividadeException, ServiceException{
		File environment = null;
		
		//faz o registro do download feito pelo aluno ou mensagem de erro do acesso do download
		File provaUploadFolder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		DownloadProvaLogger logger = new DownloadProvaLogger(provaUploadFolder);
		String content = "VAZIO"; 
		//verifica se esta sendo requisitado dentro do prazo. faz om o validator

		try {
			Validator.validateDownloadAmbiente(id, matricula);
			//Validator.validateProvaDownload(id,matricula);
		} catch (AtividadeException e) {
			content = "[ERRO]:aluno " + matricula + " tentou fazer download da atividade (roteiro ou prova) " + id + " em " + Util.formatDate(new GregorianCalendar()) + ":" + e.getMessage();
			logger.log(content);
			throw e;
		}
		
		//pega o roteiro para obter o arquivo e mandar de volta
		Map<String,Atividade> atividades = Configuration.getInstance().getAtividades();
		Atividade atividade = atividades.get(id);
		if(atividade instanceof Roteiro){
			environment = ((Roteiro) atividade).getArquivoAmbiente();
	
			Map<String,Student> studentsMap = Configuration.getInstance().getStudents();
			Student requester = studentsMap.get(matricula);
			content = "[DOWNLOAD]:atividade (roteiro ou prova) " + id + " enviada para estudante " + matricula + "-" + requester.getNome() + " em " + Util.formatDate(new GregorianCalendar());
			logger.log(content);
		}
		return environment;
	}

	@Deprecated
	public static File getEnvironmentProva(String provaId, String matricula) throws ConfigurationException, IOException, AtividadeException, ServiceException{
		File environment = null;
		
		//faz o registro do download feito pelo aluno ou mensagem de erro do acesso do download
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		File provaUploadFolder = new File(currentSemester,provaId);

		DownloadProvaLogger logger = new DownloadProvaLogger(provaUploadFolder);
		String content = "VAZIO"; 
		//verifica se esta sendo requisitado dentro do prazo. faz om o validator

		try {
			Validator.validateProvaDownload(provaId,matricula);
		} catch (AtividadeException e) {
			content = "[ERRO]:aluno " + matricula + " tentou fazer download da prova " + provaId + " em " + Util.formatDate(new GregorianCalendar()) + ":" + e.getMessage();
			logger.log(content);
			throw e;
		}
		
		//pega o roteiro par aobter o arquivo e mandar de volta
		Map<String,Prova> provas = null; //Configuration.getInstance().getProvas();
		Prova prova = provas.get(provaId);
		environment = prova.getArquivoAmbiente();

		Map<String,Student> studentsMap = Configuration.getInstance().getStudents();
		Student requester = studentsMap.get(matricula);
		content = "[DOWNLOAD]:prova " + provaId + " enviada para estudante " + matricula + "-" + requester.getNome() + " em " + Util.formatDate(new GregorianCalendar());
		logger.log(content);
		
		return environment;
	}
	
	@Deprecated
	public static File getEnvironment(String roteiro, String matricula) throws ConfigurationException, IOException, AtividadeException, ServiceException{
		File environment = null;

		//faz o registro do download feito pelo aluno ou mensagem de erro do acesso do download
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		File provaUploadFolder = new File(currentSemester,roteiro);

		DownloadProvaLogger logger = new DownloadProvaLogger(provaUploadFolder);
		String content = "VAZIO"; 

		//verifica se esta sendo requisitado dentro do prazo. faz om o validator
		try {
			Validator.validateDownload(roteiro,matricula);
		} catch (AtividadeException e) {
			content = "[ERRO]:aluno " + matricula + " tentou fazer download do roteiro " + roteiro + " em " + Util.formatDate(new GregorianCalendar()) + ":" + e.getMessage();
			logger.log(content);
			throw e;
		}
		
		//pega o roteiro par aobter o arquivo e mandar de volta
		Map<String,Student> studentsMap = Configuration.getInstance().getStudents();
		Student requester = studentsMap.get(matricula);
		content = "[DOWNLOAD]:roteiro " + roteiro + " enviada para estudante " + matricula + "-" + requester.getNome() + " em " + Util.formatDate(new GregorianCalendar());
		logger.log(content);

		//pega o roteiro par aobter o arquivo e mandar de volta
		Map<String,Roteiro> roteiros = null; //Configuration.getInstance().getRoteiros();
		Roteiro rot = roteiros.get(roteiro);
		environment = rot.getArquivoAmbiente();
		
		return environment;
	}
	/**
	 * Salva um roteiro recebido de um upload de professor ou monitor autorizado numa pasta 
	 * especifica para geração de link de download de ambiente. O arquivo tambem salva num json
	 * o link mapeando o roteiro para os arquivos especificos dele (ambiente e correcao) e tambem
	 * faz o update disso no mapeamento dos roteiros em Configuration.
	 * 
	 * @return
	 * @throws StudentException 
	 * @throws ConfigurationException 
	 * @throws IOException 
	 * @throws AtividadeException 
	 * @throws ServiceException 
	 * @throws Exception 
	 */
	public static String saveProfessorSubmission(File ambiente, File projetoCorrecao, ProfessorUploadConfiguration config) throws StudentException, ConfigurationException, IOException, AtividadeException, ServiceException {
		String result = "";
		
		String id = config.getId();
		File folderAtividade = new File(Constants.PROVAS_FOLDER,id);
		if(Constants.PATTERN_ROTEIRO.matcher(id).matches() || 
				Constants.PATTERN_ROTEIRO_REVISAO.matcher(id).matches()){
			
			folderAtividade = new File(Constants.ROTEIROS_FOLDER,id);
		} 
		if (config.getId().contains("X")) {
			id = id.substring(0, id.indexOf("X"));
		}

			// remove os arquivos de prova antes cadastrados (baseado no id da prova)
			Util.removeFilesByPrefix(folderAtividade, id);
			
			
			if(config.getNumeroTurmas() > 1){
				//replica o roteiro pela quantidade de turmas
				for (int i = 1; i <= config.getNumeroTurmas(); i++) {
					//Neste caso o id do roteiro/prova vem no formato R01-OX. Precisamos apenas mudar o X
					String atividadeAtual = new String(config.getId().getBytes());
					atividadeAtual = atividadeAtual.replace("X", String.valueOf(i));
					ProfessorUploadConfiguration newConfig = 
							new ProfessorUploadConfiguration(atividadeAtual,config.getSemestre(), config.getTurma(),1);
					result = result + saveProfessorSubmission(ambiente, projetoCorrecao, newConfig) + "\n<br>";
				}
				
			}else{
				//caso base: processa o upload de um roteiro apenas
				
				// precisa verificar se o professor enviou um roteiro cadastrado.
				Validator.validate(config);
				
				
				String uploadEnvFileName =   
						Util.generateFileName(ambiente, config);
				String uploadCorrProjFileName =  
						Util.generateFileName(projetoCorrecao, config);

				
				File foutEnv = new File(folderAtividade.getParentFile(),uploadEnvFileName);
				//System.out.println("Arquivo ambiente: " + foutEnv.getAbsolutePath());
				if (!foutEnv.exists()) {
					foutEnv.mkdirs();
				}
				File foutCorrProj = new File(folderAtividade.getParentFile(),uploadCorrProjFileName);
				//System.out.println("Arquivo ambiente: " + foutCorrProj.getAbsolutePath());
				if (!foutCorrProj.exists()) {
					foutCorrProj.mkdirs();
				}
				
				Files.copy(ambiente.toPath(), foutEnv.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Files.copy(projetoCorrecao.toPath(), foutCorrProj.toPath(), StandardCopyOption.REPLACE_EXISTING);
				//PRECISA LOGAR OPERACOES DA APLICACAO???????
				//TODO
				
				//System.out.println("Arquivos copiados");
				//adicionando os arquivos no respectivo roteiro
				Map<String,Atividade> atividades = Configuration.getInstance().getAtividades();
				Atividade atividade = atividades.get(config.getId());
				if(atividade != null){
					((Roteiro) atividade).setArquivoAmbiente(foutEnv);
					((Roteiro) atividade).setArquivoProjetoCorrecao(foutCorrProj);
					atividades.put(config.getId(), atividade);
				}
				//agora eh persistir os dados dos roteiros em JSON
				File configFolder = new File(Constants.DEFAULT_CONFIG_FOLDER_NAME);
				if(!configFolder.exists()){
					throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
				}
				//File jsonFileRoteiros = new File(configFolder,Constants.JSON_FILE_ROTEIRO);
				//Util.writeRoteirosToJson(roteiros, jsonFileRoteiros);
				
				result = "Uploads realizados: " + foutEnv.getAbsolutePath() + ", " + foutCorrProj.getAbsolutePath() + " em " + Util.formatDate(new GregorianCalendar()); 
				//System.out.println(result);
				
				//ja cria também os links simbolicos para possibilitar a correcao
				String os = System.getProperty("os.name");
				if(!os.startsWith("Windows")){
					//windows nao permite a criação de links symbolicos 
					//System.out.println("Link to: " + uploadSubFolderTarget);
					Path newLink = (new File(Constants.REPORTS_FOLDER_NAME)).toPath();
					Path target = new File(Constants.CURRENT_SEMESTER_FOLDER,id).toPath();
					//se target nao existe entao ja cria ela
					if(!Files.exists(target)){
						Files.createDirectory(target);
					}
					Runtime.getRuntime().exec("ln -s " + target + " " + newLink);
					
				}else{
					//pode-se copiar por completo mas isso deve ser feito apos a execucao do corretor
					Path newLink = (new File(Constants.REPORTS_FOLDER_NAME)).toPath();
					Path target = new File(Constants.CURRENT_SEMESTER_FOLDER,id).toPath();
					//System.out.println("Link: " + newLink);
					//System.out.println("Target: " + target);
					
					//se target nao existe entao ja cria ela
					if(!Files.exists(newLink)){
						Files.createDirectory(newLink);
					}
					if(!Files.exists(target)){
						Files.createDirectory(target);
					}
					
				}
		}
		
		
		
		
		return result;
	}
	
	@Deprecated
	public static String saveProfessorTestSubmission(File ambiente, File projetoCorrecao, ProfessorUploadConfiguration config) throws StudentException, ConfigurationException, IOException, AtividadeException, ServiceException {
		String result = "";
		
		
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		if(!uploadFolder.exists()){
			uploadFolder.mkdirs();
		}
		
		//o nome da prova eh enviada. ela deve ser colocado na pasta 
		//<upload>/semestre/provas/<ID_PROVA>NomearquivoEnviado.zip
		//dois arquivos devem ser salvos:environment e correction-proj.
		//eles devem ser inseridos no objeto roteiro que esta no Map  Configuration.provas
		//e deve ser salvo um arquivo JSON mantendo toda essa estrutura. 
		String uploadSubFolder = Constants.CURRENT_SEMESTER + File.separator + Constants.PROVAS_FOLDER_NAME;
		//contem o id da prova P0X-0X
		String uploadSubFolderTarget = Constants.CURRENT_SEMESTER + File.separator + config.getId();
	
		File folderProvas = new File(uploadFolder,uploadSubFolder);
		String id = config.getId();
		if (config.getId().contains("X")) {
			id = id.substring(0, id.indexOf("X"));
		}
		// remove os arquivos de prova antes cadastrados (baseado no id da prova)
		Util.removeFilesByPrefix(folderProvas, id);
		/*
		// remove os arquivos de prova antes cadastrados (baseado no id da
		// prova)
		if (!os.startsWith("Windows")) {
			Runtime.getRuntime().exec("rm " + folderRoteiros.getAbsolutePath() + File.separator + id + "*.*");
		} else {
			System.out.println("RODANDO: " + "del " + folderRoteiros.getAbsolutePath() + File.separator + id + "*.*");
			Runtime.getRuntime().exec("del " + folderRoteiros.getAbsolutePath() + File.separator + id + "*.*");
		}*/
		
		if(config.getNumeroTurmas() > 1){
			//replica a prova pela quantidade de turmas
			for (int i = 1; i <= config.getNumeroTurmas(); i++) {
				//Neste caso o id do roteiro vem no formato P01-OX. Precisamos apenas mudar o X
				String roteiroAtual = new String(config.getId().getBytes());
				roteiroAtual = roteiroAtual.replace("X", String.valueOf(i));
				ProfessorUploadConfiguration newConfig = 
						new ProfessorUploadConfiguration(config.getSemestre(), config.getTurma(), 
								roteiroAtual, 1);
				//System.out.println("Roteiro atual: " + newConfig.getRoteiro());
				
				result = result + saveProfessorTestSubmission(ambiente, projetoCorrecao, newConfig) + "\n<br>";
			}
			
		}else{
			//caso base: processa o upload de um roteiro apenas
			
			// precisa verificar se o professor enviou um roteiro cadastrado.
			Validator.validate(config);
			
			String uploadEnvFileName =  uploadSubFolder + File.separator + 
					Util.generateFileName(ambiente, config);
			String uploadCorrProjFileName =  uploadSubFolder + File.separator + 
					Util.generateFileName(projetoCorrecao, config);

			File foutEnv = new File(uploadFolder,uploadEnvFileName);
			if (!foutEnv.exists()) {
				foutEnv.mkdirs();
			}
			File foutCorrProj = new File(uploadFolder,uploadCorrProjFileName);
			if (!foutCorrProj.exists()) {
				foutCorrProj.mkdirs();
			}
			
					
			Files.copy(ambiente.toPath(), foutEnv.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(projetoCorrecao.toPath(), foutCorrProj.toPath(), StandardCopyOption.REPLACE_EXISTING);
	
			
			//adicionando os arquivos na respectiva prova
			Map<String,Prova> provas = null; //Configuration.getInstance().getProvas();
			Prova prova = provas.get(config.getId());
			if(prova != null){
				prova.setArquivoAmbiente(foutEnv);
				prova.setArquivoProjetoCorrecao(foutCorrProj);
				provas.put(config.getId(), prova);
			}

			File configFolder = new File(Constants.DEFAULT_CONFIG_FOLDER_NAME);
			if(!configFolder.exists()){
				throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
			}
			File jsonFileProvas = new File(configFolder,Constants.JSON_FILE_PROVA);
			//System.out.println("Escrevendo no json: " + jsonFileRoteiros.getAbsolutePath());
			//System.out.println("Json existe: " + jsonFileRoteiros.exists());
			Util.writeProvasToJson(provas, jsonFileProvas);

			/*
			//agora eh persistir os dados dos roteiros em JSON
			File configFolder = new File(Constants.DEFAULT_CONFIG_FOLDER);
			if(!configFolder.exists()){
				throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
			}
			File jsonFileRoteiros = new File(configFolder,JSON_FILE_ROTEIRO);
			Util.writeRoteirosToJson(roteiros, jsonFileRoteiros);
			*/
			
			result = "Uploads realizados: " + foutEnv.getAbsolutePath() + ", " + foutCorrProj.getAbsolutePath() + " em " + Util.formatDate(new GregorianCalendar()); 
			
			
			
			//System.out.println(result);
			//ja cria também os links simbolicos para possibilitar a correcao
			String os = System.getProperty("os.name");
			if(!os.startsWith("Windows")){
				//windows nao permite a criação de links symbolicos 
				//System.out.println("Link to: " + uploadSubFolderTarget);
				Path newLink = (new File(Constants.REPORTS_FOLDER_NAME)).toPath();
				Path target = new File(uploadFolder,uploadSubFolderTarget).toPath();
				if(!Files.exists(target)){
					Files.createDirectory(target);
				}
				Runtime.getRuntime().exec("ln -s " + target + " " + newLink);
				
			}else{
				//pode-se copiar por completo mas isso deve ser feito apos a execucao do corretor
				Path newLink = (new File(Constants.REPORTS_FOLDER_NAME)).toPath();
				Path target = new File(uploadFolder,uploadSubFolderTarget).toPath();
				//se target nao existe entao ja cria ela
				if(!Files.exists(newLink)){
					Files.createDirectory(newLink);
				}
				if(!Files.exists(target)){
					Files.createDirectory(target);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Salva um arquivo recebido em upload numa pasta especifica e com o nome do
	 * aluno, recuperado de um mapeamento (arquivo excel do controle academico).
	 * 
	 * @return
	 * @throws StudentException 
	 * @throws ConfigurationException 
	 * @throws IOException 
	 * @throws AtividadeException 
	 * @throws ServiceException 
	 * @throws Exception 
	 */
	public static String saveStudentSubmission(File uploaded, StudentUploadConfiguration config) throws StudentException, ConfigurationException, IOException, AtividadeException, ServiceException {
		//TODO 
		
		String result = null;
		// precisa verificar se o aluno que enviou esta realmente matriculado.
		Validator.validate(config);
		
		Map<String,Student> students = Configuration.getInstance().getStudents();
		Student student = students.get(config.getMatricula());
		
		// precisa criar as pastas onde o arquivo vai ser uploaded. as pastas sao criadas 
		//nela sao colocadas as submissoes dos alunos e feito um log da submissao
		//File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		
		String id = config.getId();
		File atividadeFolder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		if(!atividadeFolder.exists()){
			atividadeFolder.mkdirs();
		}
		File submissionsAtividadeFolder = new File(atividadeFolder,Constants.SUBMISSIONS_FOLDER_NAME);
		if(!submissionsAtividadeFolder.exists()){
			submissionsAtividadeFolder.mkdirs();
		}
		//o nome do arquivo eh o nome do aluno cadastrado no sistema
		//String uploadSubFolder = Constants.CURRENT_SEMESTER + File.separator + config.getId() + File.separator + Constants.SUBMISSIONS_FOLDER_NAME; 
				//+ File.separator + config.getTurma();
		//o nome od arquivo recebido eh <MATRICULA>-<NODE DO ESTUDANTE>
		//o sistema de correcao tambem trabalha comesse formato para montar a tabela geral
		String uploadFileName =  student.getMatricula() + "-" + student.getNome() + ".zip";

		File fout = new File(submissionsAtividadeFolder,uploadFileName);
		if (!fout.exists()) {
			fout.mkdirs();
		}
		Files.move(uploaded.toPath(), fout.toPath(), StandardCopyOption.REPLACE_EXISTING);
		//PRECISA LOGAR OPERACOES DA APLICACAO???????
		//TODO
		
		//faz o log dos arquivos enviados com seus respectivos owners e salva o json com o nome do aluno
		File filesOwnersJson = new File(fout.getParentFile(),student.getMatricula() + "-" + student.getNome() + ".json");
		Util.writeFilesOwnersToJson(config.getFilesOwners(), filesOwnersJson);
		
		// precisa fazer um historico das submissoes de cada estudante para cada roteiro.
		// quando acontece uma submissao, o sistema retorna um ticket com o
		// historico de todas as submissoes do aluno
		result = UploadLogger.logSubmission(fout, config,student.getNome());
		
		return result;
	}

	
	
	
	public static Map<String,File[]> allSubmissions(){
		Map<String,File[]> result = new HashMap<String,File[]>();

		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		if(currentSemester.exists()){

			File[] folders = currentSemester.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File arg0) {
					return Constants.PATTERN_ROTEIRO.matcher(arg0.getName()).matches() 
							|| Constants.PATTERN_PROVA.matcher(arg0.getName()).matches();
				}
			});
			for (int i = 0; i < folders.length; i++) {
				String folderName = folders[i].getName();
				File[] submissions = listSubmissions(folders[i]);
				result.put(folderName,submissions);
			}
		}
		
		return result;
	}
	
	/**
	 * Mostra a listagem das submissoes de prova ou roteiro ordenadas por nome
	 * do aluno. 
	 * @param folder a pasta raiz de um roteiro ou prova
	 * @return
	 */
	public static File[] listSubmissions(File folder){
		File[] result = new File[0];
		File submissionsFolder = new File(folder, Constants.SUBMISSIONS_FOLDER_NAME); 
		
		if(submissionsFolder.exists()){
			result = submissionsFolder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".zip");
				}
			});
		}
		Arrays.sort(result, (f1,f2) -> {
			int res = f1.getName().compareTo(f2.getName());
			int indexOfDash = f1.getName().lastIndexOf("-");
			if(indexOfDash != -1){
				String nameF1 = f1.getName().substring(indexOfDash + 1);
				indexOfDash = f2.getName().lastIndexOf("-");
				if(indexOfDash != -1){
					String nameF2 = f2.getName().substring(indexOfDash + 1);
					res = nameF1.compareTo(nameF2);
				}
			}
			
			return res;
		});
		return result;
	}

}
