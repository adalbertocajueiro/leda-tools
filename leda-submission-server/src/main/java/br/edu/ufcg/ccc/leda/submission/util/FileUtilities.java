package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	public static String BASE_UPLOAD_FOLDER;

	/**
	 * Salva um arquivo recebido em upload numa pasta especifica e com o nome do
	 * aluno, recuperado de um mapeamento (arquivo excel do controle academico).
	 * 
	 * @return
	 */
	public File saveUpload(File uploaded, UploadConfiguration config) {
		File result = null;
		// precisa verificar se o aluno que enviou esta realmente matriculado.
		
		// precisa criar as pastas onde o arquivo vai ser uploaded
		
		// precisa fazer um historico das submissoes de cada estudante para cada
		// roteiro.
		// quando acontece uma submissao, o sistema retorna um ticket com o
		// historico de todas as submissoes
		// do aluno
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
	 * @throws IOException 
	 * @throws BiffException 
	 */
	private Map<String, Student> loadStudentLists() throws BiffException, IOException {
		Map<String, Student> result = new HashMap<String, Student>();
		File configFolder = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
		if(!configFolder.exists()){
			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		}
		File[] excelFiles = configFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getAbsolutePath().endsWith(".xls") || pathname.getAbsolutePath().endsWith(".xlsx");
			}
		});
		
		for (int i = 0; i < excelFiles.length; i++) {
			result = loadStudentsFromExcelFile(excelFiles[i]);
		}
		return result;
	}

	private String extractTurmaFromExcelFile(File excelFile){
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
	
	protected Map<String,Student> loadStudentsFromExcelFile(File excelFile) throws IOException, BiffException{
		HashMap<String,Student> map = new HashMap<String, Student>();
		if(excelFile.getName().endsWith(".xlsx")){
			loadStudentsFromXLSX(excelFile, map);
		} else if(excelFile.getName().endsWith(".xls")){
			loadStudentsFromXLS(excelFile, map);
		}
		return map;
	}
	private void loadStudentsFromXLS(File xlsFile, Map<String, Student> map)
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
	

	private void loadStudentsFromXLSX(File xlsxFile,Map<String, Student> map) throws IOException {
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
