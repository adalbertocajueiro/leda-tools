package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

public abstract class Constants {

	public static final int HORAS_LIMITE_NORMAL = 12;
	public static final int HORAS_LIMITE_ATRASO = 48;
	
	public static final int TURMAS = 2;

	public static final String DEFAULT_CONFIG_FOLDER_NAME = "conf";
	public static final String EXCEL_FILE_ROTEIRO = "Roteiros.xlsx";
	public static final String EXCEL_FILE_PROVA = "Provas.xlsx";
	public static final String JSON_FILE_ROTEIRO = "Roteiros.json";
	public static final String JSON_FILE_PROVA = "Provas.json";

	public static final String SUBMISSIONS_FOLDER_NAME = "subs";
	public static final String REPORTS_FOLDER_NAME = "public/reports";
	public static final String ROTEIROS_FOLDER_NAME = "roteiros";
	public static final String PROVAS_FOLDER_NAME = "provas";

	public static final String EXCEL_FILE_NOTAS_FINAIS_NAME = "NotasFinaisSemestre";
	public static final String EXCEL_FILE_FALTAS_NAME = "FaltasSemestre";
	public static File DEFAULT_CONFIG_FOLDER;
	public static File UPLOAD_FOLDER;
	public static File ROTEIROS_FOLDER;
	public static File PROVAS_FOLDER;
	public static File CURRENT_SEMESTER_FOLDER;
	
	public static String UPLOAD_FOLDER_NAME;
	public static String CURRENT_SEMESTER;
	public static String MAVEN_HOME_FOLDER;
	public static int QUANTIDADE_PROVAS;
	public static int QUANTIDADE_ROTEIROS;
	
	public static final String MAVEN_OUTPUT_FILE = "maven-output.txt";
	public static final String GENERATED_REPORT_FILE = "target/generated-report.html";

	public static final Pattern PATTERN_AULA = Pattern.compile("A[0-9]{2}-[0-9][0-9[X]]");
	public static final Pattern PATTERN_ROTEIRO = Pattern.compile("R[0-9]{2}-[0-9][0-9[X]]");
	public static final Pattern PATTERN_ROTEIRO_REVISAO = Pattern.compile("RR[0-9]{1}-[0-9][0-9[X]]");
	public static final Pattern PATTERN_PROVA = Pattern.compile("P[PRF][1-9]-[0-9][0-9[X]]");
	public static final Pattern PATTERN_PROVA_PRATICA = Pattern.compile("P[P][1-9]-[0-9][0-9[X]]");
	public static final Pattern PATTERN_PROVA_REPOSICAO = Pattern.compile("P[R][1-9]-[0-9][0-9[X]]");
	public static final Pattern PATTERN_PROVA_FINAL = Pattern.compile("P[F][1-9]-[0-9][0-9[X]]");

	public static final Pattern PATTERN_DATE_TIME = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}");
	public static final String AUTOR_MATRICULA = "autor.matricula";
	public static final String AUTOR_NOME = "autor.nome";

	public static final String ID_MONITORES_SHEET = "15T_KSFA1ABUvZV_p0IVjcxa90yBJUw0794p7GO8OHEA";
	public static final String ID_ATIVIDADES_SHEET_T1 = "15rxyKxDJ4-dSIfdh1ZGZvpL7femQG27P_ESTeSseJGA";
	public static final String ID_ATIVIDADES_SHEET_T2 = "1S7Ru2RFei-0lgEHTn2L6XRYajiX8Se4yryuw8sR404A";

	public static final String ID_MEDIASEDA_SHEET_T2 = "1LEccFlOjV4o3FsJEeOYFtqzaJOHHd9q4gYurcWx95cU";
	public static final String ID_MEDIASEDA_SHEET_T1 = "1Qwpele7sF0WAb5GDqKiEq1MMUT9Ij4qJlEw3P5ey4fM";

	static{
		try {
			Properties prop = Util.loadProperties();
			UPLOAD_FOLDER_NAME = prop.getProperty("upload.folder");
			CURRENT_SEMESTER = prop.getProperty("semestre.letivo");
			MAVEN_HOME_FOLDER = prop.getProperty("mavenHomeFolder");
			QUANTIDADE_PROVAS = Integer.valueOf(prop.getProperty("quantidadeProvas"));
			QUANTIDADE_ROTEIROS = Integer.valueOf(prop.getProperty("quantidadeRoteiros"));
			
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
			//System.out.println("Property UPLOAD_FOLDER: " + UPLOAD_FOLDER);
			//System.out.println("Property CURRENT_SEMESTER: " + CURRENT_SEMESTER);
		} catch (IOException e) {
			System.out.println("Properties not loaded. system will exit");
			e.printStackTrace();
			System.exit(0);
		}
	}

}
