package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.google.gdata.util.ServiceException;

public class FileUtilities {

	
	public static File getEnvironmentAtividade(String id, String matricula) throws ConfigurationException, IOException, AtividadeException, ServiceException{
		File environment = null;
		if(Constants.PATTERN_ROTEIRO_ESPECIAL.matcher(id).matches()){
			//requisicao de um downlaod de roteiro especial - ignorar matricula
			
			Atividade atividade = Configuration.getInstance().getRoteirosEspeciais()
					.stream()
					.filter( a -> a.getId().equals(id))
					.findFirst().orElse(null);
					
			environment = ((Roteiro) atividade).getArquivoAmbiente();
		}else{
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
		}
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
		int numeroTurmas = Configuration.getInstance().getTurmas().size();
		//System.out.println("Tem " + numeroTurmas + " turmas");
		if(config.getId().contains("X")){
			for (int i = 1; i <= numeroTurmas; i++) {
				if (config.getId().contains("X")) {
					String idSemX = config.getId().replace("X", String.valueOf(i));
					ProfessorUploadConfiguration newConfig = new ProfessorUploadConfiguration(idSemX, config.getSemestre(), config.getTurma(), config.getNumeroTurmas());
					result = result + saveProfessorSubmission(ambiente, projetoCorrecao,newConfig,i) + "\n";
				}else{
					result = result + saveProfessorSubmission(ambiente, projetoCorrecao,config,i) + "\n";
			
				}
			}
		}else{
			result = result + saveProfessorSubmission(ambiente, projetoCorrecao,config, Integer.valueOf(config.getId().substring(5))) + "\n";
		}
		
		return result;
	}
	
	private static String saveProfessorSubmission(File ambiente, File projetoCorrecao, ProfessorUploadConfiguration config, int turma) throws IOException, ConfigurationException, AtividadeException, ServiceException {
		String result = "";
		String id = config.getId();
		
		
		File folderAtividade = new File(Constants.PROVAS_FOLDER, id);
		if (Constants.PATTERN_ROTEIRO.matcher(id).matches() 
				|| Constants.PATTERN_ROTEIRO_REVISAO.matcher(id).matches()
				|| Constants.PATTERN_ROTEIRO_ESPECIAL.matcher(id).matches()) {

			folderAtividade = new File(Constants.ROTEIROS_FOLDER, id);
		}
		

		// remove os arquivos de prova antes cadastrados (baseado no id da
		// prova)
		Util.removeFilesByPrefix(folderAtividade.getParentFile(), id);

		// precisa verificar se o professor enviou um roteiro cadastrado.
		//ProfessorUploadConfiguration newConfig = new ProfessorUploadConfiguration(id, config.getSemestre(), config.getTurma(), config.getNumeroTurmas());
		Validator.validate(config);

		String uploadEnvFileName = Util.generateFileName(ambiente, config);
		String uploadCorrProjFileName = Util.generateFileName(projetoCorrecao, config);

		File foutEnv = new File(folderAtividade.getParentFile(), uploadEnvFileName);
		//System.out.println("Arquivo ambiente: " + foutEnv.getAbsolutePath());
		if (!foutEnv.exists()) {
			foutEnv.mkdirs();
		}
		File foutCorrProj = new File(folderAtividade.getParentFile(), uploadCorrProjFileName);
		// System.out.println("Arquivo ambiente: " +
		// foutCorrProj.getAbsolutePath());
		if (!foutCorrProj.exists()) {
			foutCorrProj.mkdirs();
		}

		Files.copy(ambiente.toPath(), foutEnv.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(projetoCorrecao.toPath(), foutCorrProj.toPath(), StandardCopyOption.REPLACE_EXISTING);
		// PRECISA LOGAR OPERACOES DA APLICACAO???????
		// TODO

		// System.out.println("Arquivos copiados");
		// adicionando os arquivos no respectivo roteiro
		Map<String, Atividade> atividades = Configuration.getInstance().getAtividades();
		if(Constants.PATTERN_ROTEIRO_ESPECIAL.matcher(id).matches()){
			atividades = Configuration.getInstance().getTodasAtividades();
		}
		Atividade atividade = atividades.get(config.getId());
		if (atividade != null) {
			((Roteiro) atividade).setArquivoAmbiente(foutEnv);
			((Roteiro) atividade).setArquivoProjetoCorrecao(foutCorrProj);
			atividades.put(config.getId(), atividade);
		}
		// agora eh persistir os dados dos roteiros em JSON
		//File configFolder = new File(Constants.DEFAULT_CONFIG_FOLDER_NAME);
		//if (!configFolder.exists()) {
		//	throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		//}
		// File jsonFileRoteiros = new
		// File(configFolder,Constants.JSON_FILE_ROTEIRO);
		// Util.writeRoteirosToJson(roteiros, jsonFileRoteiros);

		result = "Uploads realizados: " + foutEnv.getAbsolutePath() + ", " + foutCorrProj.getAbsolutePath() + " em "
				+ Util.formatDate(new GregorianCalendar());
				// System.out.println(result);

		// ja cria também os links simbolicos para possibilitar a correcao
		// apenas se a atividade nao for roteiro especial
		if (!Constants.PATTERN_ROTEIRO_ESPECIAL.matcher(id).matches()) {
			String os = System.getProperty("os.name");
			if (!os.startsWith("Windows")) {
				// windows nao permite a criação de links symbolicos
				// System.out.println("Link to: " + uploadSubFolderTarget);
				Path newLink = (new File(Constants.REPORTS_FOLDER_NAME)).toPath();
				Path target = new File(Constants.CURRENT_SEMESTER_FOLDER, id).toPath();
				// se target nao existe entao ja cria ela
				if (!Files.exists(target)) {
					Files.createDirectory(target);
				}
				Runtime.getRuntime().exec("ln -s " + target + " " + newLink);

			} else {
				// pode-se copiar por completo mas isso deve ser feito apos
				// a execucao do corretor
				Path newLink = (new File(Constants.REPORTS_FOLDER_NAME)).toPath();
				Path target = new File(Constants.CURRENT_SEMESTER_FOLDER, id).toPath();
				// System.out.println("Link: " + newLink);
				// System.out.println("Target: " + target);

				// se target nao existe entao ja cria ela
				if (!Files.exists(newLink)) {
					Files.createDirectory(newLink);
				}
				if (!Files.exists(target)) {
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
	
	/**
	 * Salva a frequencia na pasta do CURRENT_SEMESTER, sobrescrevendo o arquivo de frequencia
	 * da mesma turma. O arquivo eh o do download do controle academico. 
	 * @param excelFileFrequencia
	 * @throws IOException 
	 */
	public static void salvarFrequencia(File excelFileFrequencia) throws IOException{
		String turma = Util.extractTurmaFromExcelFile(excelFileFrequencia);
		if(!turma.equals("0")){ //arquivo parseado corretamente
			//remove o que já tem la e depois salva
			File[] frequencias = Constants.CURRENT_SEMESTER_FOLDER.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().startsWith("frequencia") && pathname.getName().contains("-" + turma + "_");
				}
			});
			if(frequencias != null){
				if(frequencias.length == 1){
					Files.delete(frequencias[0].toPath());
				}
			}
			int index = excelFileFrequencia.getName().indexOf("frequencia");
			String name = excelFileFrequencia.getName().substring(index);
			
			File fout = new File(Constants.CURRENT_SEMESTER_FOLDER,name);
			if (!fout.exists()) {
				fout.mkdirs();
			}
			Files.move(excelFileFrequencia.toPath(), fout.toPath(), StandardCopyOption.REPLACE_EXISTING);

		}

	}
	
	public static void salvarArquivoSenhas(File excelFileSenhas) throws IOException{
		File senhas = new File(Constants.CURRENT_SEMESTER_FOLDER,Constants.EXCEL_SENHAS_FILE_NAME);
		if(senhas.exists()){
			Files.delete(senhas.toPath());
		}
		int index = excelFileSenhas.getName().indexOf(Constants.EXCEL_SENHAS_FILE_NAME);
		String name = excelFileSenhas.getName().substring(index);
		
		File fout = new File(Constants.CURRENT_SEMESTER_FOLDER,name);
		if (!fout.exists()) {
			fout.mkdirs();
		}
		Files.move(excelFileSenhas.toPath(), fout.toPath(), StandardCopyOption.REPLACE_EXISTING);


	}

	public static void main(String[] args) throws ConfigurationException, IOException, AtividadeException, ServiceException {
		File env = FileUtilities.getEnvironmentAtividade("RE3-01", "");
		String name = env.getName();
	}
}
