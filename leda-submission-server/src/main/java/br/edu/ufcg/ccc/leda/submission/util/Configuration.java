package br.edu.ufcg.ccc.leda.submission.util;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gdata.util.ServiceException;

import jxl.read.biff.BiffException;

public class Configuration {

	private Map<String, Student> students;
	private Map<String,Atividade> atividades;
	private List<Monitor> monitores;
	private ArrayList<String> ipsAutorizados = new ArrayList<String>();
	
	private static Configuration instance;
	
	private Configuration() throws ConfigurationException, IOException {
		try {
			students = Util.loadStudentLists();
			monitores = Util.loadSpreadsheetMonitor(Constants.ID_MONITORES_SHEET);
			atividades = Util.loadSpreadsheetsAtividades(monitores);
			ipsAutorizados.add("150.165.74");
			ipsAutorizados.add("150.165.54");
		} catch (BiffException e) {
			throw new ConfigurationException(e);
		} catch (ServiceException e) {
			e.printStackTrace();
			monitores = Util.loadSpreadsheetMonitorFromExcel();
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
			ipsAutorizados.add("150.165.74");
			ipsAutorizados.add("150.165.54");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			monitores = Util.loadSpreadsheetMonitorFromExcel();
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
			ipsAutorizados.add("150.165.74");
			ipsAutorizados.add("150.165.54");
		}
	}
	public static Configuration getInstance() throws ConfigurationException, IOException, ServiceException {
		if(instance == null){
			instance = new Configuration();
		}
		return instance;
	}
	
	
	public void reload() throws Exception{
		instance = new Configuration();
	}
	
	public Map<String, Student> getStudents() {
		return students;
	}
	public Map<String, Atividade> getRoteiros() {
		Map<String,Atividade> roteiros = new TreeMap();
		roteiros.putAll(atividades.values().stream().filter(ativ -> (ativ instanceof Roteiro && !(ativ instanceof Prova))).collect(Collectors.toMap(ativ -> ativ.getId(), ativ -> ativ)));

		return roteiros; 
	}
	public Map<String, Atividade> getProvas() {
		Map<String,Atividade> provas = new TreeMap(Util.comparatorProvas());
		provas.putAll(atividades.values().stream().filter(ativ -> (ativ instanceof Prova)).collect(Collectors.toMap(ativ -> ativ.getId(), ativ -> ativ)));
		return provas;
	}
	public ArrayList<String> getIpsAutorizados() {
		return ipsAutorizados;
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Aulas: <br>");
		atividades.values().stream().filter(a -> a instanceof Aula).sorted((r1,r2)-> r1.getId().compareTo(r2.getId()))
			.forEach(r -> result.append(r.toString() + "<br>"));
		result.append("Roteiros: <br>");
		
		atividades.values().stream().filter(a -> (a instanceof Roteiro) && !(a instanceof Prova)).sorted((r1,r2)-> r1.getId().compareTo(r2.getId()))
			.forEach(r -> result.append(r.toString() + "<br>"));

		result.append("Provas: <br>");
		getProvas().values().forEach(p -> result.append(p.toString() + "<br>"));
		
		return result.toString();
	}
	public List<Monitor> getMonitores() {
		return monitores;
	}
	public Map<String, Atividade> getAtividades() {
		return atividades;
	}
	
	
}
