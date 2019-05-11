package green.atm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;

public class Configuration {
	private static HashMap<String,ModalidadeTransacao> modalidades;
	private static List<String> funcionarios;
	private static Configuration instance;
	
	static {
		File folder = new File(Util.FOLDER_CONFIG);
		File file = new File(folder,Util.MODALIDADES_FILE_NAME);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			modalidades = new Gson().fromJson(br, HashMap.class);
			file = new File(folder, Util.FUNCIONARIOS_FILE_NAME);
			br = new BufferedReader(new FileReader(file));
			funcionarios = new ArrayList<String>();
			String line = "";
			while(( line = br.readLine())!= null) {
				funcionarios.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			modalidades = new HashMap<String,ModalidadeTransacao>();
			funcionarios = new ArrayList<String>();
		} catch (IOException e) {
			e.printStackTrace();
			modalidades = new HashMap<String,ModalidadeTransacao>();
			funcionarios = new ArrayList<String>();
		}
		
	}
	private Configuration() {
		
	}
	public static Configuration getInstance() {
		if(instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	public static HashMap<String, ModalidadeTransacao> getModalidades() {
		return modalidades;
	}
	public static List<String> getFuncionarios() {
		return funcionarios;
	}
	
}
