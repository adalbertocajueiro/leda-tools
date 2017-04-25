package br.edu.ufcg.ccc.leda.submission.util;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gdata.util.ServiceException;

import jxl.read.biff.BiffException;

public class Configuration {

	private Map<String, Student> students;
	private Map<String,Atividade> atividades;
	private List<Corretor> monitores;
	private ArrayList<String> ipsAutorizados;
	
	private static Configuration instance;
	
	private Configuration() throws ConfigurationException, IOException {
		if(ipsAutorizados == null){
			ipsAutorizados = new ArrayList<String>();
		}
		try {
			students = Util.loadStudentLists();
			monitores = Util.loadSpreadsheetMonitor();
			Util.loadSpreadsheetSenhasFromExcel(monitores);
			atividades = Util.loadSpreadsheetsAtividades(monitores);
			ipsAutorizados.addAll(Constants.authorizedIPs);
		} catch (BiffException e) {
			throw new ConfigurationException(e);
		} catch (ServiceException e) {
			e.printStackTrace();
			monitores = Util.loadSpreadsheetMonitorFromExcel();
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
			ipsAutorizados.addAll(Constants.authorizedIPs);
		} catch (ConnectException e) {
			e.printStackTrace();
			monitores = Util.loadSpreadsheetMonitorFromExcel();
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
			ipsAutorizados.addAll(Constants.authorizedIPs);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			monitores = Util.loadSpreadsheetMonitorFromExcel();
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
			ipsAutorizados.addAll(Constants.authorizedIPs);
		}
	}


	private Configuration(ArrayList<String> ips) throws ConfigurationException, IOException {
		ipsAutorizados = ips;
		try {
			students = Util.loadStudentLists();
			monitores = Util.loadSpreadsheetMonitor();
			Util.loadSpreadsheetSenhasFromExcel(monitores);
			atividades = Util.loadSpreadsheetsAtividades(monitores);
		} catch (BiffException e) {
			throw new ConfigurationException(e);
		} catch (ServiceException e) {
			e.printStackTrace();
			monitores = Util.loadSpreadsheetMonitorFromExcel();
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
		} catch (ConnectException e) {
			e.printStackTrace();
			monitores = Util.loadSpreadsheetMonitorFromExcel();
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			monitores = Util.loadSpreadsheetMonitorFromExcel();
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
		}
	}
	public static Configuration getInstance() throws ConfigurationException, IOException, ServiceException {
		if(instance == null){
			instance = new Configuration();
		}
		return instance;
	}
	
	
	public void reload(boolean reloadIPs) throws Exception{
		if(reloadIPs){
			instance = new Configuration();
		}else{
			instance = new Configuration(instance.ipsAutorizados);
		}
	}
	
	public Map<String, Student> getStudents() {
		return students;
	}
	public List<Atividade> getRoteiros() {
		/*Map<String,Atividade> roteiros = new TreeMap();
		roteiros.putAll(atividades.values().stream()
				.filter(ativ -> (ativ instanceof Roteiro && !(ativ instanceof Prova)))
				.sorted((ativ1,ativ2) -> ativ1.getDataHora().compareTo(ativ2.getDataHora()))
				.collect(Collectors.toMap(ativ -> ativ.getId(), ativ -> ativ)));
		
		return roteiros; */
		
		return atividades.values().stream()
				.filter(a -> !(a instanceof RoteiroEspecial))
				.filter(ativ -> (ativ instanceof Roteiro && !(ativ instanceof Prova)))
				.sorted((ativ1,ativ2) -> ativ1.getDataHora().compareTo(ativ2.getDataHora()))
				.collect(Collectors.toList());
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
	public List<Corretor> getMonitores() {
		return monitores;
	}
	//exclui os roteiros especiais que nao devem aparecer no cronograma
	public Map<String, Atividade> getAtividades() {
		return atividades.values().stream()
				.filter( a -> !(a instanceof RoteiroEspecial))
				.collect(Collectors.toMap(a -> a.getId(), a -> a));
	}
	//retorna uma lista dos roteiros especiais
	public List<Atividade> getRoteirosEspeciais() {
		return atividades.values().stream()
				.filter( a -> a instanceof RoteiroEspecial)
				.collect(Collectors.toList());
	}
	
	
}
