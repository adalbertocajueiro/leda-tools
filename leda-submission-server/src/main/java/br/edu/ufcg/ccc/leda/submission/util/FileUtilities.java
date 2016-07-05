package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.read.biff.BiffException;

public class FileUtilities {

	public static final String DEFAULT_CONFIG_FOLDER = "conf";
	public static final String EXCEL_FILE_ROTEIRO = "Roteiros.xlsx";
	public static final String JSON_FILE_ROTEIRO = "Roteiros.json";
	public static String UPLOAD_FOLDER = "/home/ubuntu/leda-upload";
	//public static String UPLOAD_FOLDER = "D:\\trash2\\leda-upload";

	public static File getEnvironment(String roteiro) throws ConfigurationException, IOException, RoteiroException{
		File environment = null;
		
		//verifica se esta sendo requisitado dentro do prazo. faz om o validator
		Validator.validateDownload(roteiro);
		
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
	 * @throws RoteiroException 
	 * @throws Exception 
	 */
	public static String saveProfessorSubmission(File ambiente, File projetoCorrecao, ProfessorUploadConfiguration config) throws StudentException, ConfigurationException, IOException, RoteiroException {
		String result = "upload nao realizado";
		// precisa verificar se o aluno que enviou esta realmente matriculado.
		Validator.validate(config);
		
		File uploadFolder = new File(FileUtilities.UPLOAD_FOLDER);
		if(!uploadFolder.exists()){
			uploadFolder.mkdirs();
		}
		
		//o nome do arquivo eh enviado. ele deve ser colocado na pasta 
		//<upload>/semestre/roteiros/<ID_ROTEIRO>NomearquivoEnviado.zip
		//dois arquivos devem ser salvos:environment e correction-proj.
		//eles devem ser inseridos no objeto roteiro que esta no Map  Configuration.roteiros
		//e deve ser salvo um arquivo JSON mantendo dota essa estrutura. 
		String uploadSubFolder = config.getSemestre() + File.separator + "roteiros";
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
		Files.move(ambiente.toPath(), foutEnv.toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.move(projetoCorrecao.toPath(), foutCorrProj.toPath(), StandardCopyOption.REPLACE_EXISTING);
		//PRECISA LOGAR OPERACOES DA APLICACAO???????
		//TODO
		
		//adicionando os arquivos no respectivo roteiro
		Map<String,Roteiro> roteiros = Configuration.getInstance().getRoteiros();
		Roteiro roteiro = roteiros.get(config.getRoteiro());
		roteiro.setArquivoAmbiente(foutEnv);
		roteiro.setArquivoProjetoCorrecao(foutCorrProj);
		roteiros.put(config.getRoteiro(), roteiro);
		
		//agora eh persistir os dados dos roteiros em JSON
		File configFolder = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
		if(!configFolder.exists()){
			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		}
		File jsonFileRoteiros = new File(configFolder,JSON_FILE_ROTEIRO);
		Util.writeRoteirosToJson(roteiros, jsonFileRoteiros);
		
		result = "Uploads realizados: " + foutEnv.getAbsolutePath() + ", " + foutCorrProj.getAbsolutePath(); 
		
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
	 * @throws RoteiroException 
	 * @throws Exception 
	 */
	public static String saveStudentSubmission(File uploaded, StudentUploadConfiguration config) throws StudentException, ConfigurationException, IOException, RoteiroException {
		String result = null;
		// precisa verificar se o aluno que enviou esta realmente matriculado.
		Validator.validate(config);
		Map<String,Student> students = Configuration.getInstance().getStudents();
		Student student = students.get(config.getMatricula());
		
		// precisa criar as pastas onde o arquivo vai ser uploaded. as pastas sao criadas 
		//na past default de uploads e seguem o padrao: <default>/semestre/roteiro/turma
		//nela sao colocadas as submissoes dos alunos e feito um log da submissao
		File uploadFolder = new File(FileUtilities.UPLOAD_FOLDER);
		if(!uploadFolder.exists()){
			uploadFolder.mkdirs();
		}
		
		//o nome do arquivo eh o nome do aluno cadastrado no sistema
		//String uploadFileName = config.getSemestre() + File.separator + config.getRoteiro() 
		//		+ File.separator + config.getTurma() + File.separator + uploaded.getName().substring(uploaded.getName().indexOf(".") + 1);
		String uploadSubFolder = config.getSemestre() + File.separator + config.getRoteiro() 
				+ File.separator + config.getTurma();
		String uploadFileName =  uploadSubFolder + File.separator + student.getNome() + ".zip";

		File fout = new File(uploadFolder,uploadFileName);
		if (!fout.exists()) {
			fout.mkdirs();
		}
		Files.move(uploaded.toPath(), fout.toPath(), StandardCopyOption.REPLACE_EXISTING);
		//PRECISA LOGAR OPERACOES DA APLICACAO???????
		//TODO
		
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
		File configFolder = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
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
	
	protected static void loadStudentsFromExcelFile(File excelFile, Map<String,Student> map) throws IOException, BiffException{
		if(excelFile.getName().endsWith(".xlsx")){
			loadStudentsFromXLSX(excelFile, map);
		} else if(excelFile.getName().endsWith(".xls")){
			loadStudentsFromXLS(excelFile, map);
		}
	}
	
	public static Map<String, Roteiro> loadRoteiros() throws IOException, ConfigurationException{
		Map<String, Roteiro> result = new HashMap<String, Roteiro>();
		File configFolder = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
		if(!configFolder.exists()){
			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		}
		//tenta ler primeiro do json
		File jsonFileRoteiros = new File(configFolder,JSON_FILE_ROTEIRO);
		Map<String,Roteiro> roteirosFromJson = new HashMap<String,Roteiro>();
		
		if(jsonFileRoteiros.exists()){
			roteirosFromJson = Util.loadRoteirosFromJson(jsonFileRoteiros);
		}
		
		//carrega os roteiros de acordo com o arquivo excel
		File excelFileRoteiro = new File(configFolder,EXCEL_FILE_ROTEIRO);
		loadRoteirosFromExcelFile(excelFileRoteiro, result);
		
		//sobrescreve os dados lidos do excel com os do json apenas os arquivos de ambiente e correcao
		reuseFiles(result,roteirosFromJson);
		
		//com o reuso pode ter acontecido de alguma data ter sido modificada. entao salvamos novamente no json
		Util.writeRoteirosToJson(result, jsonFileRoteiros);
		
		return result;
	}
	private static void reuseFiles(Map<String,Roteiro> mapExcel, Map<String,Roteiro> mapJson){
		Set<String> keys = mapJson.keySet();
		for (String key : keys) {
			Roteiro roteiroJson = mapJson.get(key);
			Roteiro roteiroExcel = mapExcel.get(key);
			roteiroExcel.setArquivoAmbiente(roteiroJson.getArquivoAmbiente());
			roteiroExcel.setArquivoProjetoCorrecao(roteiroJson.getArquivoProjetoCorrecao());
			mapExcel.put(key, roteiroExcel);
		}
	} 
	private static void loadRoteirosFromExcelFile(File xlsxFile, Map<String,Roteiro> roteiros) throws IOException{
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
            		if(idRoteiro.length() == 6){
            			org.apache.poi.ss.usermodel.Cell cellDescricao = row.getCell(1); //celula com a descricao
            			org.apache.poi.ss.usermodel.Cell cellDataLiberacao = row.getCell(2); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteEnvioNormal = row.getCell(3); //celula de datahora limite de envio normal
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteEnvioAtraso = row.getCell(4); //celula de datahora limite de envio com atraso
            			org.apache.poi.ss.usermodel.Cell cellNomeMonitor = row.getCell(5); //celula de nome do monitor
            			org.apache.poi.ss.usermodel.Cell cellDataInicioCorrecao = row.getCell(6); //celula de datahora de atricuicao ao monitor
            			org.apache.poi.ss.usermodel.Cell cellDataLimiteCorrecao = row.getCell(7); //celula de datahora limite da correcao
            			
            			GregorianCalendar dataHoraLiberacao = Util.buildDate(cellDataLiberacao.getDateCellValue());
            			cellDataLimiteEnvioNormal = evaluator.evaluateInCell(cellDataLimiteEnvioNormal);
            			GregorianCalendar dataHoraLimiteEnvioNormal = Util.buildDate(cellDataLimiteEnvioNormal.getDateCellValue());
            			cellDataLimiteEnvioAtraso = evaluator.evaluateInCell(cellDataLimiteEnvioAtraso);
            			GregorianCalendar dataHoraLimiteEnvioAtraso = Util.buildDate(cellDataLimiteEnvioAtraso.getDateCellValue());
            			cellDataInicioCorrecao = evaluator.evaluateInCell(cellDataInicioCorrecao);
            			GregorianCalendar dataInicioCorrecao = Util.buildDate(cellDataInicioCorrecao.getDateCellValue());
            			cellDataLimiteCorrecao = evaluator.evaluateInCell(cellDataLimiteCorrecao);
            			GregorianCalendar dataLimiteCorrecao = Util.buildDate(cellDataLimiteCorrecao.getDateCellValue());
            			
            			Roteiro roteiro = new Roteiro(idRoteiro,cellDescricao.getStringCellValue(),
            					dataHoraLiberacao,dataHoraLimiteEnvioNormal,dataHoraLimiteEnvioAtraso,
            					cellNomeMonitor.getStringCellValue(),dataInicioCorrecao,dataLimiteCorrecao, null,null);
            			roteiros.put(idRoteiro, roteiro);
            		}
            	}
            }
		}
		myWorkBook.close();
	}
	
	private static void loadStudentsFromXLS(File xlsFile, Map<String, Student> map)
			throws BiffException, IOException {
		// le de um arquivo e coloca no map
		Workbook workbook = Workbook.getWorkbook(xlsFile);
		Sheet sheet = workbook.getSheet(0);
		String turma = extractTurmaFromExcelFile(xlsFile);
		if(turma.equals("0")){
			throw new RuntimeException("Turma invalida!");
		}
		int linhas = sheet.getRows();
		for (int i = 0; i < linhas; i++) {
			Cell a1 = sheet.getCell(0, i);
			Cell a2 = sheet.getCell(1, i);
			Cell a3 = sheet.getCell(2, i);
			if (!(a1 instanceof EmptyCell)) {
				Student student = new Student(a2.getContents(), a3.getContents(), turma);
				map.put(student.getMatricula(), student);
			}
		}
		workbook.close();
	}
	

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
}
