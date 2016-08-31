package br.edu.ufcg.ccc.leda.submission.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gdata.util.ServiceException;

public class Validator {

	public static void validateDownload(String roteiro) throws RoteiroException, ConfigurationException, IOException, ServiceException{
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
	
	public static void validateProvaDownload(String provaId,String matricula) throws RoteiroException, ConfigurationException, IOException, ServiceException{
		Map<String,Student> studentsMap = Configuration.getInstance().getStudents();
		Student requester = studentsMap.get(matricula);
		String turma = provaId.substring(4);
		if(requester == null){
			throw new RoteiroException("Matricula " + matricula + " nao cadastrada");
		}else if(!turma.equals(requester.getTurma())){
			throw new RoteiroException("Estudante " + matricula + " nao pode fazer download da prova da turma " + turma);			
		}
				
		Map<String,Prova> provasMap = Configuration.getInstance().getProvas();
		Prova prova = provasMap.get(provaId);
		if(prova == null){
			throw new RoteiroException("Prova " + provaId + " nao cadastrada");
		}
		GregorianCalendar dataAtual = new GregorianCalendar();
		if(dataAtual.before(prova.getDataHoraLiberacao()) || 
				dataAtual.after(prova.getDataHoraLimiteEnvio())){
			throw new RoteiroException("Prova " + provaId + " disponivel para download apenas entre " +
				Util.formatDate(prova.getDataHoraLiberacao()) + " e " + Util.formatDate(prova.getDataHoraLimiteEnvio())+ ".\n"
				+ "A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
		}
		
		//se o arquivo for nulo (nao foi cadastrado ainda) ou nao existe fisicamente
		if(prova.getArquivoAmbiente() == null){
			throw new RoteiroException("Arquivo de ambiente para  a prova " + provaId + " nao cadastrado");
		} else if(!prova.getArquivoAmbiente().exists()){
			throw new RoteiroException("Arquivo de ambiente para  a prova" + provaId + " nao encontrado no servidor: " + prova.getArquivoAmbiente().getAbsolutePath());
		}
	}
	
	//realiza validacoes de uma submissao de professor 
	public static void validate(ProfessorUploadConfiguration config) throws ConfigurationException, IOException, RoteiroException, ServiceException {
				
			Map<String,Roteiro> roteirosMap = Configuration.getInstance().getRoteiros();
			//System.out.println("STUDENT: " + config.getMatricula());
			//studentMap.keySet().forEach(key -> System.out.println("CADASTRADO: " + key));
			
			//se o professor/usuario informado nao esta autorizado 
			
			//se o id do roteiro for de uma prova
			if(config.getRoteiro().startsWith("P")){
				//pega as datas da prova do mapeamento das provas
				
			}else{
				//se roteiro informado nao esta cadastrado
				Roteiro roteiro = roteirosMap.get(config.getRoteiro());
				if(roteiro == null){
					throw new RoteiroException("Roteiro " + config.getRoteiro() + " nao cadastrado.");
				}
			}
			
		}
		
	//realiza validacoes e retorna excecoes caso alguma delas nao seja satisfeita 
	public static void validate(StudentUploadConfiguration config) throws ConfigurationException, IOException, StudentException, RoteiroException, ServiceException {
			
		Map<String,Student> studentMap = Configuration.getInstance().getStudents();
		Map<String,Roteiro> roteirosMap = Configuration.getInstance().getRoteiros();
		Map<String,Prova> provasMap = Configuration.getInstance().getProvas();
		//System.out.println("STUDENT: " + config.getMatricula());
		//studentMap.keySet().forEach(key -> System.out.println("CADASTRADO: " + key));
		
		//se o estudante informa uma matricula invalida
		if(!studentMap.containsKey(config.getMatricula())){
			throw new StudentException("Estudante " + config.getMatricula() + " não cadastrado no sistema");
		}
		
		//se o estudante informa a turma errada da cadastrada no sistema
		Student student = studentMap.get(config.getMatricula());
		if(!student.getTurma().equals(config.getTurma())){
			throw new StudentException("Estudante " + config.getMatricula() + " não cadastrado na turma " + config.getTurma());
		}
		
		//se o id do roteiro for PPX, entao valida diretamente nas provas. 
		String id = config.getRoteiro();
		
		if(id.startsWith("PP")){ //eh prova
			//se o aluno submete de um IP invalido
			String ipCaller = config.getIp();
			System.out.println("Validator.IP: " + ipCaller);
			ArrayList<String> ips = Configuration.getInstance().getIpsAutorizados();
			Stream<String> ipStream = ips.stream().filter(ip -> ipCaller.startsWith(ip));
			if(ipStream.count() == 0){ //nao esta nos ips autorizados
				throw new RoteiroException("Envio a partir de IP nao autorizado: " + ipCaller + ". Envios sao possivels apenas a partir de IPs oriundos de: " + Arrays.toString(ips.toArray()));
			}
			ipStream.close();
			//se a prova informado nao existe
			if(!provasMap.containsKey(id)){
				throw new RoteiroException("Prova " + id + " não cadastrada no sistema");
			}
			//se prova informado esta fora do prazo de recebimento
			Prova prova = provasMap.get(id);
			GregorianCalendar dataAtual = new GregorianCalendar();
			if(prova.getDataHoraLiberacao().after(dataAtual) ||
					prova.getDataHoraLimiteEnvio().before(dataAtual)){
	
				throw new RoteiroException("Envio da prova " + id + " possivel apenas entre " + Util.formatDate(prova.getDataHoraLiberacao())
						+ " e " + Util.formatDate(prova.getDataHoraLimiteEnvio()) + ". A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
			}
		} else{ //eh roteiro
					
			//se o roteiro informado nao existe
			if(!roteirosMap.containsKey(id)){
				throw new RoteiroException("Roteiro " + id + " não cadastrado no sistema");
			}
			
			
			//se roteiro informado esta fora do prazo de recebimento
			Roteiro roteiro = roteirosMap.get(id);
			GregorianCalendar dataAtual = new GregorianCalendar();
			if(roteiro.getDataHoraLiberacao().after(dataAtual) ||
					roteiro.getDataHoraLimiteEnvioAtraso().before(dataAtual)){
	
				throw new RoteiroException("Envio do roteiro " + id + " possivel apenas entre " + Util.formatDate(roteiro.getDataHoraLiberacao())
						+ " e " + Util.formatDate(roteiro.getDataHoraLimiteEnvioAtraso()) + ". A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
			}
		}
	}
}
