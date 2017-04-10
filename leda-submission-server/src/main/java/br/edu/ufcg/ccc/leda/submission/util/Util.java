
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
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.function.Predicate;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom2.JDOMException;

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
import br.edu.ufcg.ccc.leda.util.CorrectionClassification;
import br.edu.ufcg.ccc.leda.util.CorrectionReport;
import br.edu.ufcg.ccc.leda.util.CorrectionReportItem;
import br.edu.ufcg.ccc.leda.util.TestReport;
import br.edu.ufcg.ccc.leda.util.Utilities;
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
					Pattern patternProvaPratica = Pattern.compile("PP[1-9]-[0-9][0-9[X]]");
					Pattern patternProvaReposicao = Pattern.compile("PR[1-9]-[0-9][0-9[X]]");
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
	
	public static SimilarityMatrix buildSimilarityMatrix(String id) throws IOException, JDOMException{
		SimilarityMatrix result = null;
		File atividadeFolder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		result = ClusteringUtil.buildSimilarityMatrix(atividadeFolder);
		
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
	
	public static TestReport loadTestReport(String id) throws IOException{
		TestReport result = null;
		File atividadeFolder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		if(atividadeFolder.exists()){
			File jsonReport = new File(atividadeFolder,id + "-report.json");
			if(jsonReport.exists()){
				result = Utilities.loadTestReportFromJson(jsonReport);
			}
		}
		return result;
	}
	
	public static CorrectionReport loadCorrectionReport(String id) throws IOException{
		CorrectionReport result = null;
		File atividadeFolder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		if(atividadeFolder.exists()){
			File correctionReport = new File(atividadeFolder,id + "-correction.json");
			if(correctionReport.exists()){
				result = Utilities.loadCorrectionReportFromJson(correctionReport);
			}
		}
		return result;
	}
	
	public static Map<String,CorrectionReport> loadCorrectionReports(Predicate<String> patternValidator) throws IOException{
		Map<String,CorrectionReport> result = new TreeMap<String,CorrectionReport>((cr1,cr2) -> {
        	int res = 1;
        	if(cr1.charAt(0) != cr2.charAt(0)){
        		res = cr2.charAt(0) -  cr1.charAt(0);
        	} else{
        		res = cr1.compareTo(cr2);
        	}
        	return res;
        
		});
		
		File[] atividadesFiltradas = Constants.CURRENT_SEMESTER_FOLDER.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return patternValidator.test(pathname.getName());
			}
		});
		for (int i = 0; i < atividadesFiltradas.length; i++) {
			CorrectionReport report = Util.loadCorrectionReport(atividadesFiltradas[i].getName());
			if(report != null){
				result.put(atividadesFiltradas[i].getName(), report);
			}
		}
		return result;
	}
	
	//retorna as notas das finais de todos os alunos
	public static Map<String, Double> getNotasDaFinal() throws IOException, ConfigurationException, ServiceException {
		Map<String, Double> notasDaFinal = new HashMap<String, Double>();
		Map<String, Student> alunos = Configuration.getInstance().getStudents();

		// vem com as notas finais de todas as turmas
		Map<String, CorrectionReport> relatoriosDaFinal = Util.loadCorrectionReports(new Predicate<String>() {
			// predicado para filtrar as provas finais
			@Override
			public boolean test(String t) {
				return Constants.PATTERN_PROVA_FINAL.matcher(t).matches();
			}
		});
		if (relatoriosDaFinal.size() != 0) {
			CorrectionReport reportFinais = relatoriosDaFinal.values().stream().findFirst().get();
			alunos.forEach((mat, aluno) -> {
				CorrectionReportItem item = reportFinais.getCorrectionReportItemforStudent(mat);
				if(item != null){
					double nota = item.getNotaTestes() + item.getNotaDesign() * 0.6;
					notasDaFinal.put(mat, nota);
				}else{
					double nota = 0.0;
					notasDaFinal.put(mat, nota);					
				}
			});

		} else {
			alunos.forEach((mat, aluno) -> {
				notasDaFinal.put(mat, 0.0);
			});
		}

		return notasDaFinal;
	}

	//retorna as medias com final sem filtrar por turma
	public static Map<String,Double> buildMediasLEDAComFinal() throws IOException, ConfigurationException, ServiceException{
		Map<String,Double> mediasLEDAComFinal = new HashMap<String,Double>();
		Map<String,Double> mediasLEDASemfinal = Util.buildMediasLEDASemFinal();
		Map<String, Student> students = Configuration.getInstance().getStudents();
		//vem com as notas finais de todas as turmas
		Map<String,CorrectionReport> relatorioDaFinal = Util.loadCorrectionReports( new Predicate<String>() {
			//predicado para filtrar as provas finais
			@Override
			public boolean test(String t) {
				return Constants.PATTERN_PROVA_FINAL.matcher(t).matches();
			}
		});
		if(relatorioDaFinal.size() == 1){
			CorrectionReport reportFinais = relatorioDaFinal.values().stream().findFirst().get();
			mediasLEDASemfinal.forEach( (mat,med) -> {
				CorrectionReportItem item = reportFinais.getCorrectionReportItemforStudent(mat);
				double nf = 0.0;
				double notaComFinal = med;

				if(item != null){
					nf = item.getNotaTestes() + item.getNotaDesign()*0.6;
					notaComFinal = med*0.6 + nf*0.4;
					
					if( med >= 7.0){ //para os alunos que nao precisavam ir pra final
						notaComFinal = med;
					}
				}else{
					//Student aluno = students.get(mat);
					//System.out.println("Aluno sem nota final: " + mat + "-" + aluno.getNome() +"-Turma " + aluno.getTurma());
					if( med >= 7.0){ //para os alunos que nao precisavam ir pra final
						notaComFinal = med;
					}
				}
				mediasLEDAComFinal.put(mat,notaComFinal);
			});
			
		}else{
			mediasLEDASemfinal.forEach( (mat,med) -> {
				mediasLEDAComFinal.put(mat,med);
			});
		}
		return mediasLEDAComFinal;
	}
	
	public static Map<String,Double> buildMediasLEDASemFinal() throws IOException, ConfigurationException, ServiceException{
		Map<String,Double> mediasSemFinal = new HashMap<String,Double>();
		Map<String,Double> mediasProvasTeoricas = Util.loadSpreadsheetsMediasEDA();
		Map<String,Double> mediasProvasPraticas = Util.buildMediasProvasPraticas();
		Map<String,Double> mediasRoteiros = Util.buildMediasRoteiros();
		
		//media roteiros = 0.25
		//media prova teorica = 0.25 (secursar EDA)
		//media prova pratica = 0.5 (se cursar EDA) e 0.75 (se nao cursar EDA)
		mediasProvasPraticas.forEach((mat,med) -> {
			double mpp = med;
			double mr = mediasRoteiros.get(mat);
			double mediaSemFinal = mpp*0.75 + mr*0.25;
			Double mpt = mediasProvasTeoricas.get(mat);
			if(mpt != null){
				mediaSemFinal = mpp*0.5 + mr*0.25 + mpt*0.25;
			}
			mediasSemFinal.put(mat, mediaSemFinal);
		});
		return mediasSemFinal;
	}
	
	public static Map<String,Double> buildMediasProvasPraticas() throws IOException, ConfigurationException, ServiceException{
		Map<String,Double> mediasProvasPraticas = new HashMap<String,Double>();
		Map<String,CorrectionReport> correctionReports = Util.loadCorrectionReports(new Predicate<String>() {
			//predicado para filtrar o que mostrar nas notas (provas e roteiros
			@Override
			public boolean test(String t) {
				return Constants.PATTERN_PROVA_PRATICA.matcher(t).matches()
						|| Constants.PATTERN_PROVA_REPOSICAO.matcher(t).matches();
			}
		});
		Map<String,Student> alunos = Configuration.getInstance().getStudents();
		alunos.keySet().forEach( m -> {
			double somatorioNotas = correctionReports.values().stream().mapToDouble( cr -> {
				CorrectionReportItem item = cr.getCorrectionReportItemforStudent(m);
				return item!=null ? item.getNotaTestes() + item.getNotaDesign()*0.6: 0.0;
			}).sum();
			mediasProvasPraticas.put(m,(double)somatorioNotas/Constants.QUANTIDADE_PROVAS);
		});
		
		return mediasProvasPraticas;
	}
	
	public static Map<String,Double> buildMediasRoteiros() throws IOException, ConfigurationException, ServiceException{
		Map<String,Double> mediasRoteiros = new HashMap<String,Double>();
		Map<String,CorrectionReport> correctionReports = Util.loadCorrectionReports(new Predicate<String>() {
			//predicado para filtrar o que mostrar nas notas (provas e roteiros
			@Override
			public boolean test(String t) {
				return Constants.PATTERN_ROTEIRO.matcher(t).matches();
			}
		});
		Map<String,Student> alunos = Configuration.getInstance().getStudents();
		alunos.keySet().forEach( m -> {
			double somatorioNotas = correctionReports.values().stream().mapToDouble( cr -> {
				CorrectionReportItem item = cr.getCorrectionReportItemforStudent(m);
				return item!=null ? item.getNotaTestes() + item.getNotaDesign()*0.6: 0.0;
			}).sum();
			mediasRoteiros.put(m,(double)somatorioNotas/Constants.QUANTIDADE_ROTEIROS);
		});
		
		return mediasRoteiros;
	}

	
	public static void writeCorrectionComment(String id, String matriculaAluno, 
			String notaDesignStr, String classificacaoStr, String comment) throws IOException{
		
		File atividadeFolder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		if(atividadeFolder.exists()){
			CorrectionReport report = null;
			File correctionReportFile = new File(atividadeFolder,id + "-correction.json");
			if(correctionReportFile.exists()){
				report = Utilities.loadCorrectionReportFromJson(correctionReportFile);
			}
			if(report != null){
				double notaDesign = Double.valueOf(notaDesignStr);
				CorrectionClassification classificacao = 
						CorrectionClassification.valueOf(classificacaoStr);
				report.setNotaDesign(matriculaAluno, notaDesign);
				report.setClassificacao(matriculaAluno, classificacao);
				report.setComentario(matriculaAluno,comment.trim());
				Utilities.writeCorrectionReportToJson(report, correctionReportFile);
			}
			
		}
		
	}
	
	public static File exportPlaninhaGeralToExcel(String turma) throws ConfigurationException, IOException, ServiceException, BiffException{
		File outputFile = null;
		//busca todos os relatorios de correcao das atividades qe valem ponto
		//elas veem ordenadas por data
		Map<String,CorrectionReport> correctionReports = Util.loadCorrectionReports(new Predicate<String>() {
			//predicado para filtrar o que mostrar nas notas (provas e roteiros
			@Override
			public boolean test(String t) {
				return Constants.PATTERN_PROVA.matcher(t).matches()
						|| Constants.PATTERN_ROTEIRO.matcher(t).matches();
			}
		});
		
		//filtra pela turma
		List<CorrectionReport> reportsDaTurma = correctionReports.values().stream()
		.filter( cr -> cr.getId().endsWith(turma))
		.collect(Collectors.toList());
		
		//pega todas as atividades listadas em ordem cronologica e agrupadas por turma
		/*List<Atividade> atividadesDaTurma = 
        		Configuration.getInstance().getAtividades().values().stream()
        		.sorted( (a1,a2) -> a1.getDataHora().compareTo(a2.getDataHora()))
        		.collect(Collectors.groupingBy( Atividade::getTurma))
        		.get(turma)
        		.stream().filter( a -> matchesRoteiroOuProva(a.getId()))
        		.collect(Collectors.toList());*/

		outputFile = new File(Constants.CURRENT_SEMESTER_FOLDER,Constants.EXCEL_FILE_NOTAS_FINAIS_NAME + "-T" + turma + ".xlsx");

		XSSFWorkbook workbook = new XSSFWorkbook();
		String[] headersAtividades = {"","Matricula","Nome", "Nota Testes","Nota Design","Nota","Classificação","Comentarios"};

		reportsDaTurma.forEach( cr -> {
			try {
				createSheetAtividade(workbook, headersAtividades, cr);
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		Map<String,Student> alunos = Util.loadStudentLists();
		//filtra a media dos roteiros apenas para determinada turma
		Map<String,Double> mediasRoteirosDaTurma = new HashMap<String,Double>();
		Map<String,Double> mediasRoteirosTodosAlunos = Util.buildMediasRoteiros();
		mediasRoteirosTodosAlunos.keySet().stream().forEach( mat ->{
			if(alunos.get(mat).getTurma().equals(turma)){
				mediasRoteirosDaTurma.put(mat, mediasRoteirosTodosAlunos.get(mat));
			}
		});
		
		createSheetSumarizacao(workbook, mediasRoteirosDaTurma,
				Util.buildMediasProvasPraticas(), Util.loadSpreadsheetsMediasEDA(), 
				Util.buildMediasLEDAComFinal());
		
		try {
			FileOutputStream out = new FileOutputStream(outputFile);
			workbook.write(out);
			out.close();
			System.out.println("Excel for current semester written successfully in " + outputFile.getAbsolutePath());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputFile;
	}
	
	//apenas medias roteiros precisam ser filtrados por turma
	private static void createSheetSumarizacao(XSSFWorkbook workbook,
			Map<String,Double> mediasRoteiros, Map<String,Double> mediasProvasPraticas,
			Map<String,Double> mediasProvasTeoricasEDA,Map<String,Double> mediasFinais) throws BiffException, IOException{
		XSSFSheet sheet = workbook.createSheet("Sumarizacao");
		
		Row row = sheet.createRow(0);
		String[] headers = {"","Matricula","Nome", "MR","MPP","MPT","Media Final Semestre"};
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		
		for (int i = 0; i < headers.length; i++) {
			//Create a new cell in current row
			Cell cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(style);					
		}
		
		
		Map<String,Student> alunos = Util.loadStudentLists();
		int count = 1;
		Set<String> matriculas = mediasRoteiros.keySet();
		for (String mat : matriculas) {
			Row newRow = sheet.createRow(count);
			Cell cellNumber = newRow.createCell(0);
			cellNumber.setCellValue(count);
			count++;
			Cell cellMat = newRow.createCell(1);
			Student aluno = alunos.get(mat);
			cellMat.setCellValue(mat);
			Cell cellNome = newRow.createCell(2);
			cellNome.setCellValue(aluno.getNome());
			Cell cellMediaRoteiros = newRow.createCell(3);
			cellMediaRoteiros.setCellValue(mediasRoteiros.get(mat));
			Cell cellMediaProvasPraticas = newRow.createCell(4);
			cellMediaProvasPraticas.setCellValue(mediasProvasPraticas.get(mat));
			Cell cellMediaProvasTeoricasEDA = newRow.createCell(5);
			if(mediasProvasTeoricasEDA.get(mat) != null){
				cellMediaProvasTeoricasEDA.setCellValue(mediasProvasTeoricasEDA.get(mat));
			}else{
				cellMediaProvasTeoricasEDA.setCellValue("-");				
			}
			Cell cellMediaFinal = newRow.createCell(6);
			if(mediasFinais.get(mat) != null){
				cellMediaFinal.setCellValue(mediasFinais.get(mat));
			}else{
				cellMediaFinal.setCellValue("-");				
			}
		}
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}
	}
	private static void createSheetAtividade(XSSFWorkbook workbook, String[] headers,CorrectionReport report) throws BiffException, IOException{
		XSSFSheet sheet = workbook.createSheet(report.getId());
		Row row = sheet.createRow(0);
		//String[] headers = {"","Matricula","Nome", "Nota Testes","Nota Design","Nota","Classificação","Comentarios"};
		XSSFCellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		
		for (int i = 0; i < headers.length; i++) {
			//Create a new cell in current row
			Cell cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(style);					
		}
		
		
		Map<String,Student> alunos = Util.loadStudentLists();
		int count = 1;
		for (CorrectionReportItem cri : report.getReportItems()) {
			Row newRow = sheet.createRow(count);
			Cell cellNumber = newRow.createCell(0);
			cellNumber.setCellValue(count);
			count++;
			Cell cellMat = newRow.createCell(1);
			Student aluno = alunos.get(cri.getMatricula());
			cellMat.setCellValue(cri.getMatricula());
			Cell cellNome = newRow.createCell(2);
			cellNome.setCellValue(aluno.getNome());
			Cell cellNotaTestes = newRow.createCell(3);
			cellNotaTestes.setCellValue(cri.getNotaTestes());
			Cell cellNotaDesign = newRow.createCell(4);
			cellNotaDesign.setCellValue(cri.getNotaDesign());
			Cell cellNotaFinal = newRow.createCell(5);
			cellNotaFinal.setCellValue(cri.getNotaTestes() + cri.getNotaDesign()*0.6);
			Cell cellClassificacao = newRow.createCell(6);
			cellClassificacao.setCellValue(cri.getClassification().name());
			Cell cellComentario = newRow.createCell(7);
			cellComentario.setCellValue(cri.getComentario());
			
		}
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

	}
	private static boolean matchesRoteiroOuProva(String id){
		return Constants.PATTERN_PROVA.matcher(id).matches()
				|| Constants.PATTERN_ROTEIRO.matcher(id).matches();
	}
	public static File exportRoteiroToExcel(String id) throws IOException, BiffException{
		File atividadeFolder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		File outputFile = null;
		if(atividadeFolder.exists()){
			CorrectionReport report = Util.loadCorrectionReport(id);
			if(report != null){
				
				XSSFWorkbook workbook = new XSSFWorkbook();
				XSSFSheet sheet = workbook.createSheet(id);
				//Create a new row in current sheet with the header
				Row row = sheet.createRow(0);
				String[] headers = {"","Matricula","Nome", "Nota Testes","Nota Design","Nota","Classificação","Comentarios"};
				XSSFCellStyle style = workbook.createCellStyle();
				XSSFFont font = workbook.createFont();
				font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
				style.setFont(font);
				style.setAlignment(CellStyle.ALIGN_CENTER);
				
				for (int i = 0; i < headers.length; i++) {
					//Create a new cell in current row
					Cell cell = row.createCell(i);
					cell.setCellValue(headers[i]);
					cell.setCellStyle(style);					
				}
				
				
				Map<String,Student> alunos = Util.loadStudentLists();
				int count = 1;
				for (CorrectionReportItem cri : report.getReportItems()) {
					Row newRow = sheet.createRow(count);
					Cell cellNumber = newRow.createCell(0);
					cellNumber.setCellValue(count);
					count++;
					Cell cellMat = newRow.createCell(1);
					Student aluno = alunos.get(cri.getMatricula());
					cellMat.setCellValue(cri.getMatricula());
					Cell cellNome = newRow.createCell(2);
					cellNome.setCellValue(aluno.getNome());
					Cell cellNotaTestes = newRow.createCell(3);
					cellNotaTestes.setCellValue(cri.getNotaTestes());
					Cell cellNotaDesign = newRow.createCell(4);
					cellNotaDesign.setCellValue(cri.getNotaDesign());
					Cell cellNotaFinal = newRow.createCell(5);
					cellNotaFinal.setCellValue(cri.getNotaTestes() + cri.getNotaDesign()*0.6);
					Cell cellClassificacao = newRow.createCell(6);
					cellClassificacao.setCellValue(cri.getClassification().name());
					Cell cellComentario = newRow.createCell(7);
					cellComentario.setCellValue(cri.getComentario());
					
				}
				for (int i = 0; i < headers.length; i++) {
					sheet.autoSizeColumn(i);
				}
				try {
					outputFile = new File(atividadeFolder,id + ".xlsx");
					FileOutputStream out = new FileOutputStream(outputFile);
					workbook.write(out);
					out.close();
					System.out.println("Excel written successfully in " + outputFile.getAbsolutePath());
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return outputFile;
	}
	
	public static void writeCorrectionReport(CorrectionReport report, 
			String id) throws IOException{
	
		File atividadeFolder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		if(atividadeFolder.exists()){
			File correctionReport = new File(atividadeFolder,id + "-correction.json");
			if(correctionReport.exists()){
				Utilities.writeCorrectionReportToJson(report,correctionReport);
			}
		}
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
	
	public static List<Corretor> loadSpreadsheetMonitor() throws IOException, ServiceException{
		ArrayList<Corretor> monitores = new ArrayList<Corretor>();
		
		SpreadsheetService service = new SpreadsheetService("Sheet1");
        
        String sheetUrl =
            "https://spreadsheets.google.com/feeds/list/" + Constants.ID_MONITORES_SHEET + "/default/public/values";

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
            String senha = null;
            
            Corretor corretor = 
            		new Monitor(matricula, nome, email, fone, senha);
            if(matricula.length() == 7){ //matricula de um professor
            	corretor = new Professor(matricula, nome, email, fone,senha);
            }
            
            monitores.add(corretor);
        }
        
		return monitores;
	}
	
	public static Map<String,Atividade> loadSpreadsheetsAtividades(List<Corretor> corretores) throws WrongDateHourFormatException, IOException, ServiceException, ConfigurationException{
		Map<String,Atividade> atividades = new HashMap<String,Atividade>();
		for (String id : Constants.activitySheetIds) {
			atividades.putAll(loadSpreadsheetAtividades(id, corretores));
		}
		return atividades;
	}
	public static Map<String,Atividade> loadSpreadsheetAtividades(String idGoogleDrive, List<Corretor> monitores) throws WrongDateHourFormatException, IOException, ServiceException, ConfigurationException{
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
                Corretor corretor = 
                		monitores.stream().filter(m -> m.getNome().equals(nomeMonitor)).findAny().orElse(null);
                
                //System.out.println(val6);
                GregorianCalendar dataHoraInicioCorrecao = Util.buildDate(cec.getValue("DataInicioCorrecao".toLowerCase()));
                //System.out.println(val7);
                GregorianCalendar dataHoraEntregaCorrecao = Util.buildDate(cec.getValue("DataEntregaCorrecao".toLowerCase()));
                //System.out.println(val8);
                String links = cec.getValue("LinksVideoAulas".toLowerCase());
                List<LinkVideoAula> linksVideoAulas = listOfLinks(links);
                
                Atividade atividade = createAtividade(id,nome,descricao,dataHoraLiberacao, linksVideoAulas,
                		dataHoraEnvioNormal,dataHoraLimiteEnvioAtraso,monitoresDoRoteiro,
                		corretor,dataHoraInicioCorrecao,dataHoraEntregaCorrecao);
                
                atividades.put(atividade.getId(), atividade);
            }
            
    		//sobrescreve os dados lidos da spreadsheet (apenas os arquivos de ambiente e correcao coletados nas pastas)
    		Util.loadAtividadesFromUploadFolder(atividades);
    		
        return atividades;
	}

	public static Map<String,Double> loadSpreadsheetsMediasEDA() throws WrongDateHourFormatException, IOException, ServiceException, ConfigurationException{
		Map<String,Double> mediasEDA = new HashMap<String,Double>();
		for (String id : Constants.edaSheetIds) {
			mediasEDA.putAll(loadSpreadsheetMediasEDA(id));
		}
		
		return mediasEDA;
	}
	
	public static Map<String,Double> loadSpreadsheetMediasEDA(String idGoogleDrive) throws IOException, ServiceException, ConfigurationException{

		Map<String,Double> mediasEDA = new HashMap<String,Double>();
		
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
                String matricula = cec.getValue("mat".toLowerCase());
                String mediaEDA = cec.getValue("MédiaParcialEDA".toLowerCase());
                if(matricula == null){
                	break;
                }
                mediaEDA = mediaEDA.replace(',', '.');
				mediasEDA.put(matricula, Double.valueOf(mediaEDA));
            }
            
		
        return mediasEDA;
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
		File configFolder = Constants.CURRENT_SEMESTER_FOLDER;
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

	public static void uploadStudentList(File excelFile) throws FileNotFoundException{
		File configFolder = Constants.CURRENT_SEMESTER_FOLDER;
		
		if(!configFolder.exists()){
			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
		}
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

	public static String extractTurmaFromExcelFile(File excelFile){
		String turma = "0";
		String nomeArquivo = excelFile.getName();
		int indexOfCodigoLeda = nomeArquivo.indexOf(Constants.CODIGO_LEDA);
		if(indexOfCodigoLeda != -1){
			char numeroTurma = nomeArquivo.charAt(indexOfCodigoLeda + Constants.CODIGO_LEDA.length() + 2);
			turma = turma + numeroTurma;
		} 
		
		return turma;
	}

	@Deprecated
	public static Map<String,Atividade> loadSpreadsheetAtividadeFromExcel(List<Corretor> monitores) throws IOException{
		Map<String,Atividade> atividades = new HashMap<String,Atividade>();
		
		atividades.putAll(loadSpreadsheetAtividadeFromExcel("Cronograma T1.xlsx", monitores));
		atividades.putAll(loadSpreadsheetAtividadeFromExcel("Cronograma T2.xlsx", monitores));
		
		return atividades;
	}
	
	@Deprecated
	public static Map<String,Atividade> loadSpreadsheetAtividadeFromExcel(String excelFileName, List<Corretor> monitores) throws IOException{
		Map<String,Atividade> atividades = new HashMap<String,Atividade>();
		
		File excelFile = new File(Constants.DEFAULT_CONFIG_FOLDER,excelFileName);
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
                        String descricao = cellDescricao != null?cellDescricao.getStringCellValue():"";
                        GregorianCalendar dataHoraLiberacao = Util.buildDate(cellDataHoraLiberacao.getDateCellValue());
                        GregorianCalendar dataHoraEnvioNormal = Util.buildDate(cellDataHoraEnvioNormal.getDateCellValue());
                        GregorianCalendar dataHoraLimiteEnvioAtraso = Util.buildDate(cellDataHoraLimiteEnvioAtraso.getDateCellValue());
                        String nomesMonitores = cellNomesMonitores != null?cellNomesMonitores.getStringCellValue():"";
                        List<Monitor> monitoresDoRoteiro = listOfMonitores(nomesMonitores,monitores);
                        
                        String nomeMonitor = cellNomeMonitorCorretor.getStringCellValue();
                        Corretor corretor = monitores.stream().filter(c -> c.getNome().equals(nomeMonitor))
                        		.findFirst().orElse(null);

                        GregorianCalendar dataHoraInicioCorrecao = Util.buildDate(cellDataInicioCorrecao.getDateCellValue());
                        GregorianCalendar dataHoraEntregaCorrecao = Util.buildDate(cellDataEntregaCorrecao.getDateCellValue());
                        String links = cellLinksVideoAulas != null?cellLinksVideoAulas.getStringCellValue():"";
                        List<LinkVideoAula> linksVideoAulas = listOfLinks(links);
                        
                        Atividade atividade = createAtividade(id,nome,descricao,dataHoraLiberacao, linksVideoAulas,
                        		dataHoraEnvioNormal,dataHoraLimiteEnvioAtraso,monitoresDoRoteiro,
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
	public static List<Corretor> loadSpreadsheetMonitorFromExcel() throws IOException{
		ArrayList<Corretor> monitores = new ArrayList<Corretor>();
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

            		if(!matricula.equals("Matricula") && matricula.length() > 1){ //pode trabalhar aqui com o matcher
            			org.apache.poi.ss.usermodel.Cell cellNome = row.getCell(1); //celula com o nome
            			org.apache.poi.ss.usermodel.Cell cellEmail = row.getCell(2); //celula com a descricao
            			org.apache.poi.ss.usermodel.Cell cellFone = row.getCell(3); //celula de datahora de liberacao
            			org.apache.poi.ss.usermodel.Cell cellSenha = row.getCell(8); //celula de datahora de liberacao

            			String nome = cellNome != null?cellNome.getStringCellValue():"";
                        String email = cellEmail != null?cellEmail.getStringCellValue():"";
                        String fone = cellFone != null? (cellFone.getCellType() != XSSFCell.CELL_TYPE_STRING)?String.valueOf((int)cellFone.getNumericCellValue()):cellFone.getStringCellValue():"";
                        String senha = null;
                        
                        Corretor corretor = 
                        		new Monitor(matricula, nome, email, fone,senha);
                        
                        if(matricula.length() == 7){ //matricula de um professor
                        	corretor = new Professor(matricula, nome, email, fone,senha);
                        }
                        monitores.add(corretor);
            		}
            	}
            }
		}
		myWorkBook.close();

       
		return monitores;
	}

	public static void loadSpreadsheetSenhasFromExcel(List<Corretor> corretores) throws IOException{
		File excelFile = new File(Constants.CURRENT_SEMESTER_FOLDER,Constants.EXCEL_SENHAS_FILE_NAME);
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

            		if(!matricula.equals("Matricula") && matricula.length() > 1){ //pode trabalhar aqui com o matcher
            			org.apache.poi.ss.usermodel.Cell cellSenha = row.getCell(1); //celula com o nome
                        String senha = cellSenha != null? (cellSenha.getCellType() != XSSFCell.CELL_TYPE_STRING)?String.valueOf((int)cellSenha.getNumericCellValue()):cellSenha.getStringCellValue():"";
                        	
                        String matr = matricula;
                        Corretor corretor = corretores.stream().filter(c -> c.getMatricula().equals(matr))
                        		.findFirst().orElse(null);
                        if(corretor != null){
                        	corretor.setSenha(senha);
                        }
            		}
            	}
            }
		}
		myWorkBook.close();
	}
	private static Atividade createAtividade(String id, String nome, String descricao,
			GregorianCalendar dataHoraLiberacao, List<LinkVideoAula> linksVideoAulas, GregorianCalendar dataHoraEnvioNormal,
			GregorianCalendar dataHoraLimiteEnvioAtraso, List<Monitor> monitores, Corretor corretor,
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
			//System.out.println("MOntando atividade da prova: " + id);
			//System.out.println("Corretor: " + corretor);
			result = new Prova(id,nome,descricao,dataHoraLiberacao,
					linksVideoAulas,dataHoraEnvioNormal,
					monitores,(Professor) corretor,dataHoraInicioCorrecao,dataHoraEntregaCorrecao,null,null);						
			
		}
		return result;
	}

	public static void loadAtividadesFromUploadFolder(Map<String,Atividade> atividades){
		
			Set<String> keys = atividades.keySet();
			for (String key : keys) {
				Atividade atividade = atividades.get(key);
				File folderToSearch = Constants.ROTEIROS_FOLDER;
				if(Constants.PATTERN_PROVA.matcher(atividade.getId()).matches()){
					folderToSearch = Constants.PROVAS_FOLDER;
				}
				File[] files = folderToSearch.listFiles(new FileFilter() {
					
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

	public static File compact(File folder) throws IOException, BiffException{
		Util.exportRoteiroToExcel(folder.getName());
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
	
	public static File compact(String id) throws IOException, BiffException{
		Util.exportRoteiroToExcel(id); //exporta para excel de forma que a planilah seja incluida no arquivo compactado
		File folder = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
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
	public static void compactAllData() throws IOException{
		File[] foldersToCompact = 
				Constants.CURRENT_SEMESTER_FOLDER.listFiles(new FileFilter() {
					
					@Override
					public boolean accept(File pathname) {
						return Constants.PATTERN_PROVA.matcher(pathname.getName()).matches()
								|| Constants.PATTERN_ROTEIRO.matcher(pathname.getName()).matches();
					}
				});
		Runnable compaction = new Runnable() {
			
			@Override
			public void run() {
				for (File file : foldersToCompact) {
					try {
						compact(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BiffException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread compactionThread = new Thread(compaction);
		compactionThread.start();
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
	
	public static Submission getSubmissionForStudent(String idAtividade, String matricula) throws ConfigurationException, IOException, ServiceException{
		Submission result = null;
		Map<String,List<Submission>> allSubmissions = Util.allSubmissions(false);
		List<Submission> submissions = allSubmissions.get(idAtividade);
		if(submissions != null){
			result = submissions.stream().filter( s -> s.getAluno().getMatricula().equals(matricula))
				.findFirst().orElse(null);
		}
				
		return result;
	}
	
	public static Map<String,Map<String,Boolean>> totalizacaoFaltas() throws ConfigurationException, IOException, ServiceException{
		// o retorno eh do tipo matricula -> {idAtividade -> presenca(boolean)}
		Map<String,Map<String,Boolean>> result = new HashMap<String,Map<String,Boolean>>();
		Map<String,List<Submission>> todasSubmissoes = 
				Util.allSubmissions(false); //sem roteiros de revisao
		Map<String,Student> alunos = Configuration.getInstance().getStudents();
		
		//varre todos os alunos procurando as presencas nas submissoes dele
		//e levando em consideração as reposicoes
		alunos.forEach( (mat,aluno) -> {
			Map<String,Boolean> presencasAluno = new HashMap<String,Boolean>();
			todasSubmissoes.forEach( (id,list) -> {
				Submission sub = list.stream().filter( s -> s.getAluno().getMatricula().equals(mat)).findFirst().orElse(null);
				boolean submeteu = false;
				if(sub != null){
					if(sub.getArquivoSubmetido() != null){
						submeteu = true;
					}else{
						submeteu = verificaPresencaReposicao(mat,id,todasSubmissoes);
					}				
				}
				//precisa antes de inserir julgar as provas normais e de reposicao
				//para nao contar dobrado
				presencasAluno.put(id, submeteu);
			});
			result.put(mat, presencasAluno);
		});
		return result;
	}
	
	/**
	 * Verifica se para uma dada prova, se o aluno esteve presente na prova ou nao reposicao
	 * nenhuma falta deve ser registrada. Se nao houve envio para nenhuma delas entao 
	 * deve registrar falta apenas na prova pratica e nao na reposicao. 
	 * @param id
	 * @param todasSubmissoes
	 * @return
	 */
	private static boolean verificaPresencaReposicao(String mat, String id, Map<String, List<Submission>> todasSubmissoes) {
		boolean presenca = false;
		if(Constants.PATTERN_PROVA_PRATICA.matcher(id).matches()){ //eh prova pratica e ja nao teve submissao para ela
			//tem que verificar se teve submissao para a respectiva prova de reposicao
			String idProvaReposicao = "PR"+ id.charAt(2)+ id.substring(3);
			List<Submission> submissoes = 
					todasSubmissoes.get(idProvaReposicao);
			if(submissoes != null){
				Submission sub = submissoes.stream()
						.filter( s -> s.getAluno().getMatricula().equals(mat)).findFirst().orElse(null);
				if(sub != null){
					presenca = sub.getArquivoSubmetido() != null;
				}
			}
		} else if (Constants.PATTERN_PROVA_REPOSICAO.matcher(id).matches()){
			/*//eh prova de reposicao e tem que verificar se teve submissao para a respectiva
			//prova pratica. se sim, entao nao precisa registrar falta na reposicao
			String idProvaPratica = "PP"+ id.charAt(2)+ id.substring(3);
			List<Submission> submissoes = 
					todasSubmissoes.get(idProvaPratica);
			if(submissoes != null){
				Submission sub = submissoes.stream()
						.filter( s -> s.getAluno().getMatricula().equals(mat)).findFirst().orElse(null);
				if(sub != null){
					presenca = sub.getArquivoSubmetido() != null;
				}
			}*/
			presenca = true;
		} 
		return presenca;
	}

	public static Map<String,List<Submission>> allSubmissions(boolean showAll) throws ConfigurationException, IOException, ServiceException{
		//precisa ordenar as submissoes pelas datas de cada atividade
		Map<String,Atividade> atividades = Configuration.getInstance().getAtividades();

		Map<String,List<Submission>> result = //new HashMap<String,List<Submission>>();
		new TreeMap<String,List<Submission>>(
				(s1,s2) -> {
					Atividade a1 = atividades.get(s1);
					Atividade a2 = atividades.get(s2);
					if(a1.getDataHora().compareTo(a2.getDataHora()) == 0){
						return a1.getNome().compareTo(a2.getNome());
					}else{
						return a1.getDataHora().compareTo(a2.getDataHora());
					}
				});

		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File currentSemester = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		//System.out.println("Semestre atual: " + currentSemester.getAbsolutePath());
		if(currentSemester.exists()){
			File[] folders = currentSemester.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File arg0) {
					boolean matches = Constants.PATTERN_ROTEIRO.matcher(arg0.getName()).matches()
							|| Constants.PATTERN_PROVA.matcher(arg0.getName()).matches();
					if(showAll){
						matches = matches || Constants.PATTERN_ROTEIRO_REVISAO.matcher(arg0.getName()).matches();
					}
					return matches;
							
				}
			});
			//System.out.println("Subpastas encontradas: " + folders.length);
			for (int i = 0; i < folders.length; i++) {
				String folderName = folders[i].getName();
				List<Submission> submissions = submissions(folders[i]);
				//System.out.print(folders[i].getAbsolutePath() + "(" + folderName + ") ");
				//System.out.println(submissions!= null?submissions.size():0);
				
				//System.out.println(" submissoes " + submissions!= null);
				result.put(folderName,submissions);
			}
		}
		/*Map<String,List<Submission>> resultFinal = result.keySet().stream().sorted( (id1,id2) ->{
			Atividade a1 = atividades.get(id1);
			Atividade a2 = atividades.get(id2);
			return a1.getDataHora().compareTo(a2.getDataHora());
		}).collect(Collectors.toMap(id -> id, id -> result.get(id)));*/
		
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
		
	private static List<Monitor> listOfMonitores(String listOfMonitores,List<Corretor> monitores) {
		ArrayList<Monitor> list = new ArrayList<Monitor>();
		if(listOfMonitores != null){
			String[] nomes = listOfMonitores.split(",");
			for (String nome : nomes) {
				monitores.stream().forEach(m -> {
					if(m.getNome().equals(nome.trim()) && !(m instanceof Professor)){
						list.add((Monitor) m);
					}
				});
			}
		}
		
		return list;
	}
	private static List<LinkVideoAula> listOfLinks(String listOfLinks){
		ArrayList<LinkVideoAula> list = new ArrayList<LinkVideoAula>();
		Function<String[], List<LinkVideoAula>> fill = new Function<String[], List<LinkVideoAula>>() {
			@Override
			public List<LinkVideoAula> apply(String[] links) {
				for (String textoURL : links) {

					LinkVideoAula link = new LinkVideoAula(textoURL.trim());
					list.add(link);

				}
				return list;
			}
		};
		if(listOfLinks != null && listOfLinks.length() > 0){
			fill.apply(listOfLinks.split(","));
		}
		return list;
	}
	

	public static void main(String[] args) throws ConfigurationException, IOException, WrongDateHourFormatException, ServiceException, BiffException, JDOMException {
		//Util.sendEmail("adalberto.cajueiro@gmail.com", "adalberto.cajueiro@gmail.com", "Teste", "conteudo", "adalberto.cajueiro@gmail.com", "acfcaju091401");
		//Util.send2();
		//System.out.println("Email enviado");
		//Util.loadConfig("conf/application.conf");
		//https://docs.google.com/spreadsheets/d//pubhtml
		//Map<String,Prova> provas = Util.loadSpreadsheetProvas("1mt0HNYUMgK_tT_P2Lz5PQjBP16F6Hn-UI8P21C0iPmI");
		//provas.forEach((id,p) -> System.out.println(p));
		//List<Monitor> monitores = Util.loadSpreadsheetMonitorFromExcel();
		//Map<String,Atividade> atividades = Util.loadSpreadsheetsAtividades(monitores);
		//Map<String,Student> alunos = Util.loadStudentLists();
		//List<Student> students = alunos.values().stream().filter(a -> a.getTurma() == "01").sorted((a1,a2) -> a1.getNome().compareTo(a2.getNome())).collect(Collectors.toList());
		//students.forEach(s -> System.out.println(s.getNome()));
		Map<String, Double> mediasFinais = Util.buildMediasLEDAComFinal();
		Map<String, Double> mediasSemFinais = Util.buildMediasLEDASemFinal();
		Double mediaComFinal = mediasFinais.get("115211289");
		Double mediaSemFinal = mediasSemFinais.get("115211289");
		Map<String, Student> students = Util.loadStudentLists();
		students.forEach((s,e) -> {
			System.out.println(e.getNome() + " - " + e.getTurma());
		});
		Util.exportPlaninhaGeralToExcel("01");
		List<Submission> submissoes = Util.submissions(new File(Constants.CURRENT_SEMESTER_FOLDER,"R01-01"));
		Util.exportRoteiroToExcel("R02-01");
		Map<String,Double> mediasEDA = Util.loadSpreadsheetsMediasEDA();
		Map<String,Student> alunos = Util.loadStudentLists();
		mediasEDA.forEach((m,med) -> {
			if(alunos.get(m) != null){
				System.out.println(alunos.get(m).getNome() + " teve media de EDA " + med);
			}
		});
		Submission sub = Util.getSubmissionForStudent("R02-01", "116110707");
		System.out.println(sub.generateSubmittedFileLink());
		Map<String,Double> medias = Util.buildMediasProvasPraticas();
		
		Map<String,CorrectionReport> correctionReports = Util.loadCorrectionReports(new Predicate<String>() {
			//predicado para filtrar o que mostrar nas notas (provas e roteiros
			@Override
			public boolean test(String t) {
				return Constants.PATTERN_PROVA.matcher(t).matches()
						|| Constants.PATTERN_ROTEIRO.matcher(t).matches();
			}
		});
		
		TreeMap<String, CorrectionReport> mapReport = new TreeMap<String,CorrectionReport>((cr1,cr2) -> {
        	int result = 1;
        	if(cr1.charAt(0) != cr2.charAt(0)){
        		result = cr2.charAt(0) -  cr1.charAt(0);
        	} else{
        		result = cr1.compareTo(cr2);
        	}
        	return result;
        
		});
		mapReport.putAll(correctionReports);
		correctionReports = correctionReports.values().stream().sorted( (cr1,cr2) -> {
        	int result = 1;
        	if(cr1.getId().charAt(0) != cr2.getId().charAt(0)){
        		result = cr2.getId().charAt(0) -  cr1.getId().charAt(0);
        	} else{
        		result = cr1.getId().compareTo(cr2.getId());
        	}
        	return result;
        }).collect(Collectors.toMap(cr -> cr.getId(), cr -> cr));
		
		List<Corretor> corretores = Util.loadSpreadsheetMonitorFromExcel();
		SimilarityMatrix matrix = Util.buildSimilarityMatrix("R01-01");
		for (int i = 0; i < matrix.getSimilarities().length; i++) {
			System.out.println(Arrays.toString(matrix.getSimilarities()[i]));
		}
		Util.loadSpreadsheetSenhasFromExcel(corretores);
		Map<String,Atividade> atividades = Util.loadSpreadsheetAtividadeFromExcel(corretores);
		atividades.values().forEach(a -> a.getMonitores().forEach(m -> System.out.println(m.getNome())));
		atividades.values().stream().filter(a -> a instanceof Roteiro).forEach(r -> System.out.println(((Roteiro) r).getCorretor()));
		//CorrectionReport report = Util.loadCorrectionReport("RR1-01");
		//report.getReportItems().forEach(tri -> System.out.println(tri.getMatricula()));
		int i = 2;
		int k = 1;
		
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
