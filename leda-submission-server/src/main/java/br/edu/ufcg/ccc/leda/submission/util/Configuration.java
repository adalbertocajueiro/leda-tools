package br.edu.ufcg.ccc.leda.submission.util;

import java.io.IOException;
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
	//private Map<String,Roteiro> roteiros;
	//private Map<String,Prova> provas;
	private List<Monitor> monitores;
	private ArrayList<String> ipsAutorizados = new ArrayList<String>();
	//private static final String ID_ROTEIROS_SHEET = "19npZPI7Y1jyk1jxNKHgUZkYTk3hMT_vdmHunQS-tOhA";
	//private static final String ID_PROVAS_SHEET = "1mt0HNYUMgK_tT_P2Lz5PQjBP16F6Hn-UI8P21C0iPmI";
	public static final String ID_ATIVIDADES_SHEET = "15rxyKxDJ4-dSIfdh1ZGZvpL7femQG27P_ESTeSseJGA";
	public static final String ID_MONITORES_SHEET = "15T_KSFA1ABUvZV_p0IVjcxa90yBJUw0794p7GO8OHEA";
	
	private static Configuration instance;
	
	private Configuration() throws ConfigurationException, IOException {
		try {
			students = Util.loadStudentLists();
			monitores = Util.loadSpreadsheetMonitor(ID_MONITORES_SHEET);
			atividades = Util.loadSpreadsheetAtividades(ID_ATIVIDADES_SHEET,monitores);
			//roteiros = new TreeMap<String,Roteiro>(Util.loadSpreadsheetRoteiros(ID_ROTEIROS_SHEET));
			//provas = new TreeMap<String, Prova> (Util.comparatorProvas());
			//provas.putAll(Util.loadSpreadsheetProvas(ID_PROVAS_SHEET));
			//provas = Util.loadSpreadsheetProvas(ID_PROVAS_SHEET);
			ipsAutorizados.add("150.165.74");
			ipsAutorizados.add("150.165.54");
		} catch (BiffException e) {
			throw new ConfigurationException(e);
		} catch (ServiceException e) {
			e.printStackTrace();
			//atividades = new TreeMap<String,Atividade>();
			if(monitores == null){
				monitores = Util.loadSpreadsheetMonitorFromExcel();
			}
			atividades = Util.loadSpreadsheetAtividadeFromExcel(monitores);
			ipsAutorizados.add("150.165.74");
			ipsAutorizados.add("150.165.54");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			//atividades = new TreeMap<String,Atividade>();
			if(monitores == null){
				monitores = Util.loadSpreadsheetMonitorFromExcel();
			}
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
		atividades.values().stream().filter(a -> a instanceof Prova).sorted((p1,p2)-> p1.getId().compareTo(p2.getId()))
			.forEach(p -> result.append(p.toString() + "<br>"));
		
		return result.toString();
	}
	public List<Monitor> getMonitores() {
		return monitores;
	}
	public Map<String, Atividade> getAtividades() {
		return atividades;
	}
	
	
}
