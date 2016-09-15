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
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

	public static final String AUTOR_MATRICULA = "autor.matricula";
	public static final String AUTOR_NOME = "autor.nome";

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
	
	public static void removeFilesByPrefix(File folder, String prefix) throws IOException{
		File[] files = folder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().startsWith(prefix);
			}
		});
		for (int i = 0; i < files.length; i++) {
			Files.deleteIfExists(files[i].toPath());
		}
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
	
	/**
	 * Retorna a lista dos alunos que fizeram o download de um roteiro ou prova. Essa lista
	 * será utilizada para saber quem fez o download mas não submeteu a resposta.
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	public static List<String> alunosDownload(String id) throws IOException{
		ArrayList<String> alunosComDownload = new ArrayList<String>();
		File uploadFolder = new File(FileUtilities.UPLOAD_FOLDER);
		File currentSemester = new File(uploadFolder,FileUtilities.CURRENT_SEMESTER);
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

		File uploadFolder = new File(FileUtilities.UPLOAD_FOLDER);
		File currentSemester = new File(uploadFolder,FileUtilities.CURRENT_SEMESTER);
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
			File submissionsFolder = new File(atividadeFolder,FileUtilities.SUBMISSIONS_FOLDER);
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
					} catch (ConfigurationException | IOException | ServiceException | RoteiroException
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
		ArrayList<List<FileCopy>> result = new ArrayList<List<FileCopy>>();
		
		File uploadFolder = new File(FileUtilities.UPLOAD_FOLDER);
		File currentSemester = new File(uploadFolder,FileUtilities.CURRENT_SEMESTER);
		File folder = new File(currentSemester,id);
		File submissionsFolder = new File(folder,FileUtilities.SUBMISSIONS_FOLDER);
		
		File[] subfolders = submissionsFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		for (File file : subfolders) {
			List<FileCopy> copies = listCopies(file);
			result.add(copies);
		}
		return result;
	}
	
	public static List<FileCopy> listCopies(File projectStudentFolder) throws ConfigurationException, IOException, ServiceException{
		List<FileCopy> result = new ArrayList<FileCopy>();

		if(projectStudentFolder.getName().contains("-")){
			String matricula = projectStudentFolder.getName().substring(0,projectStudentFolder.getName().indexOf("-"));
			Student copier = Configuration.getInstance().getStudents().get(matricula);
			List<File> files = getFiles(projectStudentFolder, ".java");
			for (File file : files) {
				try {
					Student owner = readAuthor(file.toPath());
					if(owner.getMatricula() != null){
						if(!owner.getMatricula().equals(matricula)){
							result.add(new FileCopy(file,owner,copier));
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return result;
	}
	public static Student readAuthor(Path file) throws IOException{
		Student proprietario = new Student(null,null,null);

	    UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
	    
	    String matricula = AUTOR_MATRICULA;
	    ByteBuffer readBuffer = ByteBuffer.allocate(view.size(matricula));
	    view.read(matricula, readBuffer);
	    readBuffer.flip();
	    proprietario.setMatricula(new String(readBuffer.array(), "UTF-8"));

	    String nome = AUTOR_NOME;
	    readBuffer = ByteBuffer.allocate(view.size(nome));
	    view.read(nome, readBuffer);
	    readBuffer.flip();
	    proprietario.setNome(new String(readBuffer.array(), "UTF-8"));
	    
	    return proprietario;
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
		
	
	public static void main(String[] args) throws ConfigurationException, IOException, WrongDateHourFormatException, ServiceException {
		//Util.sendEmail("adalberto.cajueiro@gmail.com", "adalberto.cajueiro@gmail.com", "Teste", "conteudo", "adalberto.cajueiro@gmail.com", "acfcaju091401");
		//Util.send2();
		//System.out.println("Email enviado");
		//Util.loadConfig("conf/application.conf");
		//https://docs.google.com/spreadsheets/d//pubhtml
		//Map<String,Prova> provas = Util.loadSpreadsheetProvas("1mt0HNYUMgK_tT_P2Lz5PQjBP16F6Hn-UI8P21C0iPmI");
		//provas.forEach((id,p) -> System.out.println(p));
		Util.submissions(new File("D:\\trash2\\leda-upload\\2016.1\\R01-01"));
		
		Map<String,List<Submission>> allSubmissions = Util.allSubmissions();
		allSubmissions.forEach( (id,ls) -> {
			System.out.println("Atividade: " + id);
			ls.forEach( s -> {
				if(s.getArquivoSubmetido() != null){
					System.out.println(" - " + s.getArquivoSubmetido().getName());
				}
			} );
		});
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
