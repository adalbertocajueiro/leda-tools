package green.atm.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import green.atm.extrato.TipoTransacao;
import green.atm.extrato.Transacao;

public class Configuration {
	private static HashMap<String,ModalidadeTransacao> modalidades;
	private static HashMap<String,TipoTransacao> tiposTransacao;
	private static List<String> funcionarios;
	private static OrderedLinkedList<Transacao> transacoes;
	private static Configuration instance;
	
	
	static {
		File folder = new File(Util.FOLDER_CONFIG);
		File file = new File(folder,Util.MODALIDADES_FILE_NAME);
		File fileTiposTransacao = new File(folder,Util.TIPO_TRANSACAO_FILE_NAME);
		File fileTransacoes = new File(folder,Util.TRANSACOES_FILE_NAME);
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			modalidades = new Gson().fromJson(br, HashMap.class);
			
			br = new BufferedReader(new FileReader(fileTiposTransacao));
			tiposTransacao = new Gson().fromJson(br, (new TypeToken<HashMap<String,TipoTransacao>>(){}).getType() );
			
			br = new BufferedReader(new FileReader(fileTransacoes));
			transacoes = new Gson().fromJson(br, (new TypeToken<OrderedLinkedList<Transacao>>(){}).getType() );
			
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
			tiposTransacao = new HashMap<String,TipoTransacao>();
			transacoes = new OrderedLinkedList<Transacao>();
		} catch (IOException e) {
			e.printStackTrace();
			modalidades = new HashMap<String,ModalidadeTransacao>();
			funcionarios = new ArrayList<String>();
			tiposTransacao = new HashMap<String,TipoTransacao>();
			transacoes = new OrderedLinkedList<Transacao>();
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
	public static HashMap<String, TipoTransacao> getTiposTransacao() {
		return tiposTransacao;
	}
	public static OrderedLinkedList<Transacao> getTransacoes() {
		return transacoes;
	}
	
}
