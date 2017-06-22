package plag.runner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.edu.ufcg.ccc.leda.util.Student;
import plag.parser.CachingDetectionResult;
import plag.parser.SubmissionDetectionResult;
import plag.parser.plaggie.ConfigurationException;
import plag.parser.plaggie.Plaggie;
import plag.parser.plaggie.PlaggieUFCG;

public class PlagRunner {
	
	private static final String CONF_FILE = "/plaggie.properties";
	public static final String ANALYSIS_FOLDER_NAME = "analysis";
	private File analysisFolder;
	private File atividadeFolder;
	private Properties properties;
	
	
	public PlagRunner(File atividadeFolder) throws URISyntaxException, IOException {
		this.properties = loadConfigFile();
		if(!atividadeFolder.exists()){
			throw new RuntimeException("Pasta da atividade " + atividadeFolder.getAbsolutePath() + " nao encontrada!");
		}
		this.analysisFolder = new File(atividadeFolder,ANALYSIS_FOLDER_NAME);
		if(!this.analysisFolder.exists()){
			analysisFolder.mkdir();
		}
		this.atividadeFolder = atividadeFolder;
	}

	
	public File getAnalysisFolder() {
		return analysisFolder;
	}


	public void setAnalysisFolder(File analysisFolder) {
		this.analysisFolder = analysisFolder;
	}


	public File getAtividadeFolder() {
		return atividadeFolder;
	}


	public void setAtividadeFolder(File atividadeFolder) {
		this.atividadeFolder = atividadeFolder;
	}


	public Properties getProperties() {
		return properties;
	}


	public void setProperties(Properties properties) {
		this.properties = properties;
	}


	public ArrayList<SimilarityAnalysisResult> runPlagiarismAnalysis(String submissionsSubFolderName,String[] fileNames) throws Exception{
		File submissionsFolder = new File(this.atividadeFolder,submissionsSubFolderName);
		if(!submissionsFolder.exists()){
			throw new RuntimeException("Pasta de submissoes " + submissionsFolder.getAbsolutePath() + " nao encontrada!");
		}
		ArrayList detResults = this.plagAnalysis(submissionsFolder, fileNames);
		return this.processResults(detResults);
	}
	
	//pode ser que as configuracoes sejam um parametro ou algumas delas possam 
	//ser passadas por parametro para o MOJO
	private Properties loadConfigFile() throws URISyntaxException, IOException{
		Properties prop = new Properties();
		
		String confFile = CONF_FILE;
		InputStream is = Plaggie.class.getResourceAsStream(CONF_FILE);
		// URI uri = new URI(is.)
		URL url = Plaggie.class.getResource(CONF_FILE);
		File fConfFile = new File(url.toURI().getPath());
		if (!fConfFile.exists()) {
			System.out.println("Configuration file " + confFile + " not found!");
			System.exit(1);
		}
		prop.load(url.openStream());
		
		return prop;
	}
	/**
	 * 
	 * Recebe a pasta de submissoes dos alunos e compara os arquivos fornecidos em 
	 * determinada lista de arquivos dada por fileNames.
	 * 
	 * @param submissionsFolder
	 * @param fileNames
	 * @throws Exception 
	 */
	public ArrayList plagAnalysis(File submissionsFolder,String[] fileNames) throws Exception {
		//precisa: usar uma pasta temporaria (cria uma pasta analysis na pasta da atividade)
		//criar uma pasta para cada aluno (pode ser a matricula)
		//copiar apenas os arquivos especificos informados na lista
		// para a pasta do aluno correspondente. 
		createAnalysisFolder(submissionsFolder, fileNames);
		
		//depois roda a analise na pasta de analises gerada. cria uma configuracao
		//padrao ou especifica 
		PlaggieUFCG plag = new PlaggieUFCG(this.analysisFolder,this.properties);
		plag.run(fileNames);
		
		return plag.detResults;
	}
	

	/**
	 * Cria a pasta de analise de um estudante e ja copia os arquivos pra la. 
	 *  //nome da pasta de analises pode ser uma constante global
		//cria uma subpasta "analysis" na pasta pai de submissionsFolder
		//varre as subpastas de submissionsFolder (com as submissoes dos alunos)
		//copia de cada subpasta os arquivos informados em fileNames (busca recursiva)

	 * @param studentFolder
	 * @return
	 * @throws IOException 
	 */
	private File createAnalysisFolder(File submissionsFolder, String[] fileNames) throws IOException{
		File analysisFolder = new File(submissionsFolder.getParentFile(),ANALYSIS_FOLDER_NAME);
		if(!analysisFolder.exists()){
			analysisFolder.mkdirs();
		}
		File[] subFolders = submissionsFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		if(subFolders.length > 0){
			for (int i = 0; i < subFolders.length; i++) {
				File studentFolder = subFolders[i];
				File studentAnalysisSubFolder = 
						new File(analysisFolder,studentFolder.getName());
				if(!studentAnalysisSubFolder.exists()){
					studentAnalysisSubFolder.mkdirs();
				}
				for (int j = 0; j < fileNames.length; j++) {
					String fileName = fileNames[j];
					File found = searchFile(studentFolder, fileName);
					if(found != null){
						//copia normalmente para a pasta do aluno 
						//usar Files.copy
						String matricula = getStudentAnalysisFolderName(found);
						if(matricula != null){
							File studentFileOut = new File(studentAnalysisSubFolder,found.getName());
							Files.copy(found.toPath(), studentFileOut.toPath(), StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}
		}
		return analysisFolder;
	}
	
	/**
	 * O nome do arquivo file tem o padrao $/matricula-NOME/$ onde matrcula
	 * eh um numero de 9 digitos. depois vem o menos e depois o nome
	 * 
	 * @param file
	 * @return
	 */
	protected static String getStudentAnalysisFolderName(File file){
		String matricula = null;
		Pattern padrao = Pattern.compile("(.*)([0-9]{9}-)(.*)");
		Matcher matcher = padrao.matcher(file.getAbsolutePath());
		if(matcher.matches()){
			int start = matcher.start(2);
			int end = matcher.end(2);
			matricula = file.getAbsolutePath().substring(start, end-1);
		}
		
		return matricula;
	}
	/**
	 * Busca um arquivo em uma pasta recursivamente e retorna o arquivo ou null.
	 * 
	 * @param folder
	 * @param fileName
	 * @return
	 */
	private File searchFile(File folder, String fileName){
		File found = null;
		if(folder.isDirectory()){
			//pega todos os arquivos normais e tenta ver se fileName esta la
			File[] realFiles = folder.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile() && pathname.getName().endsWith(fileName);
				}
			});
			
			if(realFiles.length == 1){
				found = realFiles[0];
			}else{ //nao esta na pasta atual e deve buscar nas subpastas
				//pega todos as subpastas e tenta ver se fileName esta la recursivamente
				File[] subFolders = folder.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						return pathname.isDirectory();
					}
				});
				if(subFolders.length > 0){
					for (int i = 0; i < subFolders.length; i++) {
						found = searchFile(subFolders[i], fileName);
						if(found != null){
							break;
						}
					}
				}
			}
		}
		return found;
	}
	
	private ArrayList<SimilarityAnalysisResult> processResults(ArrayList results){
		ArrayList<SimilarityAnalysisResult> analysisResults = 
				new ArrayList<SimilarityAnalysisResult>();
		if(results.size() > 0){
			results.forEach(sdr -> {
				SubmissionDetectionResult result = (SubmissionDetectionResult)sdr;
				Collection fileResults = result.getFileDetectionResults();
				fileResults.forEach(o -> {
					if (o instanceof CachingDetectionResult){
						CachingDetectionResult r = (CachingDetectionResult)o;
						try {
							SimilarityAnalysisResult newResult
							 = new SimilarityAnalysisResult(r.getFileA(),r.getFileB(),Math.max(r.getSimilarityA(), r.getSimilarityB()));
							analysisResults.add(newResult);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				});
				
			});
			
		}
		
		return analysisResults;
	}
	
	public static void main(String[] args) throws Exception {
		String[] fileNames = {"SimultaneousBubblesort.java","InsertionSort.java","BubbleSort.java","SelectionSort.java"};
		File atividadeFolder = new File("D:\\trash2\\leda-upload\\2017.1\\R01-01");
		PlagRunner pr = new PlagRunner(atividadeFolder);
		ArrayList<SimilarityAnalysisResult> results = pr.runPlagiarismAnalysis("subs", fileNames);
		int i = 1;
		for (SimilarityAnalysisResult r : results) {
			System.out.println(i++ + " File: " + r.getFileStudent1().getName() + ": (" + r.getMatriculaStudent1() + "," + r.getMatriculaStudent2() + ") with similariry " + r.getSimilarity());
		}
	}
}
