package plag.runner;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import br.edu.ufcg.ccc.leda.util.Student;
import plag.parser.CachingDetectionResult;
import plag.parser.SubmissionDetectionResult;
import plag.parser.plaggie.Plaggie;

public class PlagRunner {
	
	/**
	 * Recebe a pasta de submissoes dos alunos e compara os arquivos fornecidos em 
	 * determinada lista de arquivos dada por fileNames.
	 * 
	 * @param submissionsFolder
	 * @param fileNames
	 */
	public static void plagAnalysis(File submissionsFolder,String[] fileNames, Map<String, Student> students){
		//precisa: usar uma pasta temporaria (cria uma pasta analysis na pasta da atividade)
		//criar uma pasta para cada aluno (pode ser a matricula)
		//copiar apenas os arquivos especificos informados na lista
		// para a pasta do aluno correspondente. 
		
		//depois roda a analise na pasta de analises gerada. cria uma configuracao
		//padrao ou especifica 
	}
	

	/**
	 * Cria a pasta de analise de um estudante e ja copia os arquivos pra la. 
	 *  //nome da pasta de analises pode ser uma constante global
		//cria uma subpasta "analysis" na pasta pai de submissionsFolder
		//varre as subpastas de submissionsFolder (com as submissoes dos alunos)
		//copia de cada subpasta os arquivos informados em fileNames (busca recursiva)

	 * @param studentFolder
	 * @return
	 */
	private static File createAnalysisFolder(File submissionsFolder, String[] fileNames){
		File analysisFolder = new File(submissionsFolder.getParentFile(),"analysis");
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
					}
				}
			}
		}
		return analysisFolder;
	}
	/**
	 * Busca um arquivo em uma pasta recursivamente e retorna o arquivo ou null.
	 * 
	 * @param folder
	 * @param fileName
	 * @return
	 */
	private static File searchFile(File folder, String fileName){
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
	
	public static void main(String[] args) {
		Plaggie plag = new Plaggie();
		String folder = "D:\\UFCG\\leda\\leda-tools\\leda-plaggie\\src\\main\\resources\\subs";
		String file1 = "D:\\UFCG\\leda\\leda-tools\\leda-plaggie\\target\\classes\\subs\\115210859-HECTOR HIAGO DE MEDEIROS\\src\\main\\java\\sorting\\variationsOfBubblesort\\SimultaneousBubblesort.java";
		String file2 = "D:\\UFCG\\leda\\leda-tools\\leda-plaggie\\target\\classes\\subs\\116210442-FILIPE PIRES GUIMARAES\\src\\main\\java\\sorting\\variationsOfBubblesort\\SimultaneousBubblesort.java";
		String[] args2 = {folder};
		plag.main(args2);
		ArrayList results = plag.detResults;
		ArrayList<SimilarityAnalysisResult> analysisResults = 
				new ArrayList<SimilarityAnalysisResult>();
		if(results.size() > 0){
			SubmissionDetectionResult result = (SubmissionDetectionResult)results.get(0);
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
		}
		int i = 0;
	}
}
