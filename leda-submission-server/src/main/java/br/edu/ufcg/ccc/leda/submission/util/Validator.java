package br.edu.ufcg.ccc.leda.submission.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

public class Validator {

	public static void validateDownload(String roteiro) throws RoteiroException, ConfigurationException, IOException{
		Map<String,Roteiro> roteirosMap = Configuration.getInstance().getRoteiros();
		Roteiro rot = roteirosMap.get(roteiro);
		if(rot == null){
			throw new RoteiroException("Roteiro " + roteiro + " nao cadastrado");
		}
		GregorianCalendar dataAtual = new GregorianCalendar();
		if(dataAtual.before(rot.getDataHoraLiberacao()) || 
				dataAtual.after(rot.getDataHoraLimiteEnvioAtraso())){
			throw new RoteiroException("Roteiro " + roteiro + " disponivel para download apenas entre " +
				Util.formatDate(rot.getDataHoraLiberacao()) + " e " + Util.formatDate(rot.getDataHoraLimiteEnvioAtraso())+ ".\n"
				+ "A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
		}
		
		//se o arquivo for nulo (nao foi cadastrado ainda) ou nao existe fisicamente
		if(rot.getArquivoAmbiente() == null){
			throw new RoteiroException("Arquivo de ambiente para  o roteiro " + roteiro + " nao cadastrado");
		} else if(!rot.getArquivoAmbiente().exists()){
			throw new RoteiroException("Arquivo de ambiente para  o roteiro " + roteiro + " nao encontrado no servidor: " + rot.getArquivoAmbiente().getAbsolutePath());
		}
	}
	
	//realiza validacoes de uma submissao de professor 
	public static void validate(ProfessorUploadConfiguration config) throws ConfigurationException, IOException, RoteiroException {
				
			Map<String,Student> studentMap = Configuration.getInstance().getStudents();
			Map<String,Roteiro> roteirosMap = Configuration.getInstance().getRoteiros();
			//System.out.println("STUDENT: " + config.getMatricula());
			//studentMap.keySet().forEach(key -> System.out.println("CADASTRADO: " + key));
			
			//se o professor/usuario informado nao esta autorizado 
			
			//se roteiro informado nao esta cadastrado
			Roteiro roteiro = roteirosMap.get(config.getRoteiro());
			if(roteiro == null){
				throw new RoteiroException("Roteiro " + config.getRoteiro() + " nao cadastrado.");
			}
			
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