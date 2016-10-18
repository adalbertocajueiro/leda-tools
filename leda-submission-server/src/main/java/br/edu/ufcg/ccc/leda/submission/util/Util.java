
package br.edu.ufcg.ccc.leda.submission.util;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxParam;
import com.google.common.reflect.TypeToken;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CustomElementCollection;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.ServiceException;
import com.google.gson.Gson;

import br.edu.ufcg.ccc.leda.util.Compactor;
import jxl.read.biff.BiffException;

public class Util {

	private static final int BUFFER_SIZE = 4096;
	
	/**
	 * dataHora precisa ter o formato DD/MM/YYYY HH:MM:SS
	 * esse formato pode ser informado na formatacao da celula do excel de forma que
	 * o valor da calula pode ser extraido como date.
	 * @param dataHora
	 * @return
	 * @throws WrongDateHourFormatException 
	 */
	public static GregorianCalendar buildDate(Date dataHora){
		GregorianCalendar result = null;
		
		if(dataHora != null){
			result = new GregorianCalendar();
			result.setTime(dataHora);
		}
			
		return result;
	}
	
	public static String generateFileName(File file, ProfessorUploadConfiguration config){
		String result = config.getId();
		
		result = result + "-" + removeTempInfoFromFileName(file.getName());
		
		return result;
	}
	
	
	public static Comparator<String> comparatorProvas(){
		Comparator<String> comparator = 
				(name1,name2)-> {
					int result = 0;
					Pattern patternProvaPratica = Pattern.compile("PP[1-3]-[0-9][0-9[X]]");
					Pattern patternProvaReposicao = Pattern.compile("PR[1-3]-[0-9][0-9[X]]");
					Pattern patternProvaFinal = Pattern.compile("PF[1-3]-[0-9][0-9[X]]");
					Pattern patternRoteiro = Pattern.compile("R[0-9]{2}-[0-9][0-9[X]]");
					if (patternRoteiro.matcher(name1).matches()){
						if(patternRoteiro.matcher(name2).matches()){
							result = name1.compareTo(name2);
						}else{
							result = -1;
						}
					} else{
						if(patternProvaPratica.matcher(name1).matches()){
							if(patternProvaPratica.matcher(name2).matches()){
								result = name1.compareTo(name2);
							}else if (patternProvaReposicao.matcher(name2).matches()){
								result = -1;
							} else if (patternProvaFinal.matcher(name2).matches()){
								result = -2;
							} else if (patternRoteiro.matcher(name2).matches()){
								result = 3;
							}
						} else if (patternProvaReposicao.matcher(name1).matches()){
							if(patternProvaReposicao.matcher(name2).matches()){
								result = name1.compareTo(name2);
							}else if (patternProvaPratica.matcher(name2).matches()){
								result = 1;
							} else if (patternProvaFinal.matcher(name2).matches()){
								result = -1;
							} else if (patternRoteiro.matcher(name2).matches()){
								result = 3;
							}
						} else if (patternProvaFinal.matcher(name1).matches()){
							if(patternProvaFinal.matcher(name2).matches()){
								result = name1.compareTo(name2);
							}else if (patternProvaPratica.matcher(name2).matches()){
								result = 2;
							} else if (patternProvaReposicao.matcher(name2).matches()){
								result = 1;
							} else if (patternRoteiro.matcher(name2).matches()){
								result = 3;
							}
						}
					}
					
					
					return result;
				};
		return comparator;
	}
	
	private static String removeTempInfoFromFileName(String fileName){
		String result = fileName;
		int indexDot = fileName.indexOf(".");
		if(indexDot != -1){
			result = fileName.substring(indexDot + 1);
		}
		
		return result;
	}
	public static void writeRoteirosToJson(Map<String,Roteiro> roteiros, File jsonFile) throws ConfigurationException, IOException{
		Gson gson = new Gson();

		FileWriter fw = new FileWriter(jsonFile);
		gson.toJson(roteiros, fw);
		fw.flush();
		fw.close();
	}
	
	public static void writeProvasToJson(Map<String,Prova> provas, File jsonFile) throws ConfigurationException, IOException{
		Gson gson = new Gson();

		FileWriter fw = new FileWriter(jsonFile);
		gson.toJson(provas, fw);
		fw.flush();
		fw.close();
	}
	
	public static void writeFilesOwnersToJson(Map<String,String> filesOwners,File jsonFile) throws IOException{
		Gson gson = new Gson();

		FileWriter fw = new FileWriter(jsonFile);
		gson.toJson(filesOwners, fw);
		fw.flush();
		fw.close();
	}
	
	@Deprecated
	public static Map<String, Roteiro> loadRoteirosFromJson(File jsonFile) throws ConfigurationException, IOException{
		Gson gson = new Gson();
		FileReader fr = new FileReader(jsonFile);
		Map<String, Roteiro> map = gson.fromJson(fr, new TypeToken<Map<String,Roteiro>>(){}.getType());
		return map;
	}
	
	public static Map<String, String> loadFilesOwnersFromJson(File jsonFile) throws FileNotFoundException {
		Gson gson = new Gson();
		FileReader fr = new FileReader(jsonFile);
		Map<String, String> map = gson.fromJson(fr, new TypeToken<Map<String,String>>(){}.getType());
		return map;
	}
	
	public static String formatDate(GregorianCalendar date){
		String result = date.getTime().toString();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		result = formatter.format(date.getTime());
		//result = result.replace('T', ' ');
		//result = result.substring(0,19);
		
		return result;
	}
	
	public static String formatDate(long time){
		Date date = new Date();
		date.setTime(time);
		String result = date.toString();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		result = formatter.format(date.getTime());
		//result = result.replace('T', ' ');
		//result = result.substring(0,19);
		
		return result;
	}
	
	public static Properties loadProperties() throws IOException{
		Properties p = new Properties();
		File confFolder = new File(Constants.DEFAULT_CONFIG_FOLDER_NAME);
		FileReader fr = new FileReader(new File(confFolder,"app.properties"));
		p.load(fr);
		
		return p;
	}
	
	public static GregorianCalendar buildDate(String dataHora) throws WrongDateHourFormatException{
		GregorianCalendar result = null;
		if(dataHora != null){
			result = new GregorianCalendar();
			//se estiver no formato errado retorna uma excecao
			//tem que fazer isso com string format provavelmente ou regex
			if(!Constants.PATTERN_DATE_TIME.matcher(dataHora).matches()){
				throw new WrongDateHourFormatException("Date " + dataHora + " does not respect the format DD/MM/YYYY HH:MM:SS");
			}
			result.set(Calendar.DATE, Integer.parseInt(dataHora.substring(0, 2)));
			result.set(Calendar.MONTH, Integer.parseInt(dataHora.substring(3,5)) - 1); //janeiro eh considerado mes 0
			result.set(Calendar.YEAR, Integer.parseInt(dataHora.substring(6, 10)));
			result.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dataHora.substring(11, 13)));
			result.set(Calendar.MINUTE, Integer.parseInt(dataHora.substring(14, 16)));
			result.set(Calendar.SECOND, Integer.parseInt(dataHora.substring(17)));
		} 
		return result;
	}

	public static void unzip(File correctionZipFile) throws IOException {
		File destDir = correctionZipFile.getParentFile();
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(correctionZipFile));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            
        	String filePath = destDir.getAbsolutePath() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
            	//String submittedFileName = getPureFileName(entry);
            	//String submittedFilePath = getPathFileName(entry);
            	//File fileParentDir = new File(destDir.getAbsolutePath() + File.separator + submittedFilePath);
            	//this.filesFolders.put(submittedFileName, submittedFilePath);
            	
           		extractFile(zipIn, filePath);
            } 
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
	
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
    	
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
	
	public static List<Monitor> loadSpreadsheetMonitor(String idGoogleDrive) throws IOException, ServiceException{
		ArrayList<Monitor> monitores = new ArrayList<Monitor>();
		
		SpreadsheetService service = new SpreadsheetService("Sheet1");
        
        String sheetUrl =
            "https://spreadsheets.google.com/feeds/list/" + idGoogleDrive + "/default/public/values";

        // Use this String as url
        URL url = new URL(sheetUrl);

        // Get Feed of Spreadsheet url
        ListFeed lf = service.getFeed(url, ListFeed.class);

        //Iterate over feed to get cell value
        for (ListEntry le : lf.getEntries()) {
            CustomElementCollection cec = le.getCustomElements();
            //Pass column name to access it's cell values
            String matricula = cec.getValue("Matricula".toLowerCase());
            String nome = cec.getValue("Nome".toLowerCase());
            //System.out.println(val);
            String email = cec.getValue("Email".toLowerCase());
            String fone = cec.getValue("Fone".toLowerCase());
            
            Monitor monitor = 
            		new Monitor(matricula, nome, email, fone);
            
            monitores.add(monitor);
        }
        
		return monitores;
	}
	
	public static Map<String,Atividade> loadSpreadsheetAtividades(String idGoogleDrive, List<Monitor> monitores) throws WrongDateHourFormatException, IOException, ServiceException, ConfigurationException{
		Map<String,Atividade> atividades = new HashMap<String,Atividade>();
		SpreadsheetService service = new SpreadsheetService("Sheet1");
        
            String sheetUrl =
                "https://spreadsheets.google.com/feeds/list/" + idGoogleDrive + "/default/public/values";

            // Use this String as url
            URL url = new URL(sheetUrl);

            // Get Feed of Spreadsheet url
            ListFeed lf = service.getFeed(url, ListFeed.class);

            //Iterate over feed to get cell value
            for (ListEntry le : lf.getEntries()) {
                CustomElementCollection cec = le.getCustomElements();
                //Pass column name to access it's cell values
                String id = cec.getValue("Id".toLowerCase());
                String nome = cec.getValue("Nome".toLowerCase());
                //System.out.println(val);
                String descricao = cec.getValue("Descrição".toLowerCase());
                //System.out.println(val2);
                GregorianCalendar dataHoraLiberacao = Util.buildDate(cec.getValue("Data-HoraLiberação".toLowerCase()));
                //System.out.println(val3);
                GregorianCalendar dataHoraEnvioNormal = Util.buildDate(cec.getValue("Data-HoraLimiteEnvioNormal".toLowerCase()));
                //System.out.println(val4);
                GregorianCalendar dataHoraLimiteEnvioAtraso = Util.buildDate(cec.getValue("Data-HoraLimiteEnvioAtraso".toLowerCase()));
                //System.out.println(val5);
                
                String nomesMonitores = cec.getValue("Monitores".toLowerCase());
                List<Monitor> monitoresDoRoteiro = listOfMonitores(nomesMonitores,monitores);
                
                String nomeMonitor = cec.getValue("Corretor".toLowerCase());
                Monitor corretor = 
                		monitores.stream().filter(m -> m.getNome().equals(nomeMonitor)).findAny().orElse(null);
                
                //System.out.println(val6);
                GregorianCalendar dataHoraInicioCorrecao = Util.buildDate(cec.getValue("DataInicioCorrecao".toLowerCase()));
                //System.out.println(val7);
                GregorianCalendar dataHoraEntregaCorrecao = Util.buildDate(cec.getValue("DataEntregaCorrecao".toLowerCase()));
                //System.out.println(val8);
                String links = cec.getValue("LinksVideoAulas".toLowerCase());
                List<URL> linksVideoAulas = listOfLinks(links);
                
                Atividade atividade = createAtividade(id,nome,descricao,dataHoraLiberacao, linksVideoAulas,
                		dataHoraEnvioNormal,dataHoraLimiteEnvioAtraso,monitores,
                		corretor,dataHoraInicioCorrecao,dataHoraEntregaCorrecao);
                
                atividades.put(atividade.getId(), atividade);
            }
            
    		//sobrescreve os dados lidos da spreadsheet (apenas os arquivos de ambiente e correcao coletados nas pastas)
    		Util.loadAtividadesFromUploadFolder(atividades);
    		
        return atividades;
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

	
	@Deprecated
	public static Map<String,Atividade> loadSpreadsheetAtividadeFromExcel(List<Monitor> monitores) throws IOException{
		Map<String,Atividade> atividades = new HashMap<String,Atividade>();
		
		File excelFile = new File(Constants.DEFAULT_CONFIG_FOLDER,"Cronograma T1.xlsx");
		FileInputStream fis = new FileInputStream(excelFile);
		
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
		while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator(); 
            if(cellIterator.hasNext()){
            	org.apache.poi.ss.usermodel.Cell cellId = row.getCell(0);
            	if(cellId != null){
            		String id = cellId.getStringCellValue(); //

            		if(!id.equals("Id")){ //pode trabalhar aqui com o matcher
            			org.apache.poi.ss.usermodel.Cell cellNome = row.getCell(1); //celula com o nome
            			org.apache.poi.ss.usermodel.Cell cellDescricao = row.getCell(2); //celula com a descricao
            			org.apache.poi.ss.usermodel.Cell cellDataHoraLiberacao = row.getCell(3); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellDataHoraEnvioNormal = row.getCell(4); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellDataHoraLimiteEnvioAtraso = row.getCell(5); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellNomesMonitores = row.getCell(6); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellNomeMonitorCorretor = row.getCell(7); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellDataInicioCorrecao = row.getCell(8); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellDataEntregaCorrecao = row.getCell(9); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellLinksVideoAulas = row.getCell(10); //celula de datahora de liberacao

                        String nome = cellNome != null?cellNome.getStringCellValue():"";
                        String descricao = cellDescricao != cellDescricao?cellDescricao.getStringCellValue():"";
                        GregorianCalendar dataHoraLiberacao = Util.buildDate(cellDataHoraLiberacao.getDateCellValue());
                        GregorianCalendar dataHoraEnvioNormal = Util.buildDate(cellDataHoraEnvioNormal.getDateCellValue());
                        GregorianCalendar dataHoraLimiteEnvioAtraso = Util.buildDate(cellDataHoraLimiteEnvioAtraso.getDateCellValue());
                        String nomesMonitores = cellNomesMonitores != null?cellNomesMonitores.getStringCellValue():"";
                        List<Monitor> monitoresDoRoteiro = listOfMonitores(nomesMonitores,monitores);
                        
                        String nomeMonitor = cellNomeMonitorCorretor != null?cellNomeMonitorCorretor.getStringCellValue():"";
                        Monitor corretor = 
                        		monitores.stream().filter(m -> m.getNome().equals(nomeMonitor)).findAny().orElse(null);
                        
                        GregorianCalendar dataHoraInicioCorrecao = Util.buildDate(cellDataInicioCorrecao.getDateCellValue());
                        GregorianCalendar dataHoraEntregaCorrecao = Util.buildDate(cellDataEntregaCorrecao.getDateCellValue());
                        String links = cellLinksVideoAulas != null?cellLinksVideoAulas.getStringCellValue():"";
                        List<URL> linksVideoAulas = listOfLinks(links);
                        
                        Atividade atividade = createAtividade(id,nome,descricao,dataHoraLiberacao, linksVideoAulas,
                        		dataHoraEnvioNormal,dataHoraLimiteEnvioAtraso,monitores,
                        		corretor,dataHoraInicioCorrecao,dataHoraEntregaCorrecao);
                        
                        atividades.put(atividade.getId(), atividade);
            		}
            	}
            }
		}
		myWorkBook.close();

		//sobrescreve os dados lidos da spreadsheet (apenas os arquivos de ambiente e correcao coletados nas pastas)
		Util.loadAtividadesFromUploadFolder(atividades);

       
		return atividades;
	}

	@Deprecated
	public static List<Monitor> loadSpreadsheetMonitorFromExcel() throws IOException{
		ArrayList<Monitor> monitores = new ArrayList<Monitor>();
		File excelFile = new File(Constants.DEFAULT_CONFIG_FOLDER,"Monitores.xlsx");
		FileInputStream fis = new FileInputStream(excelFile);
		
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
		while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator(); 
            if(cellIterator.hasNext()){
            	org.apache.poi.ss.usermodel.Cell cellMatricula = row.getCell(0);
            	if(cellMatricula != null){
            		String matricula = null;
            		if(cellMatricula.getCellType() != XSSFCell.CELL_TYPE_STRING){
            			matricula = String.valueOf((int)cellMatricula.getNumericCellValue()); //
            		}else{
            			matricula = cellMatricula.getStringCellValue();
            		}

            		if(!matricula.equals("Matricula")){ //pode trabalhar aqui com o matcher
            			org.apache.poi.ss.usermodel.Cell cellNome = row.getCell(1); //celula com o nome
            			org.apache.poi.ss.usermodel.Cell cellEmail = row.getCell(2); //celula com a descricao
            			org.apache.poi.ss.usermodel.Cell cellFone = row.getCell(3); //celula de datahora de liberacao

            			String nome = cellNome != null?cellNome.getStringCellValue():"";
                        String email = cellEmail != null?cellEmail.getStringCellValue():"";
                        String fone = cellFone != null?cellFone.getStringCellValue():"";
                        
                        Monitor monitor = 
                        		new Monitor(matricula, nome, email, fone);
                        
                        monitores.add(monitor);
            		}
            	}
            }
		}
		myWorkBook.close();

       
		return monitores;
	}

	private static Atividade createAtividade(String id, String nome, String descricao,
			GregorianCalendar dataHoraLiberacao, List<URL> linksVideoAulas, GregorianCalendar dataHoraEnvioNormal,
			GregorianCalendar dataHoraLimiteEnvioAtraso, List<Monitor> monitores, Monitor corretor,
			GregorianCalendar dataHoraInicioCorrecao, GregorianCalendar dataHoraEntregaCorrecao) {
		
		Atividade result = null;
		if(Constants.PATTERN_AULA.matcher(id).matches()){
			result = new Aula(id,nome,descricao,dataHoraLiberacao,linksVideoAulas,monitores);			
		}else if(Constants.PATTERN_ROTEIRO_REVISAO.matcher(id).matches()){
			result = new RoteiroRevisao(id,nome,descricao,dataHoraLiberacao,
					linksVideoAulas,dataHoraEnvioNormal,dataHoraLimiteEnvioAtraso,
					monitores,corretor,dataHoraInicioCorrecao,dataHoraEntregaCorrecao,null,null);						
		}else if(Constants.PATTERN_ROTEIRO.matcher(id).matches()){
			result = new Roteiro(id,nome,descricao,dataHoraLiberacao,
					linksVideoAulas,dataHoraEnvioNormal,dataHoraLimiteEnvioAtraso,
					monitores,corretor,dataHoraInicioCorrecao,dataHoraEntregaCorrecao,null,null);						
			
		}else if(Constants.PATTERN_PROVA.matcher(id).matches()){
			result = new Prova(id,nome,descricao,dataHoraLiberacao,
					linksVideoAulas,dataHoraEnvioNormal,dataHoraLimiteEnvioAtraso,
					monitores,corretor,dataHoraInicioCorrecao,dataHoraEntregaCorrecao,null,null);						
			
		}
		return result;
	}

	public static void loadAtividadesFromUploadFolder(Map<String,Atividade> atividades){
		
			Set<String> keys = atividades.keySet();
			for (String key : keys) {
				Atividade atividade = atividades.get(key);
				File[] files = Constants.ROTEIROS_FOLDER.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						//tem arquivos cadastrado para o roteiro pelo ID
						return pathname.getName().startsWith(atividade.getId());
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
				if(atividade instanceof Roteiro){
					((Roteiro) atividade).setArquivoAmbiente(environment);
					((Roteiro) atividade).setArquivoProjetoCorrecao(correction);
				}else if(atividade instanceof Prova){
					((Roteiro) atividade).setArquivoAmbiente(environment);
					((Roteiro) atividade).setArquivoProjetoCorrecao(correction);
				}
			}
	}

	public static File compact(File folder) throws IOException{
		//id pode ser d euma prova ou de um roteiro
		File target = null;
		Compactor compactor = new Compactor();
		//File uploadFolder = new File(Constants.UPLOAD_FOLDER);
		//File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		//File folder = new File(currentSemester,id);
		if(folder.exists()){
			//target = new File(currentSemester,id + ".zip");
			target = new File(folder.getParentFile(), folder.getName() + ".zip");
			compactor.zipFolder(folder, target);
		}else{
			throw new FileNotFoundException("Missing folder: " + folder.getAbsolutePath());			
		}
		
		return target;
	}
	
	public static void removeFilesByPrefix(File folder, String prefix) throws IOException{
		File[] files = folder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().startsWith(prefix);
			}
		});
		if(files != null){
			for (int i = 0; i < files.length; i++) {
				Files.deleteIfExists(files[i].toPath());
			}
		}
	}

	@Deprecated
	public static void loadConfig(String path) throws IOException{
		NgxConfig conf = NgxConfig.read(path);
		NgxBlock app = conf.findBlock("application");
		NgxBlock port = app.findBlock("port");
		List<String> values = port.getValues();
		String p = port.getValue(); // "8889"
		NgxParam listen = port.findParam("port"); // Ex.2

		//List<NgxEntry> rtmpServers = conf.findAll(NgxConfig.BLOCK, "rtmp", "server"); // Ex.3
		//for (NgxEntry entry : rtmpServers) {
		//    ((NgxBlock)entry).getName(); // "server"
		//    ((NgxBlock)entry).findParam("application", "live"); // "on" for the first iter, "off" for the second one
		//}
	}
	
	/**
	 * Retorna a lista dos alunos que fizeram o download de um roteiro ou prova. Essa lista
	 * será utilizada para saber quem fez o download mas não submeteu a resposta.
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	public static List<String> alunosDownload(String id) throws IOException{
		ArrayList<String> alunosComDownload = new ArrayList<String>();
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		File provaUploadFolder = new File(currentSemester,id);
		File downloadLogFile = new File(provaUploadFolder, id + ".log");
		
		if(downloadLogFile.exists()){
			FileReader fr = new FileReader(downloadLogFile);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while( (line = br.readLine()) != null ){
					if(line.contains("[DOWNLOAD]")){
						int indexEstudante = line.indexOf("estudante");
						int indexDash = line.indexOf("-",indexEstudante);
						String matricula = line.substring(indexEstudante + "estudante".length(), indexDash).trim();
						alunosComDownload.add(matricula);
					}
			}
			br.close();
			fr.close();
		}
		
		return alunosComDownload;
	}
	
	public static Map<String,List<Submission>> allSubmissions() throws ConfigurationException, IOException, ServiceException{
		Map<String,List<Submission>> result = new HashMap<String,List<Submission>>();

		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		if(currentSemester.exists()){
			Pattern patternRoteiro = Pattern.compile("R[0-9]{2}-[0-9][0-9[X]]");
			Pattern patternProva = Pattern.compile("P[PRF][1-3]-[0-9][0-9[X]]");
			File[] folders = currentSemester.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File arg0) {
					return patternRoteiro.matcher(arg0.getName()).matches() 
							|| patternProva.matcher(arg0.getName()).matches();
				}
			});
			for (int i = 0; i < folders.length; i++) {
				String folderName = folders[i].getName();
				List<Submission> submissions = submissions(folders[i]);
				result.put(folderName,submissions);
			}
		}
		
		return result;
	}
	
	public static List<Submission> submissions(File atividadeFolder) throws ConfigurationException, IOException, ServiceException{
		Map<String,Student> alunos = Configuration.getInstance().getStudents();
		List<Submission> result = new ArrayList<Submission>();
	
		if(atividadeFolder.exists()){
			File submissionsFolder = new File(atividadeFolder,Constants.SUBMISSIONS_FOLDER_NAME);
			List<File> files = new ArrayList<File>();
			if(submissionsFolder.exists()){
				files = filesAsList(submissionsFolder);
			}
			String id = atividadeFolder.getName();
			alunos = alunos.values().stream()
				.filter( a -> a.getTurma().equals(id.substring(4)))
				.sorted( (a1,a2) -> a1.getNome().compareTo(a2.getNome()))
				.collect(Collectors.toMap( a -> a.getMatricula(), a -> a));
			//inserir na lista de submissoes
			alunos.values().stream()
				.forEach( a -> {
					Submission sub;
					try {
						sub = new Submission(id, a.getMatricula());
						result.add(sub);
					} catch (ConfigurationException | IOException | ServiceException | AtividadeException
							| StudentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				});
		
		}
		return result.stream().sorted( (s1,s2) -> s1.getAluno().getNome().compareTo(s2.getAluno().getNome())).collect(Collectors.toList());
		
		//return result;
	}

	/**
	 * Mostra a listagem das submissoes de prova ou roteiro ordenadas por nome
	 * do aluno. 
	 * @param folder a pasta raiz de um roteiro ou prova
	 * @return
	 */
	public static List<File> filesAsList(File folder){
		ArrayList<File> result = new ArrayList<File>();
		File[] files = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".zip");
				}
			});
		if(files != null){
			for (File file : files) {
				result.add(file);
			}
		}
		
/*		Arrays.sort(result, (f1,f2) -> {
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
*/		return result;
	}
	public static List<List<FileCopy>> listAllCopies(String id) throws ConfigurationException, IOException, ServiceException{
		
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		File folder = new File(currentSemester,id);

		return listAllCopies(folder);
	}
	
	/**
	 * 
	 * @param folderId a pasta do roteiro no qual vai ser fazer o relatorio de 
	 * todas as possiveis copias.
	 * @return
	 * @throws ConfigurationException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<List<FileCopy>> listAllCopies(File folderId) throws ConfigurationException, IOException, ServiceException{
		List<List<FileCopy>> result = new ArrayList<List<FileCopy>>();

		File submissionsFolder = new File(folderId,Constants.SUBMISSIONS_FOLDER_NAME);
		File[] jsons = submissionsFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		});
		
		for (File file : jsons) {
			result.add(listCopies(file));
		}
		
		return result;
	}

	public static List<FileCopy> listCopies(File jsonFile) throws IOException{
		List<FileCopy> result = new ArrayList<FileCopy>();
		String matriculaCopier = jsonFile.getName().substring(0,jsonFile.getName().indexOf("-"));
		String nomeCopier = jsonFile.getName().substring(jsonFile.getName().indexOf("-") + 1, jsonFile.getName().indexOf("."));
		
		Student copier = new Student(matriculaCopier,nomeCopier,null);
		
		Map<String,String> filesOwners = Util.loadFilesOwnersFromJson(jsonFile);
		filesOwners.forEach((fName,fOwner) -> {
			String matricula = fOwner.substring(0,fOwner.indexOf("-"));
			if(!matricula.equals(matriculaCopier)){
				FileCopy copy = new FileCopy(fName,fOwner,copier);
				result.add(copy);
			}
		});
		
		return result;
	}
	public static List<File> getFiles(File folder, String extension){
		ArrayList<File> files = new ArrayList<File>();
		addFile(folder, files, extension);
		return files; 
	}
	private static void addFile(File folder, List<File> files, String extension){
		if(folder.isDirectory()){
			File[] subFiles = folder.listFiles();
			for (int i = 0; i < subFiles.length; i++) {
				addFile(subFiles[i], files, extension);
			}
		} else if (folder.getName().endsWith(extension)){
			if(!files.contains(folder)){
				files.add(folder);
			}
		} 
	}

	
	public static void sendEmail(String fromAddr, String toAddr, String subject, String body, String user, String passwd)
	{
	    Properties props = new Properties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.user", user);
        props.put("mail.smtp.password", passwd);
	    Session session = Session.getDefaultInstance(props);

	    MimeMessage message = new MimeMessage(session);

	    try
	    {
	        message.setFrom(new InternetAddress(fromAddr));
	        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddr));
	        message.setSubject(subject);
	        message.setText(body);
	        //Transport transport = session.getTransport("smtp");
	        //transport.connect("smtp.gmail.com", user, passwd);
	        Transport.send(message);
	        //transport.send(message, message.getAllRecipients()); 
	        //transport.close();
            System.out.println("done");
	    }
	    catch (AddressException e) {e.printStackTrace();}
	    catch (MessagingException e) {e.printStackTrace();}
	}
	
	public static void sendTLS()
	   {
	      
		final String username = "adalberto.cajueiro@gmail.com";
		final String password = "acfcaju091401";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("adalberto.cajueiro@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("adalberto.cajueiro@gmail.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n No spam to my email, please!");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	   }
	
	public static void sendSSL()
	   {
	      
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		//props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.port", "587");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		//props.put("mail.smtp.starttls.enable", "true");
		//props.put("mail.smtp.port", "465");
		props.put("mail.smtp.port", "587");

		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("adalberto.cajueiro@gmail.com","acfcaju091401");
				}
			});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("adalberto.cajueiro@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("adalberto.cajueiro@gmail.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler," +
					"\n\n No spam to my email, please!");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	   }
	
	public static void send1(){
		final String username = "adalberto.cajueiro@gmail.com";
		final String password = "acfcaju091401";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		Session session = Session.getInstance(props,null);

		try {

		    Message message = new MimeMessage(session);
		    message.setFrom(new InternetAddress("adalberto.cajueiro@gmail.com"));
		    message.setRecipients(Message.RecipientType.TO,
		            InternetAddress.parse("adalberto.cajueiro@gmail.com"));
		    message.setSubject("Test Subject");
		    message.setText("Test");

		    Transport transport = session.getTransport("smtp");
		    String mfrom = "adalberto.cajueiro";// example laabidiraissi 
		    transport.connect("smtp.gmail.com", mfrom, "acfcaju091401");
		    transport.sendMessage(message, message.getAllRecipients());
		    //Transport.send(message);

		    System.out.println("Done");

		} catch (MessagingException e) {
		    throw new RuntimeException(e);
		}
	}
	
	public static void send2(){
		final String senderEmailID = "adalberto.cajueiro@gmail.com";
		final String senderPassword = "acfcaju091401";
		final String emailSMTPserver = "smtp.gmail.com";
		final String emailServerPort = "587";
		String receiverEmailID = "adalberto.cajueiro@gmail.com";
		String emailSubject = "Test Mail";
		String emailBody = ":)";
		Properties props = new Properties();
		props.put("mail.smtp.user",senderEmailID);
		props.put("mail.smtp.host", emailSMTPserver);
		props.put("mail.smtp.port", emailServerPort);
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", emailServerPort);
		props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		SecurityManager security = System.getSecurityManager();
		try
		{
		Authenticator auth = new SMTPAuthenticator();
		Session session = Session.getInstance(props, auth);
		MimeMessage msg = new MimeMessage(session);
		msg.setText(emailBody);
		msg.setSubject(emailSubject);
		msg.setFrom(new InternetAddress(senderEmailID));
		msg.addRecipient(Message.RecipientType.TO,
		new InternetAddress(receiverEmailID));
		Transport.send(msg);
		System.out.println("Message send Successfully:)");
		}
		catch (Exception mex)
		{
		mex.printStackTrace();
		}
	}
		
	private static List<Monitor> listOfMonitores(String listOfMonitores,List<Monitor> monitores) {
		ArrayList<Monitor> list = new ArrayList<Monitor>();
		if(listOfMonitores != null){
			for (String nome : listOfMonitores.split(",")) {
				monitores.stream().forEach(m -> {
					if(m.getNome().equals(nome)){
						list.add(m);
					}
				});
			}
		}
		
		return list;
	}
	private static List<URL> listOfLinks(String listOfLinks){
		ArrayList<URL> list = new ArrayList<URL>();
		Function<String[], List<URL>> fill = new Function<String[], List<URL>>() {
			@Override
			public List<URL> apply(String[] links) {
				
				for (String link : links) {
					try {
						URL url = new URL(link);
						list.add(url);
					} catch (MalformedURLException e) {
						//link incorreto nao adiciona na lista de links	
					}
					
				}
				return list;
			}
		};
		if(listOfLinks != null){
			fill.apply(listOfLinks.split(","));
		}
		return list;
	}
	

	public static void main(String[] args) throws ConfigurationException, IOException, WrongDateHourFormatException, ServiceException {
		//Util.sendEmail("adalberto.cajueiro@gmail.com", "adalberto.cajueiro@gmail.com", "Teste", "conteudo", "adalberto.cajueiro@gmail.com", "acfcaju091401");
		//Util.send2();
		//System.out.println("Email enviado");
		//Util.loadConfig("conf/application.conf");
		//https://docs.google.com/spreadsheets/d//pubhtml
		//Map<String,Prova> provas = Util.loadSpreadsheetProvas("1mt0HNYUMgK_tT_P2Lz5PQjBP16F6Hn-UI8P21C0iPmI");
		//provas.forEach((id,p) -> System.out.println(p));
		List<Monitor> monitores = Util.loadSpreadsheetMonitorFromExcel();
		Map<String,Atividade> atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
		int i = 0;
		
		//Map<String,Prova> provas = Util.loadSpreadsheetProvas("19npZPI7Y1jyk1jxNKHgUZkYTk3hMT_vdmHunQS-tOhA");
		//provas.forEach((id,p) -> System.out.println(p));
		//File target = Util.compact(new File("D:\\trash2\\leda-upload\\2016.1\\R01-02"));
		//System.out.println(target.getName());
		//int i = 0;
		//Util.loadRoteirosFromJson(new File("D:\\trash2\\file.json"));
		//Util.loadProperties();
		/*File folder = new File("/home/ubuntu/leda/leda-tools/leda-submission-server/public");
		boolean exists = folder.exists();
		Path newLink = (new File(folder,"report")).toPath();
		Path target = (new File("/home/ubuntu/leda-upload")).toPath();
		Runtime.getRuntime().exec("ln -s " + target + " " + newLink);
		System.out.println("link criado de " + newLink + " para " + target);*/
		//Map<String,Roteiro> roteiros = Util.loadSpreadsheet("19npZPI7Y1jyk1jxNKHgUZkYTk3hMT_vdmHunQS-tOhA");
		//roteiros.values().stream().sorted((r1,r2) -> r1.getId().compareTo(r2.getId()))
		//	.forEach(r -> System.out.println(r));
		/*Pattern pattern = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");
		Matcher matcher = pattern.matcher("49/04/1970  14:00:00");
		System.out.println(matcher.matches());
		String[] elements = pattern.split("49/04/1970  14:00:00");
		System.out.println(elements.length);
		System.out.println(Arrays.toString(elements));
		System.out.println(matcher.groupCount());
		GregorianCalendar calendar = new GregorianCalendar(); //buildDate("20/07/2016 14:00:00");
		System.out.println("ERA: " + calendar.get(Calendar.ERA));
		 System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
		 System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
		 System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
		 System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
		 System.out.println("DATE: " + calendar.get(Calendar.DATE));
		 System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
		 System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
		 System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
		 System.out.println("DAY_OF_WEEK_IN_MONTH: "
		                    + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		 System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
		 System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
		 System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
		 System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
		 System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
		 System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
		 System.out.println("ZONE_OFFSET: "
		                    + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000)));
		 System.out.println("DST_OFFSET: "
		                    + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000)));

		 System.out.println("Current Time, with hour reset to 3");
		 calendar.clear(Calendar.HOUR_OF_DAY); // so doesn't override
		 calendar.set(Calendar.HOUR, 3);
		 System.out.println("ERA: " + calendar.get(Calendar.ERA));
		 System.out.println("YEAR: " + calendar.get(Calendar.YEAR));
		 System.out.println("MONTH: " + calendar.get(Calendar.MONTH));
		 System.out.println("WEEK_OF_YEAR: " + calendar.get(Calendar.WEEK_OF_YEAR));
		 System.out.println("WEEK_OF_MONTH: " + calendar.get(Calendar.WEEK_OF_MONTH));
		 System.out.println("DATE: " + calendar.get(Calendar.DATE));
		 System.out.println("DAY_OF_MONTH: " + calendar.get(Calendar.DAY_OF_MONTH));
		 System.out.println("DAY_OF_YEAR: " + calendar.get(Calendar.DAY_OF_YEAR));
		 System.out.println("DAY_OF_WEEK: " + calendar.get(Calendar.DAY_OF_WEEK));
		 System.out.println("DAY_OF_WEEK_IN_MONTH: "
		                    + calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		 System.out.println("AM_PM: " + calendar.get(Calendar.AM_PM));
		 System.out.println("HOUR: " + calendar.get(Calendar.HOUR));
		 System.out.println("HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
		 System.out.println("MINUTE: " + calendar.get(Calendar.MINUTE));
		 System.out.println("SECOND: " + calendar.get(Calendar.SECOND));
		 System.out.println("MILLISECOND: " + calendar.get(Calendar.MILLISECOND));
		 System.out.println("ZONE_OFFSET: "
		        + (calendar.get(Calendar.ZONE_OFFSET)/(60*60*1000))); // in hours
		 System.out.println("DST_OFFSET: "
		        + (calendar.get(Calendar.DST_OFFSET)/(60*60*1000))); // in hours
*/	}

}
class SMTPAuthenticator extends javax.mail.Authenticator{
	public PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication("adalberto.cajueiro@gmail.com", "acfcaju091401");
	}
}
