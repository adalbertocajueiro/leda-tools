package br.edu.ufcg.ccc.leda.submission.util;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
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

public class Util {

	public static final Pattern PATTERN_DATE_TIME = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");
	
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
		String result = config.getRoteiro();
		
		result = result + "-" + removeTempInfoFromFileName(file.getName());
		
		return result;
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
	
	public static Map<String, Roteiro> loadRoteirosFromJson(File jsonFile) throws ConfigurationException, IOException{
		Gson gson = new Gson();
		FileReader fr = new FileReader(jsonFile);
		Map<String, Roteiro> map = gson.fromJson(fr, new TypeToken<Map<String,Roteiro>>(){}.getType());
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
	
	public static Properties loadProperties() throws IOException{
		Properties p = new Properties();
		File confFolder = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
		FileReader fr = new FileReader(new File(confFolder,"app.properties"));
		p.load(fr);
		
		return p;
	}
	
	public static GregorianCalendar buildDate(String dataHora) throws WrongDateHourFormatException{
		GregorianCalendar result = new GregorianCalendar();
		//se estiver no formato errado retorna uma excecao
		//tem que fazer isso com string format provavelmente ou regex
		if(!PATTERN_DATE_TIME.matcher(dataHora).matches()){
			throw new WrongDateHourFormatException("Date " + dataHora + " does not respect the format DD/MM/YYYY HH:MM:SS");
		}
		result.set(Calendar.DATE, Integer.parseInt(dataHora.substring(0, 2)));
		result.set(Calendar.MONTH, Integer.parseInt(dataHora.substring(3,5)) - 1); //janeiro eh considerado mes 0
		result.set(Calendar.YEAR, Integer.parseInt(dataHora.substring(6, 10)));
		result.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dataHora.substring(11, 13)));
		result.set(Calendar.MINUTE, Integer.parseInt(dataHora.substring(14, 16)));
		result.set(Calendar.SECOND, Integer.parseInt(dataHora.substring(17)));
		
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
	
	public static Map<String,Roteiro> loadSpreadsheetRoteiros(String idGoogleDrive) throws WrongDateHourFormatException, IOException, ServiceException, ConfigurationException{
		Map<String,Roteiro> roteiros = new HashMap<String,Roteiro>();
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
                String idRoteiro = cec.getValue("Roteiro".toLowerCase());
                //System.out.println(val);
                String descricao = cec.getValue("Descrição".toLowerCase());
                //System.out.println(val2);
                GregorianCalendar dataHoraLiberacao = Util.buildDate(cec.getValue("Data-HoraLiberação".toLowerCase()));
                //System.out.println(val3);
                GregorianCalendar dataHoraEnvioNormal = Util.buildDate(cec.getValue("Data-HoraLimiteEnvioNormal".toLowerCase()));
                //System.out.println(val4);
                GregorianCalendar dataHoraLimiteEnvioAtraso = Util.buildDate(cec.getValue("Data-HoraLimiteEnvioAtraso".toLowerCase()));
                //System.out.println(val5);
                String monitorCorretor = cec.getValue("MonitorCorretor".toLowerCase());
                //System.out.println(val6);
                GregorianCalendar dataHoraInicioCorrecao = Util.buildDate(cec.getValue("DataInicioCorrecao".toLowerCase()));
                //System.out.println(val7);
                GregorianCalendar dataHoraEntregaCorrecao = Util.buildDate(cec.getValue("DataEntregaCorrecao".toLowerCase()));
                //System.out.println(val8);
                Roteiro roteiro = new Roteiro(idRoteiro,descricao,dataHoraLiberacao,
                		dataHoraEnvioNormal,dataHoraLimiteEnvioAtraso,monitorCorretor,
                		dataHoraInicioCorrecao,dataHoraEntregaCorrecao,null,null);
                roteiros.put(roteiro.getId(), roteiro);
            }
            
            File configFolder = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
    		if(!configFolder.exists()){
    			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
    		}
    		File jsonFileRoteiros = new File(configFolder,FileUtilities.JSON_FILE_ROTEIRO);

    		//sobrescreve os dados lidos da spreadsheet
    		FileUtilities.loadRoteirosFromUploadFolder(roteiros);
    		
    		//com o reuso pode ter acontecido de alguma data ter sido modificada. entao salvamos novamente no json
    		Util.writeRoteirosToJson(roteiros, jsonFileRoteiros);
        
        return roteiros;
	}
	
	public static Map<String,Prova> loadSpreadsheetProvas(String idGoogleDrive) throws WrongDateHourFormatException, IOException, ServiceException, ConfigurationException{
		Map<String,Prova> provas = new HashMap<String,Prova>();
		SpreadsheetService service = new SpreadsheetService("Sheet2");
            String sheetUrl =
            		"https://spreadsheets.google.com/feeds/list/" + idGoogleDrive + "/default/public/values";
            		//"https://docs.google.com/spreadsheets/d/19npZPI7Y1jyk1jxNKHgUZkYTk3hMT_vdmHunQS-tOhA";

            // Use this String as url
            URL url = new URL(sheetUrl);

            // Get Feed of Spreadsheet url
            ListFeed lf = service.getFeed(url, ListFeed.class);

            //Iterate over feed to get cell value
            for (ListEntry le : lf.getEntries()) {
                CustomElementCollection cec = le.getCustomElements();
                //Pass column name to access it's cell values
                String idProva = cec.getValue("Prova".toLowerCase());
                //System.out.println(idProva);
                String descricao = cec.getValue("Descrição".toLowerCase());
                //System.out.println(val2);
                GregorianCalendar dataHoraLiberacao = Util.buildDate(cec.getValue("Data-HoraLiberação".toLowerCase()));
                //System.out.println(val3);
                GregorianCalendar dataHoraLimiteEnvio = Util.buildDate(cec.getValue("Data-HoraLimiteEnvio".toLowerCase()));
                //System.out.println(val4);
                Prova prova = new Prova(idProva,descricao,null,null,
                		dataHoraLiberacao,dataHoraLimiteEnvio);
                provas.put(prova.getProvaId(), prova);
            }
            
            File configFolder = new File(FileUtilities.DEFAULT_CONFIG_FOLDER);
    		if(!configFolder.exists()){
    			throw new FileNotFoundException("Missing config folder: " + configFolder.getAbsolutePath());
    		}
    		File jsonFileProvas = new File(configFolder,FileUtilities.JSON_FILE_PROVA);

    		//sobrescreve os dados lidos da spreadsheet
    		FileUtilities.loadProvasFromUploadFolder(provas);
    		
    		//com o reuso pode ter acontecido de alguma data ter sido modificada. entao salvamos novamente no json
    		Util.writeProvasToJson(provas, jsonFileProvas);
        
        return provas;
	}
	public static File compact(File folder) throws IOException{
		//id pode ser d euma prova ou de um roteiro
		File target = null;
		Compactor compactor = new Compactor();
		//File uploadFolder = new File(FileUtilities.UPLOAD_FOLDER);
		//File currentSemester = new File(uploadFolder,FileUtilities.CURRENT_SEMESTER);
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
		
	
	public static void main(String[] args) throws ConfigurationException, IOException, WrongDateHourFormatException, ServiceException {
		//Util.sendEmail("adalberto.cajueiro@gmail.com", "adalberto.cajueiro@gmail.com", "Teste", "conteudo", "adalberto.cajueiro@gmail.com", "acfcaju091401");
		//Util.send2();
		//System.out.println("Email enviado");
		//Util.loadConfig("conf/application.conf");
		//https://docs.google.com/spreadsheets/d//pubhtml
		Map<String,Prova> provas = Util.loadSpreadsheetProvas("1mt0HNYUMgK_tT_P2Lz5PQjBP16F6Hn-UI8P21C0iPmI");
		provas.forEach((id,p) -> System.out.println(p));
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
