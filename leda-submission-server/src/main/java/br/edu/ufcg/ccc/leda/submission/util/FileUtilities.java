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
		Map<String,Prova> provas = Configuration.getInstance().getProvas();
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
		Map<String,Roteiro> roteiros = Configuration.getInstance().getRoteiros();
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
			
			//o nome do arquivo eh enviado. ele deve ser colocado na pasta 
			//<upload>/semestre/roteiros/<ID_ROTEIRO>NomearquivoEnviado.zip
			//dois arquivos devem ser salvos:environment e correction-proj.
			//eles devem ser inseridos no objeto roteiro que esta no Map  Configuration.roteiros
			//e deve ser salvo um arquivo JSON mantendo dota essa estrutura. 
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
							new ProfessorUploadConfiguration(config.getSemestre(), config.getTurma(), 
									atividadeAtual, 1);
					result = result + saveProfessorSubmission(ambiente, projetoCorrecao, newConfig) + "\n<br>";
				}
				
			}else{
				//caso base: processa o upload de um roteiro apenas
				
				// precisa verificar se o professor enviou um roteiro cadastrado.
				Validator.validate(config);
				
				
				String uploadEnvFileName =  folderAtividade.getName() + File.separator + 
						Util.generateFileName(ambiente, config);
				String uploadCorrProjFileName =  folderAtividade.getName() + File.separator + 
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
				File jsonFileRoteiros = new File(configFolder,Constants.JSON_FILE_ROTEIRO);
				//Util.writeRoteirosToJson(roteiros, jsonFileRoteiros);
				
				result = "Uploads realizados: " + foutEnv.getAbsolutePath() + ", " + foutCorrProj.getAbsolutePath() + " em " + Util.formatDate(new GregorianCalendar()); 
				//System.out.println(result);
				
				//ja cria também os links simbolicos para possibilitar a correcao
				String os = System.getProperty("os.name");
				if(!os.startsWith("Windows")){
					//windows nao permite a criação de links symbolicos 
					//System.out.println("Link to: " + uploadSubFolderTarget);
					Path newLink = (new File(Constants.REPORTS_FOLDER_NAME)).toPath();
					Path target = folderAtividade.toPath();
					//se target nao existe entao ja cria ela
					if(!Files.exists(target)){
						Files.createDirectory(target);
					}
					Runtime.getRuntime().exec("ln -s " + target + " " + newLink);
					
				}else{
					//pode-se copiar por completo mas isso deve ser feito apos a execucao do corretor
					Path newLink = (new File(Constants.REPORTS_FOLDER_NAME)).toPath();
					Path target = folderAtividade.toPath();
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
			Map<String,Prova> provas = Configuration.getInstance().getProvas();
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
		//TODO PPPPPPPPPP
		
		String result = null;
		// precisa verificar se o aluno que enviou esta realmente matriculado.
		Validator.validate(config);
		
		Map<String,Student> students = Configuration.getInstance().getStudents();
		Student student = students.get(config.getMatricula());
		
		// precisa criar as pastas onde o arquivo vai ser uploaded. as pastas sao criadas 
		//na past default de uploads e seguem o padrao: <default>/semestre/roteiro/turma
		//nela sao colocadas as submissoes dos alunos e feito um log da submissao
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		if(!uploadFolder.exists()){
			uploadFolder.mkdirs();
		}
		
		//o nome do arquivo eh o nome do aluno cadastrado no sistema
		//String uploadFileName = config.getSemestre() + File.separator + config.getRoteiro() 
		//		+ File.separator + config.getTurma() + File.separator + uploaded.getName().substring(uploaded.getName().indexOf(".") + 1);
		String uploadSubFolder = Constants.CURRENT_SEMESTER + File.separator + config.getId() + File.separator + Constants.SUBMISSIONS_FOLDER_NAME; 
				//+ File.separator + config.getTurma();
		//o nome od arquivo recebido eh <MATRICULA>-<NODE DO ESTUDANTE>
		//o sistema de correcao tambem trabalha comesse formato para montar a tabela geral
		String uploadFileName =  uploadSubFolder + File.separator + 
				student.getMatricula() + "-" + student.getNome() + ".zip";

		File fout = new File(uploadFolder,uploadFileName);
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

	/**
	 * Carrega os nomes dos alunso paseando-se nas matriculas. Os alunos
	 * fornecem a matricula e o nome sera pego da lista dos amtriculados excel).
	 * Esse mapa deve ser atualizado caso o arquivo de excel seja atualizado
	 * também. Ver isso nos frameworks do engenho onde vai rodar ou entao
	 * colocar uma url no sistema para ele fazer isso automaticamente quando
	 * invocada essafuncao por get. Os arquivos dos estudantes devem ser lidos
	 * de uma pasta pegando todos os dados em arquivos xls ou xlxs que comecam
	 * com o nome frequencia.
	 * 
	 * @return
	 * @throws ConfigurationException 
	 * @throws IOException 
	 * @throws BiffException 
	 */
	public static Map<String, Student> loadStudentLists() throws IOException, BiffException {
		Map<String, Student> result = new HashMap<String, Student>();
		File configFolder = new File(Constants.DEFAULT_CONFIG_FOLDER_NAME);
		if(!configFolder.exists()){
			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		}
		//System.out.println("%%%%%%CONFIG FOLDER: " + configFolder.getAbsolutePath());
		File[] excelFiles = configFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				boolean frequencia = pathname.getName().startsWith("frequencia");
				boolean excel = pathname.getName().endsWith(".xls") || pathname.getName().endsWith(".xlsx");
				return  frequencia && excel;
			}
		});
		//System.out.println("%%%%%%%%% EXCEL FILES: " + excelFiles.length);
		for (int i = 0; i < excelFiles.length; i++) {
			loadStudentsFromExcelFile(excelFiles[i],result);
		}
		return result;
	}

	private static String extractTurmaFromExcelFile(File excelFile){
		String turma = "00";
		String nomeArquivo = excelFile.getName();
		if(nomeArquivo.indexOf("-01_") != -1){
			turma = "01";
		} else if (nomeArquivo.indexOf("-02_") != -1){
			turma = "02";
		} else if (nomeArquivo.indexOf("-03_") != -1){
			turma = "03";
		}
		
		return turma;
	}
	
	/**
	 * Arquivo excel tem que ter extensao xlsx
	 * 
	 * @param excelFile
	 * @param map
	 * @throws IOException
	 * @throws BiffException
	 */
	protected static void loadStudentsFromExcelFile(File excelFile, Map<String,Student> map) throws IOException, BiffException{
		loadStudentsFromXLSX(excelFile, map);
	}
	
	/*public static Map<String, Roteiro> loadRoteiros() throws IOException, ConfigurationException{
		Map<String, Roteiro> roteiros = new HashMap<String, Roteiro>();
		File configFolder = new File(Constants.DEFAULT_CONFIG_FOLDER_NAME);
		if(!configFolder.exists()){
			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		}
		//tenta ler primeiro do json
		File jsonFileRoteiros = new File(configFolder,Constants.JSON_FILE_ROTEIRO);
		//Map<String,Roteiro> roteirosFromJson = new HashMap<String,Roteiro>();
		
		//ver a possibilidade de montar a lista dos arquivos de cada roteiro 
		//lendo os arquivos diretamente na pasta de upload dos roteiros
		//isso eliminaria a necessidade do json
		//if(jsonFileRoteiros.exists()){
		//	roteirosFromJson = Util.loadRoteirosFromJson(jsonFileRoteiros);
		//}
		
		//carrega os roteiros de acordo com o arquivo excel
		File excelFileRoteiro = new File(configFolder,Constants.EXCEL_FILE_ROTEIRO);
		loadRoteirosFromExcelFile(excelFileRoteiro, roteiros);
		
		//sobrescreve os dados lidos do excel com os do json apenas os arquivos de ambiente e correcao
		//reuseFiles(result,roteirosFromJson);
		loadRoteirosFromUploadFolder(roteiros);
		
		//com o reuso pode ter acontecido de alguma data ter sido modificada. entao salvamos novamente no json
		Util.writeRoteirosToJson(roteiros, jsonFileRoteiros);
		
		return roteiros;
	}*/
	/*private static void reuseFiles(Map<String,Roteiro> mapExcel, Map<String,Roteiro> mapJson){
		Set<String> keys = mapJson.keySet();
		for (String key : keys) {
			Roteiro roteiroJson = mapJson.get(key);
			Roteiro roteiroExcel = mapExcel.get(key);
			roteiroExcel.setArquivoAmbiente(roteiroJson.getArquivoAmbiente());
			roteiroExcel.setArquivoProjetoCorrecao(roteiroJson.getArquivoProjetoCorrecao());
			mapExcel.put(key, roteiroExcel);
		}
	} */
	
	/*public static Map<String, Prova> loadProvas() throws IOException, ConfigurationException{
		Map<String, Prova> provas = new HashMap<String, Prova>();
		File configFolder = new File(Constants.DEFAULT_CONFIG_FOLDER_NAME);
		if(!configFolder.exists()){
			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		}
		//tenta ler primeiro do json
		File jsonFileProvas = new File(configFolder,Constants.JSON_FILE_PROVA);
		//Map<String,Roteiro> roteirosFromJson = new HashMap<String,Roteiro>();
		
		//ver a possibilidade de montar a lista dos arquivos de cada roteiro 
		//lendo os arquivos diretamente na pasta de upload dos roteiros
		//isso eliminaria a necessidade do json
		//if(jsonFileRoteiros.exists()){
		//	roteirosFromJson = Util.loadRoteirosFromJson(jsonFileRoteiros);
		//}
		
		//carrega os roteiros de acordo com o arquivo excel
		File excelFileProva = new File(configFolder,Constants.EXCEL_FILE_PROVA);
		loadProvasFromExcelFile(excelFileProva, provas);
		
		//sobrescreve os dados lidos do excel com os do json apenas os arquivos de ambiente e correcao
		//reuseFiles(result,roteirosFromJson);
		loadProvasFromUploadFolder(provas);
		
		//com o reuso pode ter acontecido de alguma data ter sido modificada. entao salvamos novamente no json
		Util.writeProvasToJson(provas, jsonFileProvas);
		
		return provas;
	}*/
	
	/*private static void loadRoteirosFromExcelFile(File xlsxFile, Map<String,Roteiro> roteiros) throws IOException{
		FileInputStream fis = new FileInputStream(xlsxFile);
		
		org.apache.poi.ss.usermodel.Workbook myWorkBook = null;
		org.apache.poi.ss.usermodel.Sheet mySheet = null;
		try{
			myWorkBook = new XSSFWorkbook (fis);
			mySheet = myWorkBook.getSheetAt(0);
		}catch(POIXMLException ex){
			//problema na leitura do arquivo excel
			ex.printStackTrace();
		}
	
		Iterator<Row> rowIterator = mySheet.iterator();
		FormulaEvaluator evaluator = myWorkBook.getCreationHelper().createFormulaEvaluator();
		while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator(); 
            if(cellIterator.hasNext()){
            	org.apache.poi.ss.usermodel.Cell cellIdRoteiro = row.getCell(0);
            	if(cellIdRoteiro != null){
            		String idRoteiro = cellIdRoteiro.getStringCellValue(); //
            		if(idRoteiro.length() == 6){ //pode trabalhar aqui com o matcher
            			org.apache.poi.ss.usermodel.Cell cellNome = row.getCell(1); //celula com o nome
            			org.apache.poi.ss.usermodel.Cell cellDescricao = row.getCell(2); //celula com a descricao
            			org.apache.poi.ss.usermodel.Cell cellDataLiberacao = row.getCell(3); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteEnvioNormal = row.getCell(4); //celula de datahora limite de envio normal
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteEnvioAtraso = row.getCell(5); //celula de datahora limite de envio com atraso
            			org.apache.poi.ss.usermodel.Cell cellNomeMonitor = row.getCell(6); //celula de nome do monitor
            			org.apache.poi.ss.usermodel.Cell cellDataInicioCorrecao = row.getCell(7); //celula de datahora de atricuicao ao monitor
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteCorrecao = row.getCell(8); //celula de datahora limite da correcao
            			org.apache.poi.ss.usermodel.Cell cellLinksVideoAulas = row.getCell(8); //celula de datahora limite da correcao
            			
            			GregorianCalendar dataHoraLiberacao = Util.buildDate(cellDataLiberacao.getDateCellValue());
            			cellDataLimiteEnvioNormal = evaluator.evaluateInCell(cellDataLimiteEnvioNormal);
            			GregorianCalendar dataHoraLimiteEnvioNormal = Util.buildDate(cellDataLimiteEnvioNormal.getDateCellValue());
            			cellDataLimiteEnvioAtraso = evaluator.evaluateInCell(cellDataLimiteEnvioAtraso);
            			GregorianCalendar dataHoraLimiteEnvioAtraso = Util.buildDate(cellDataLimiteEnvioAtraso.getDateCellValue());
            			cellDataInicioCorrecao = evaluator.evaluateInCell(cellDataInicioCorrecao);
            			GregorianCalendar dataInicioCorrecao = Util.buildDate(cellDataInicioCorrecao.getDateCellValue());
            			cellDataLimiteCorrecao = evaluator.evaluateInCell(cellDataLimiteCorrecao);
            			GregorianCalendar dataLimiteCorrecao = Util.buildDate(cellDataLimiteCorrecao.getDateCellValue());
            			
            			List<URL> links = Util.loadLinks(cellLinksVideoAulas.getStringCellValue());
            			
            			Roteiro roteiro = new Roteiro(idRoteiro,cellNome.getStringCellValue(), 
            					cellDescricao.getStringCellValue(), dataHoraLiberacao, links,
            					dataHoraLimiteEnvioNormal,dataHoraLimiteEnvioAtraso,
            					cellNomeMonitor.getStringCellValue(),dataInicioCorrecao,dataLimiteCorrecao, null,null);
            			roteiros.put(idRoteiro, roteiro);
            		}
            	}
            }
		}
		myWorkBook.close();
	}
	
	private static void loadProvasFromExcelFile(File xlsxFile, Map<String,Prova> provas) throws IOException{
		FileInputStream fis = new FileInputStream(xlsxFile);
		
		org.apache.poi.ss.usermodel.Workbook myWorkBook = null;
		org.apache.poi.ss.usermodel.Sheet mySheet = null;
		try{
			myWorkBook = new XSSFWorkbook (fis);
			mySheet = myWorkBook.getSheetAt(0);
		}catch(POIXMLException ex){
			//problema na leitura do arquivo excel
			ex.printStackTrace();
		}
	
		Iterator<Row> rowIterator = mySheet.iterator();
		FormulaEvaluator evaluator = myWorkBook.getCreationHelper().createFormulaEvaluator();
		while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator(); 
            if(cellIterator.hasNext()){
            	org.apache.poi.ss.usermodel.Cell cellIdRoteiro = row.getCell(0);
            	if(cellIdRoteiro != null){
            		String idProva = cellIdRoteiro.getStringCellValue(); //
            		if(idProva.length() == 6){ //talvez precise mudar para um regex que casa com o id das provas/roteiros 
            			
            			org.apache.poi.ss.usermodel.Cell cellNome = row.getCell(1); //celula com o nome
            			org.apache.poi.ss.usermodel.Cell cellDescricao = row.getCell(2); //celula com a descricao
            			org.apache.poi.ss.usermodel.Cell cellDataLiberacao = row.getCell(3); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteEnvioNormal = row.getCell(4); //celula de datahora limite de envio normal
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteEnvioAtraso = row.getCell(5); //celula de datahora limite de envio com atraso
            			org.apache.poi.ss.usermodel.Cell cellNomeMonitor = row.getCell(6); //celula de nome do monitor
            			org.apache.poi.ss.usermodel.Cell cellDataInicioCorrecao = row.getCell(7); //celula de datahora de atricuicao ao monitor
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteCorrecao = row.getCell(8); //celula de datahora limite da correcao
            			org.apache.poi.ss.usermodel.Cell cellLinksVideoAulas = row.getCell(8); //celula de datahora limite da correcao
            			
            			GregorianCalendar dataHoraLiberacao = Util.buildDate(cellDataLiberacao.getDateCellValue());
            			cellDataLimiteEnvioNormal = evaluator.evaluateInCell(cellDataLimiteEnvioNormal);
            			GregorianCalendar dataHoraLimiteEnvioNormal = Util.buildDate(cellDataLimiteEnvioNormal.getDateCellValue());
            			cellDataLimiteEnvioAtraso = evaluator.evaluateInCell(cellDataLimiteEnvioAtraso);
            			GregorianCalendar dataHoraLimiteEnvioAtraso = Util.buildDate(cellDataLimiteEnvioAtraso.getDateCellValue());
            			cellDataInicioCorrecao = evaluator.evaluateInCell(cellDataInicioCorrecao);
            			GregorianCalendar dataInicioCorrecao = Util.buildDate(cellDataInicioCorrecao.getDateCellValue());
            			cellDataLimiteCorrecao = evaluator.evaluateInCell(cellDataLimiteCorrecao);
            			GregorianCalendar dataLimiteCorrecao = Util.buildDate(cellDataLimiteCorrecao.getDateCellValue());
            			
            			List<URL> links = Util.loadLinks(cellLinksVideoAulas.getStringCellValue());
            			
            			Prova prova = new Prova(idProva,cellNome.getStringCellValue(), 
            					cellDescricao.getStringCellValue(), dataHoraLiberacao, links,
            					dataHoraLimiteEnvioNormal,dataHoraLimiteEnvioAtraso,
            					cellNomeMonitor.getStringCellValue(),dataInicioCorrecao,dataLimiteCorrecao, null,null);
            	
            			provas.put(idProva, prova);
            		}
            	}
            }
		}
		myWorkBook.close();
	}*/
	
	private static void loadStudentsFromXLSX(File xlsxFile,Map<String, Student> map) throws IOException {
		FileInputStream fis = new FileInputStream(xlsxFile);
		
		org.apache.poi.ss.usermodel.Workbook myWorkBook = null;
		org.apache.poi.ss.usermodel.Sheet mySheet = null;
		try{
			myWorkBook = new XSSFWorkbook (fis);
			mySheet = myWorkBook.getSheetAt(0);
		}catch(POIXMLException ex){
			myWorkBook = new HSSFWorkbook (fis);
			mySheet = myWorkBook.getSheetAt(0);
		}
		String turma = extractTurmaFromExcelFile(xlsxFile);
		if(turma.equals("0")){
			myWorkBook.close();
			throw new RuntimeException("Turma invalida!");
		}
		
		Iterator<Row> rowIterator = mySheet.iterator();
		
		while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator(); 
            if(cellIterator.hasNext()){
            	org.apache.poi.ss.usermodel.Cell cell0 = row.getCell(0);
            	if(cell0 != null){
            			org.apache.poi.ss.usermodel.Cell cell1 = row.getCell(1);
            			org.apache.poi.ss.usermodel.Cell cell2 = row.getCell(2);
            			Student student = new Student(cell1.getStringCellValue(), cell2.getStringCellValue(), turma);
            			map.put(cell1.getStringCellValue(), student);
            	}
            }
		}
		myWorkBook.close();
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


	
	
	//list.addAll(Arrays.asList(files));
	
    //TODO
	//Precisa de um metodo que monte a lista dos arquivos para cada roteiro diretamente da pasta
	//onde foram feitos os uploads. Ele precisa ser por id do roteiro e pegar environment e correction
	//e fazer as devidas astribuicoes aos roteiros. 
	/*public static void loadRoteirosFromUploadFolder(Map<String,Roteiro> roteiros){
		File roteirosFolder = new File(Constants.UPLOAD_FOLDER_NAME, Constants.CURRENT_SEMESTER + File.separator + Constants.ROTEIROS_FOLDER_NAME);
		if(roteirosFolder.exists()){
			Set<String> keys = roteiros.keySet();
			for (String key : keys) {
				Roteiro roteiro = roteiros.get(key);
				File[] files = roteirosFolder.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						//tem arquivos cadastrado para o roteiro pelo ID
						return pathname.getName().startsWith(roteiro.getId());
					}
				});
				//tem arquivo cadastrado para o roteiro
				File environment = null;
				File correction = null;
				if(files.length > 0){
					for (int i = 0; i < files.length; i++) {
						if(files[i].getName().contains("environment")){
							environment = files[i];
						} else{
							correction = files[i];
						}
					}
				}
				roteiro.setArquivoAmbiente(environment);
				roteiro.setArquivoProjetoCorrecao(correction);
			}
		}
	}
	public static void loadProvasFromUploadFolder(Map<String,Prova> provas){
		File provasFolder = new File(Constants.UPLOAD_FOLDER_NAME, Constants.CURRENT_SEMESTER + File.separator + Constants.PROVAS_FOLDER_NAME);
		if(provasFolder.exists()){
			Set<String> keys = provas.keySet();
			for (String key : keys) {
				Prova prova = provas.get(key);
				File[] files = provasFolder.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						//tem arquivos cadastrado para o roteiro pelo ID
						return pathname.getName().startsWith(prova.getId());
					}
				});
				//tem arquivo cadastrado para o roteiro
				File environment = null;
				File correction = null;
				if(files.length > 0){
					for (int i = 0; i < files.length; i++) {
						if(files[i].getName().contains("environment")){
							environment = files[i];
						} else{
							correction = files[i];
						}
					}
				}
				prova.setArquivoAmbiente(environment);
				prova.setArquivoProjetoCorrecao(correction);
			}
		}
	}*/
}
