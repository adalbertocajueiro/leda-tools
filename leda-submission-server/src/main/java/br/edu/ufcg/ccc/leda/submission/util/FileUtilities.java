package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.read.biff.BiffException;

public class FileUtilities {

	public static final String DEFAULT_CONFIG_FOLDER = "conf";
	public static String UPLOAD_FOLDER = "upload";
	private static Validator validator;
	
	
	public FileUtilities() {
		validator = new Validator();
	}

	/**
	 * Salva um arquivo recebido em upload numa pasta especifica e com o nome do
	 * aluno, recuperado de um mapeamento (arquivo excel do controle academico).
	 * 
	 * @return
	 * @throws StudentException 
	 * @throws ConfigurationException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static String saveUpload(File uploaded, UploadConfiguration config) throws StudentException, ConfigurationException, IOException {
		String result = null;
		// precisa verificar se o aluno que enviou esta realmente matriculado.
		if(!Validator.validateStudent(config)){
			throw new StudentException();
		}
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
	public static Map<String, Student> loadStudentLists() throws ConfigurationException, IOException {
		Map<String, Student> result = new HashMap<String, Student>();
		File configFolder = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
		if(!configFolder.exists()){
			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		}
		//System.out.println("%%%%%%CONFIG FOLDER: " + configFolder.getAbsolutePath());
		File[] excelFiles = configFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().endsWith(".xls") || pathname.getAbsolutePath().endsWith(".xlsx");
			}
		});
		//System.out.println("%%%%%%%%% EXCEL FILES: " + excelFiles.length);
		for (int i = 0; i < excelFiles.length; i++) {
			try {
				loadStudentsFromExcelFile(excelFiles[i],result);
			} catch (BiffException e) {
				throw new ConfigurationException("",e);
			}
		}
		return result;
	}

	private static String extractTurmaFromExcelFile(File excelFile){
		String turma = "0";
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
