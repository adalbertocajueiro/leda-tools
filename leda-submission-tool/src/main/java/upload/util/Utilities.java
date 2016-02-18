package upload.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Classe contendo m�todos utilitarios para uma aplicacao de upload.
 * @author betinho
 *
 */
public class Utilities {

	private static Vector<String> names = new Vector<String>();
	private static String fileName = "alunos.txt";
	private static String folderName = "lista";
	private static File submissionsFolder;
	//private static Properties prop = null;
	private static ResourceBundle rb = null;
	private static int quantidade;
	private static int salvos = 0;

	static{
		try {
			load();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void load() throws FileNotFoundException, IOException {
		if (rb == null) {
			ResourceBundle rb = ResourceBundle.getBundle("upload");
			//prop = new Properties();
			//prop.load(new FileReader("upload.properties"));
			folderName = rb.getString("folderName");
			fileName = rb.getString("fileName");
			//folderName = prop.getProperty("folderName");
			//fileName = prop.getProperty("fileName");
			createFolder(folderName);
			loadNames();
		}
	}

	private static void loadNames() throws FileNotFoundException, IOException {
		
		File file = new File(fileName);
		FileReader fr = new FileReader(file);
		//System.out.println(fileName);
		//InputStream is = Utilities.class.getResourceAsStream(fileName);
		//InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(fr);
		String name;
		while ((name = br.readLine()) != null) {
			names.addElement(name);
		}
		quantidade = names.size();
		br.close();
	}

	public static void removeName(String name) {
		names.remove(name);
		salvos++;
	}

	public static boolean containsName(String name) {
		return names.contains(name);
	}

	private static void createFolder(String folderName) {
		submissionsFolder = new File("target" + File.separator + folderName);
		if (!submissionsFolder.exists()) {
			submissionsFolder.mkdirs();
		}

		System.out.println("Pasta criada: " + submissionsFolder.getAbsolutePath());
	}

	public static Vector<String> getNames() {
		return names;
	}

	public static String getFolderName() {
		return folderName;
	}
	
	public static File getSubmissionsFolder() {
		return submissionsFolder;
	}
	
	public static int getQuantidade(){
		return quantidade; 
	}
	public static int getSalvos(){
		return salvos; 
	}

}
