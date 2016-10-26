package br.edu.ufcg.ccc.leda.submission.util;

import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


/**
 * Classe que representa a configuracao de um upload, contendo informacoes sobre semestre, turma, 
 * matricula do aluno, etc. 
 * 
 * @author Adalberto
 *
 */
public class StudentUploadConfiguration extends UploadConfiguration {

	private String matricula;
	private String ip;
	private Map<String,String> filesOwners;
	
	public StudentUploadConfiguration(String id, String semestre, String turma,
			String matricula,String ip, String files) {

		super(id,semestre,turma);
		
		this.matricula = matricula;
		this.ip = ip;
		Gson gson = new Gson();
		filesOwners = gson.fromJson(files, new TypeToken<Map<String,String>>(){}.getType());
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Map<String, String> getFilesOwners() {
		return filesOwners;
	}

	public void setFilesOwners(Map<String, String> filesOwners) {
		this.filesOwners = filesOwners;
	}
	
	
}
