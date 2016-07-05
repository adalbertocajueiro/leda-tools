package br.edu.ufcg.ccc.leda.submission.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

public class Validator {

	//realiza validacoes de uma submissao de professor 
	public static void validate(ProfessorUploadConfiguration config) throws ConfigurationException, IOException {
				
			Map<String,Student> studentMap = Configuration.getInstance().getStudents();
			Map<String,Roteiro> roteirosMap = Configuration.getInstance().getRoteiros();
			//System.out.println("STUDENT: " + config.getMatricula());
			//studentMap.keySet().forEach(key -> System.out.println("CADASTRADO: " + key));
			
			//se o professor/usuario informado nao esta autorizado 
			
			//se roteiro informado nao esta cadastrado
			
			
		}
		
	//realiza validacoes e retorna excecoes caso alguma delas nao seja satisfeita 
	public static void validate(StudentUploadConfiguration config) throws ConfigurationException, IOException, StudentException, RoteiroException {
			
		Map<String,Student> studentMap = Configuration.getInstance().getStudents();
		Map<String,Roteiro> roteirosMap = Configuration.getInstance().getRoteiros();
		//System.out.println("STUDENT: " + config.getMatricula());
		//studentMap.keySet().forEach(key -> System.out.println("CADASTRADO: " + key));
		
		//se o estudante informauma matricula invalida
		if(!studentMap.containsKey(config.getMatricula())){
			throw new StudentException("Estudante " + config.getMatricula() + " não cadastrado no sistema");
		}
		
		//se o estudante informa a turma errada da cadastrada no sistema
		Student student = studentMap.get(config.getMatricula());
		if(!student.getTurma().equals(config.getTurma())){
			throw new StudentException("Estudante " + config.getMatricula() + " não cadastrado na turma " + config.getTurma());
		}
		
		//se o roteiro informado nao existe
		if(!roteirosMap.containsKey(config.getRoteiro())){
			throw new RoteiroException("Roteiro " + config.getRoteiro() + " não cadastrado no sistema");
		}
		//se roteiro informado esta fora do prazo de recebimento
		Roteiro roteiro = roteirosMap.get(config.getRoteiro());
		GregorianCalendar dataAtual = new GregorianCalendar();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		if(roteiro.getDataHoraLimiteEnvioAtraso().before(dataAtual)){
			throw new RoteiroException("Limite de envio ro roteiro " + config.getRoteiro() + " terminou em " + formatter.format(dataAtual.getTime()));
		}
	}
}
