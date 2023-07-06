package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

public abstract class Constants {

	public static final int HORAS_LIMITE_NORMAL = 12;
	public static final int HORAS_LIMITE_ATRASO = 48;
	
	public static final String DEFAULT_CONFIG_FOLDER_NAME = "conf";

	public static final String SUBMISSIONS_FOLDER_NAME = "subs";
	public static final String ANALYSIS_FOLDER_NAME = "analysis";
	public static final String REPORTS_FOLDER_NAME = "public/reports";
	public static final String ROTEIROS_FOLDER_NAME = "roteiros";
	public static final String PROVAS_FOLDER_NAME = "provas";

	public static final String EXCEL_FILE_NOTAS_FINAIS_NAME = "NotasFinaisSemestre";
	public static final String EXCEL_FILE_FALTAS_NAME = "FaltasSemestre";
	public static final String EXCEL_SENHAS_FILE_NAME = "Senhas.xlsx";
	public static final String EXCEL_CORRETORES_FILE_NAME = "Monitores.xlsx";
	public static final String EXCEL_ATIVIDADES_FILE_NAME = "Cronogramas.xlsx";
	public static final String EXCEL_EDA_T1_FILE_NAME = "NotasEDA-T1.xlsx";
	public static final String EXCEL_EDA_T2_FILE_NAME = "NotasEDA-T2.xlsx";

	public static File DEFAULT_CONFIG_FOLDER;
	public static File UPLOAD_FOLDER;
	public static File ROTEIROS_FOLDER;
	public static File PROVAS_FOLDER;
	public static File CURRENT_SEMESTER_FOLDER;
	public static File ANALYSIS_FOLDER;
	public static Properties PLAGIARISM_PROPERTIES;
	
	public static String UPLOAD_FOLDER_NAME;
	public static String CURRENT_SEMESTER;
	public static String MAVEN_HOME_FOLDER;
	//public static int QUANTIDADE_PROVAS;
	//public static int QUANTIDADE_ROTEIROS;
	//public static List<String> activitySheetIds;
	public static List<String> authorizedIPs;
	//public static List<String> edaSheetIds;
	
	public static double PESO_TESTES;
	public static double PESO_DESIGN;
	
	//public static final String MAVEN_OUTPUT_FILE = "maven-output.txt";

	public static final Pattern PATTERN_AULA = Pattern.compile("A[0-9]{2}-[0-9][0-9[X]]");
	public static final Pattern PATTERN_ROTEIRO = Pattern.compile("R[0-9]{2}-[0-9][0-9[X]]");
	public static final Pattern PATTERN_ROTEIRO_REVISAO = Pattern.compile("RR[0-9]{1}-[0-9][0-9[X]]");
	public static final Pattern PATTERN_ROTEIRO_ESPECIAL = Pattern.compile("RE[0-9]{1,2}-[0-9][0-9]");
	public static final Pattern PATTERN_PROVA = Pattern.compile("P[PRF][1-9]-[0-9][0-9[X]]");
	public static final Pattern PATTERN_PROVA_PRATICA = Pattern.compile("P[P][1-9]-[0-9][0-9[X]]");
	public static final Pattern PATTERN_PROVA_REPOSICAO = Pattern.compile("P[R][1-9]-[0-9][0-9[X]]");
	public static final Pattern PATTERN_PROVA_FINAL = Pattern.compile("P[F][1-9]-[0-9][0-9[X]]");
	public static final Pattern PATTERN_MATRICULA = Pattern.compile("1[0-9]{8}");

	public static final Pattern PATTERN_DATE_TIME = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");
	public static final String AUTOR_MATRICULA = "autor.matricula";
	public static final String AUTOR_NOME = "autor.nome";

	//public static String ID_MONITORES_SHEET;

	public static final String CODIGO_LEDA = "1411306";

	static{
		try {
			Properties prop = Util.loadProperties();
			PLAGIARISM_PROPERTIES = Util.loadPlagiarismProperties();
			UPLOAD_FOLDER_NAME = prop.getProperty("upload.folder");
			CURRENT_SEMESTER = prop.getProperty("semestre.letivo");
			MAVEN_HOME_FOLDER = prop.getProperty("mavenHomeFolder");
			//QUANTIDADE_PROVAS = Integer.valueOf(prop.getProperty("quantidadeProvas"));
			//QUANTIDADE_ROTEIROS = Integer.valueOf(prop.getProperty("quantidadeRoteiros"));
			//ID_MONITORES_SHEET = prop.getProperty("idSheetMonitores");
			PESO_TESTES = Double.parseDouble(prop.getProperty("pesoTestes","0.4"));
			PESO_DESIGN = Double.parseDouble(prop.getProperty("pesoDesign","0.6"));
			
			//activitySheetIds = new ArrayList<String>();
			authorizedIPs = new ArrayList<String>();
			//edaSheetIds = new ArrayList<String>();
			
			//String ids = prop.getProperty("activitySheetIds");
			//for (String id : ids.split(",")) {
			//	activitySheetIds.add(id.trim());
			//}
			
			String ips = prop.getProperty("authorizedIPs");
			for (String ip : ips.split(",")) {
				authorizedIPs.add(ip.trim());
			}
			
			//String edaIds = prop.getProperty("edaSheetIds");
			//for (String id : edaIds.split(",")) {
			//	edaSheetIds.add(id.trim());
			//}
			
			DEFAULT_CONFIG_FOLDER = new File(Constants.DEFAULT_CONFIG_FOLDER_NAME);
    		if(!DEFAULT_CONFIG_FOLDER.exists()){
    			throw new FileNotFoundException("Missing config folder: " + DEFAULT_CONFIG_FOLDER.getAbsolutePath());
    		}
    		UPLOAD_FOLDER = new File(UPLOAD_FOLDER_NAME);
    		if(!UPLOAD_FOLDER.exists()){
    			UPLOAD_FOLDER.mkdirs();
    		}
    		CURRENT_SEMESTER_FOLDER = new File(UPLOAD_FOLDER, Constants.CURRENT_SEMESTER);
    		if(!CURRENT_SEMESTER_FOLDER.exists()){
    			CURRENT_SEMESTER_FOLDER.mkdirs();
    		}
    		ROTEIROS_FOLDER = new File(CURRENT_SEMESTER_FOLDER,ROTEIROS_FOLDER_NAME);
			if(!ROTEIROS_FOLDER.exists()){
				ROTEIROS_FOLDER.mkdirs();
			}
    		PROVAS_FOLDER = new File(CURRENT_SEMESTER_FOLDER,PROVAS_FOLDER_NAME);
			if(!PROVAS_FOLDER.exists()){
				PROVAS_FOLDER.mkdirs();
			}
			ANALYSIS_FOLDER = new File(CURRENT_SEMESTER_FOLDER, Constants.ANALYSIS_FOLDER_NAME);
    		if(!ANALYSIS_FOLDER.exists()){
    			ANALYSIS_FOLDER.mkdir();
    		}
		} catch (IOException e) {
			System.out.println("Properties not loaded. system will exit");
			e.printStackTrace();
			System.exit(0);
		}
	}

}
