package poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.SAXException;

public class POIUtils {
	
	public static void loadPDF() throws IOException, SAXException, TikaException{
		BodyContentHandler handler = new BodyContentHandler();
	      Metadata metadata = new Metadata();
	      FileInputStream inputstream = new FileInputStream(new File("/Users/adalbertocajueiro/Documents/Pessoal/Betinho/condominio/2019/extratos/Sicoob comprovante (20-04-2019 08-43-54).pdf"));
	      ParseContext pcontext = new ParseContext();
	      
	      //parsing the document using PDF parser
	      PDFParser pdfparser = new PDFParser(); 
	      pdfparser.parse(inputstream, handler, metadata,pcontext);
	      
	      
	      //getting the content of the document
	      //System.out.println("Contents of the PDF :" + handler.toString());
	      List<String> content = content(handler.toString());
	      for (String string : content) {
	  			System.out.println(string);
		}
	}
	
	private static List<String> content(String content){
		ArrayList<String> list = new ArrayList<String>();
		System.out.println("Original: ");
		
		content = content.replaceAll("\n\n\n\n\n", "\n"); //remover quebra de pagina
		content = content.replaceAll("\n\n\n\n", "\n"); //remover quebra de pagina
		content = content.replaceAll("\n\n\n", "\n"); //remover quebra de pagina
		System.out.println(content);
		String[]  text = content.split("\n\n");
		for (String string : text) {
			list.add(string);
		}
		System.out.println("Elementos: " + list.size());
		list = (ArrayList<String>) list.stream().
				filter(s -> s.length() > 1).
				collect(Collectors.toList());
		System.out.println("Elementos depois: " + list.size());
		return list;
		
	}
	
	public static void main(String[] args) throws Exception{
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