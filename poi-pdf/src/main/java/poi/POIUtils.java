package poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook; 
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import green.atm.extrato.Transacao;
import green.atm.util.Util;


public class POIUtils {
	
	public static final String TOKEN_INICIO_MOVIMENTACOES= "DATA HISTÓRICO VALOR";
	public static final String TOKEN_FIM_MOVIMENTACOES= "RESUMO";
	public static final String TOKEN_PERIODO_EXTRATO = "PERÍODO:";
	public static final Pattern PATTERN_DATE = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}");
	public static final Pattern PATTERN_VALOR_CREDITO = Pattern.compile("([0-9]{1,3}[.])*[0-9]{1,3},[0-9]{2}[C]");
	public static final Pattern PATTERN_VALOR_DEBITO = Pattern.compile("([0-9]{1,3}[.])*[0-9]{1,3},[0-9]{2}[D]");
	public static final String SALDO_BLOQUEADO_ANTERIOR  = "SALDO BLOQ.ANTERIOR";
	public static final String SALDO_DO_DIA = "SALDO DO DIA";
	public static final String SALDO_ANTERIOR  = "SALDO ANTERIOR";
	private static String TOKEN_DOC = "DOC.:";

	public static void loadPDF() throws IOException, SAXException, TikaException{
		BodyContentHandler handler = new BodyContentHandler();
	      Metadata metadata = new Metadata();
	      //FileInputStream inputstream = new FileInputStream(new File("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (20-04-2019 08-43-54).pdf"));
	      FileInputStream inputstream = new FileInputStream(new File("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (01-05-2019 19-37-03).pdf"));
	      
	      /*File f = new File("/Users/adalbertocajueiro/leda-upload/2019.1/RR1-01/subs/112211050-FABIANA ALVES GOMES.zip");
	      long time = f.lastModified();
	      Date d = new Date(time);
	      SimpleDateFormat df = new SimpleDateFormat("$(dd-MM-YYYY HH*mm*ss)$");
	      System.out.println(df.format(d));*/
	      
	      /*System.out.println(f.exists());
	      GregorianCalendar now = new GregorianCalendar();
		  SimpleDateFormat df = new SimpleDateFormat("dd-MM-YYYY HH*mm*ss");
	      String newName = f.getName().substring(0,f.getName().lastIndexOf('.')) + " (" + df.format(now.getTime()) + ").zip";
	      System.out.println(newName);
	      File oldRenamed = new File(f.getParentFile(),newName);
	      FileUtils.copyFile(f,oldRenamed);*/
	      ParseContext pcontext = new ParseContext();
	      
	      //parsing the document using PDF parser
	      PDFParser pdfparser = new PDFParser(); 
	      pdfparser.parse(inputstream, handler, metadata,pcontext);
	      
	      
	      //getting the content of the document
	      //System.out.println("Contents of the PDF :" + handler.toString());
	      List<String> content = content(handler.toString());
	      //for (String string : content) {
	  	//		System.out.println(string);
		//}
	}
	
	private static List<String> content(String content){
		ArrayList<String> list = new ArrayList<String>();

		content = content.replaceAll("\n\n\n\n", "\n"); //remover quebra de pagina
		content = content.replaceAll("\n\n", "\n"); //remover linhas duplas

		extrairPeriodoExtrato(content);
		content = extrairResumoInicial(content);
		content = extrairResumoFinal(content);
		String saldoAnteriorTexto = extrairSaldoAnterior(content);

		String[]  text = content.split("\n");

		for (String string : text) {
			list.add(string);
		}
		//FILTRA A LISTA PARA EXTRAIR INFORMACAO DE SALDO ANTERIOR, SALDO BLOQUEADO ANTERIOR e SALDO DO DIA
		list = (ArrayList<String>) list.stream().
				filter(s -> !(s.contains(SALDO_ANTERIOR) 								
						|| s.contains(SALDO_BLOQUEADO_ANTERIOR) 
						|| s.contains(SALDO_DO_DIA))).
				collect(Collectors.toList());
		//System.out.println("Elementos depois: " + list.size());

		ArrayList<String> transacoes = new ArrayList<String>();
		String transacao = "";
		for (String string : list) {
			
			if(string.startsWith(TOKEN_DOC)) {
				transacao = transacao + string;
				transacoes.add(transacao);
				transacao = "";
			} else {
				transacao = transacao + string + "$";
			}
		}

		transacoes.forEach(t -> System.out.println(t));
		System.out.println(transacoes.size());
		ArrayList<Transacao> transacoesObj = new ArrayList<Transacao>();
		transacoes.stream().forEach( t -> transacoesObj.add(Util.buildTransacao(t, 2019)));
		double saldoAnterior = Double.parseDouble(saldoAnteriorTexto.substring(0, saldoAnteriorTexto.length()-1));
		//loop para encontrar o valor minimo do saldo atingido pela conta
		double saldoMinimo = saldoAnterior;
		double saldoAtual = saldoAnterior;
		for (Transacao t : transacoesObj) {
			saldoAtual = saldoAtual + t.getValor();
			if (saldoAtual < saldoMinimo) {
				saldoMinimo = saldoAtual;
			}
		}
		
		return list;
		
	}
	private static String extrairSaldoAnterior(String content) {
		String result = "";
		int indexSaldoAnterior = content.indexOf(SALDO_ANTERIOR);
		result = content.substring(indexSaldoAnterior + SALDO_ANTERIOR.length() + 1,content.indexOf('\n', indexSaldoAnterior)).trim();
		result = result.replaceAll("[.]", "");
		result = result.replaceAll(",", ".");
		return result;
	}
	private static String extrairResumoInicial(String content) {
		String result = content;
		result = content.substring(content.indexOf(TOKEN_INICIO_MOVIMENTACOES) + TOKEN_INICIO_MOVIMENTACOES.length() + 1);
		return result;
	}
	private static String extrairResumoFinal(String content) {
		String result = content;
		result = content.substring(0,content.indexOf(TOKEN_FIM_MOVIMENTACOES));
		return result;
	}
	private static String  extrairPeriodoExtrato(String content) {
		String result = "";
		int indexInicio = content.indexOf(TOKEN_PERIODO_EXTRATO) + TOKEN_PERIODO_EXTRATO.length();
		int indexFim = content.indexOf('\n', indexInicio);
		String[] datas = content.substring(indexInicio, indexFim).split("-");
		GregorianCalendar inicio = buildDate(datas[0].trim());
		GregorianCalendar fim = buildDate(datas[1].trim());
		
		return result;
	}
	public static GregorianCalendar buildDate(String dataHora) {
		GregorianCalendar result = null;
		if(dataHora != null){
			result = new GregorianCalendar();
			//se estiver no formato errado retorna uma excecao
			//tem que fazer isso com string format provavelmente ou regex
			if(PATTERN_DATE.matcher(dataHora).matches()){
				result.set(Calendar.DATE, Integer.parseInt(dataHora.substring(0, 2)));
				result.set(Calendar.MONTH, Integer.parseInt(dataHora.substring(3,5)) - 1); //janeiro eh considerado mes 0
				result.set(Calendar.YEAR, Integer.parseInt(dataHora.substring(6, 10)));
			}
			
		} 
		return result;
	}
	public static void main(String[] args) throws Exception{
		GregorianCalendar now = new GregorianCalendar();
		System.out.println(now.get(Calendar.YEAR));
		System.out.println("Casou: " + Pattern.compile(".*([0-9]{1,3}[.])*[0-9]{1,3},[0-9]{2}[D].*").matcher("797,14D").matches());
		String[] elementos = PATTERN_VALOR_DEBITO.split("797,14C");
		
		String patternString = ".*http://.*";
		
		Matcher mtch = Pattern.compile("[.]*([0-9]{1,3}[.])*[0-9]{1,3},[0-9]{2}[D[C]][.]*").matcher("TARIFA COBRANÇA 2,00D");
        List<String> ips = new ArrayList<String>();
        String textoValor = "";
        if(mtch.find()){
        	textoValor = mtch.group();
        }
        
        double valor = 0.0;
        if(textoValor.endsWith("D")){
			textoValor = textoValor.replaceAll("[.]", "");
			textoValor = textoValor.replaceAll(",", ".");
			try {
				valor = - Double.parseDouble(textoValor);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				System.out.print("Excecao: de covnersao de valor para double " + textoValor);
			}
		}
        int groups = mtch.groupCount();
        ips.forEach(ip -> System.out.println(ip));
                loadPDF();
	}


	public static void loadExcelData() throws FileNotFoundException, IOException {
		FileInputStream is = new FileInputStream("/Users/adalbertocajueiro/Documents/UFCG/2019.1/disciplinas/leda/frequencia_2019.1_1411306-01_095134819.xlsx");
		Workbook wb = new XSSFWorkbook(is);

		
         for (int i = 0; i < wb.getNumberOfSheets(); i++) {
		Sheet sheet = wb.getSheetAt(i);
		System.out.println(wb.getSheetName(i));
           //pega a partir da 1a linha para descartar o header
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%");
		Iterator<Row> rowIterator = sheet.iterator();
		//descarta a primeira linha
		rowIterator.next();
		while (rowIterator.hasNext()) {
			Row currentRow = rowIterator.next();
			
			Cell cellMatricula = currentRow.getCell(1);
			Cell cellNome = currentRow.getCell(2);
			if(cellMatricula != null) {
				String matricula = null;
				String nome = null;
				if(cellMatricula.getCellType() == CellType.NUMERIC){
					matricula = String.valueOf((int)cellMatricula.getNumericCellValue()); //
				}else{
					matricula = cellMatricula.getStringCellValue();
				}
				if(cellNome.getCellType() == CellType.NUMERIC){
					nome = String.valueOf((int)cellNome.getNumericCellValue()); //
				}else{
					nome = cellNome.getStringCellValue();
				}
				System.out.println("(" + matricula + "," + nome + ")");
			}
			
			
			
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%");
		
         }
         wb.close();
         is.close();
	}
}
