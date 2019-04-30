package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.google.gdata.util.ServiceException;

public class Validator {

	public static void validateDownloadAmbiente(String id, String matricula)
			throws AtividadeException, ConfigurationException, IOException, ServiceException {

		Map<String, Student> studentsMap = Configuration.getInstance().getStudents();
		Student requester = studentsMap.get(matricula);
		String turma = id.substring(4);
		if (requester == null) {
			List<Corretor> corretores = Configuration.getInstance().getMonitores();
			if (!corretores.stream().filter(c -> c.getMatricula().equals(matricula)).findFirst().isPresent()) {

				throw new AtividadeException("Matricula " + matricula + " nao cadastrada");
			}
		} else if (!turma.equals(requester.getTurma())) {
			throw new AtividadeException(
					"Estudante " + matricula + " nao pode fazer download de ambientes da turma " + turma);
		}

		Map<String, Atividade> atividades = Configuration.getInstance().getAtividades();
		Atividade atividade = atividades.get(id);
		if (atividade == null) {
			throw new AtividadeException("Atividade (roteiro ou prova)" + id + " nao cadastrada");
		}
		GregorianCalendar dataAtual = new GregorianCalendar();
		if (atividade instanceof Prova) {
			// se for prova de reposicao e o aluno tiver feito o download da prova
			// correspondente entao neo permite
			// ele fazer download
			// se id for de uma prova de revisao tem que ver na prova correspondente a ela
			if (Constants.PATTERN_PROVA_REPOSICAO.matcher(id).matches()) {
				String idProvaPratica = "PP" + id.charAt(2) + id.substring(3);
				List<String> fizeramDownload = Util.alunosDownload(idProvaPratica);
				if (fizeramDownload.contains(matricula)) {
					throw new AtividadeException("Ja existe um registro de download da prova " + idProvaPratica
							+ " para o estudante " + matricula + ". Download da prova de reposicao nao permitido!!! ");
				}
			}

			if (dataAtual.before(atividade.getDataHora())
					|| dataAtual.after(((Prova) atividade).getDataHoraLimiteEnvioNormal())) {
				throw new AtividadeException("Prova " + id + " disponivel para download apenas entre "
						+ Util.formatDate(atividade.getDataHora()) + " e "
						+ Util.formatDate(((Prova) atividade).getDataHoraLimiteEnvioNormal()) + ".\n"
						+ "A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
			}
		} else if (atividade instanceof Roteiro && !(atividade instanceof RoteiroRevisao)) {
			// restricao para Adrews fazer download antecipado
			/*
			 * if(matricula.equals("116110125") && !(atividade instanceof Prova)){
			 * GregorianCalendar dataLiberacao = (GregorianCalendar)
			 * atividade.getDataHora().clone(); dataLiberacao.add(Calendar.HOUR_OF_DAY, -3);
			 * if(dataAtual.before(dataLiberacao)){ throw new AtividadeException("Roteiro "
			 * + id + " disponivel para download apenas a partir de " +
			 * Util.formatDate(dataLiberacao) + ".\n" + "A hora atual do servidor eh: " +
			 * Util.formatDate(new GregorianCalendar())); } }else{
			 */
			if (dataAtual.before(atividade.getDataHora())) {
				throw new AtividadeException("Roteiro " + id + " disponivel para download apenas a partir de "
						+ Util.formatDate(atividade.getDataHora()) + ".\n" + "A hora atual do servidor eh: "
						+ Util.formatDate(new GregorianCalendar()));
			}
			// }
		}

		// se o arquivo for nulo (nao foi cadastrado ainda) ou nao existe fisicamente
		if (((Roteiro) atividade).getArquivoAmbiente() == null) {
			throw new AtividadeException(
					"Arquivo de ambiente para  atividade (roteiro ou prova) " + id + " nao cadastrado");
		} else if (!((Roteiro) atividade).getArquivoAmbiente().exists()) {
			throw new AtividadeException("Arquivo de ambiente para  atividade (roteiro ou prova) " + id
					+ " nao encontrado no servidor: " + ((Roteiro) atividade).getArquivoAmbiente().getAbsolutePath());
		}
	}

	@Deprecated
	public static void validateDownload(String id, String matricula)
			throws AtividadeException, ConfigurationException, IOException, ServiceException {

		Map<String, Student> studentsMap = Configuration.getInstance().getStudents();
		Student requester = studentsMap.get(matricula);
		String turma = id.substring(4);
		if (requester == null) {
			throw new AtividadeException("Matricula " + matricula + " nao cadastrada");
		} else if (!turma.equals(requester.getTurma())) {
			throw new AtividadeException(
					"Estudante " + matricula + " nao pode fazer download de ambientes da turma " + turma);
		}

		Map<String, Roteiro> roteirosMap = null; // Configuration.getInstance().getRoteiros();
		Roteiro rot = roteirosMap.get(id);
		if (rot == null) {
			throw new AtividadeException("Roteiro " + id + " nao cadastrado");
		}
		GregorianCalendar dataAtual = new GregorianCalendar();
		if (dataAtual.before(rot.getDataHora()) || dataAtual.after(rot.getDataHoraLimiteEnvioAtraso())) {
			throw new AtividadeException("Roteiro " + id + " disponivel para download apenas entre "
					+ Util.formatDate(rot.getDataHora()) + " e " + Util.formatDate(rot.getDataHoraLimiteEnvioAtraso())
					+ ".\n" + "A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
		}

		// se o arquivo for nulo (nao foi cadastrado ainda) ou nao existe fisicamente
		if (rot.getArquivoAmbiente() == null) {
			throw new AtividadeException("Arquivo de ambiente para  o roteiro " + id + " nao cadastrado");
		} else if (!rot.getArquivoAmbiente().exists()) {
			throw new AtividadeException("Arquivo de ambiente para  o roteiro " + id + " nao encontrado no servidor: "
					+ rot.getArquivoAmbiente().getAbsolutePath());
		}
	}

	@Deprecated
	public static void validateProvaDownload(String id, String matricula)
			throws AtividadeException, ConfigurationException, IOException, ServiceException {
		Map<String, Student> studentsMap = Configuration.getInstance().getStudents();
		Student requester = studentsMap.get(matricula);
		String turma = id.substring(4);
		if (requester == null) {
			throw new AtividadeException("Matricula " + matricula + " nao cadastrada");
		} else if (!turma.equals(requester.getTurma())) {
			throw new AtividadeException(
					"Estudante " + matricula + " nao pode fazer download da prova da turma " + turma);
		}

		Map<String, Prova> provasMap = null; // Configuration.getInstance().getProvas();
		Prova prova = provasMap.get(id);
		if (prova == null) {
			throw new AtividadeException("Prova " + id + " nao cadastrada");
		}
		GregorianCalendar dataAtual = new GregorianCalendar();
		if (dataAtual.before(prova.getDataHora()) || dataAtual.after(prova.getDataHoraLimiteEnvioNormal())) {
			throw new AtividadeException(
					"Prova " + id + " disponivel para download apenas entre " + Util.formatDate(prova.getDataHora())
							+ " e " + Util.formatDate(prova.getDataHoraLimiteEnvioNormal()) + ".\n"
							+ "A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
		}

		// se o arquivo for nulo (nao foi cadastrado ainda) ou nao existe fisicamente
		if (prova.getArquivoAmbiente() == null) {
			throw new AtividadeException("Arquivo de ambiente para  a prova " + id + " nao cadastrado");
		} else if (!prova.getArquivoAmbiente().exists()) {
			throw new AtividadeException("Arquivo de ambiente para  a prova" + id + " nao encontrado no servidor: "
					+ prova.getArquivoAmbiente().getAbsolutePath());
		}
	}

	// realiza validacoes de uma submissao de professor
	public static void validate(ProfessorUploadConfiguration config)
			throws ConfigurationException, IOException, AtividadeException, ServiceException {

		Map<String, Atividade> atividades = Configuration.getInstance().getAtividades();
		Atividade atividade = atividades.get(config.getId());
		if (atividade == null) {
			List<Atividade> roteirosEspeciais = Configuration.getInstance().getRoteirosEspeciais();
			atividade = roteirosEspeciais.stream().filter(a -> a.getId().equals(config.getId())).findFirst()
					.orElse(null);
			if (atividade == null) {
				throw new AtividadeException("Atividade (roteiro ou prova) " + config.getId() + " nao cadastrada");
			}
		}
	}

	// realiza validacoes e retorna excecoes caso alguma delas nao seja satisfeita
	public static void validate(StudentUploadConfiguration config)
			throws ConfigurationException, IOException, StudentException, AtividadeException, ServiceException {

		Map<String, Student> studentMap = Configuration.getInstance().getStudents();
		Map<String, Atividade> atividades = Configuration.getInstance().getAtividades();
		// System.out.println("STUDENT: " + config.getMatricula());
		// studentMap.keySet().forEach(key -> System.out.println("CADASTRADO: " + key));

		// se o estudante informa uma matricula invalida
		if (!studentMap.containsKey(config.getMatricula())) {
			throw new StudentException("Estudante " + config.getMatricula() + " não cadastrado no sistema");
		}

		// se o estudante informa a turma errada da cadastrada no sistema
		Student student = studentMap.get(config.getMatricula());
		if (!student.getTurma().equals(config.getTurma())) {
			throw new StudentException(
					"Estudante " + config.getMatricula() + " não cadastrado na turma " + config.getTurma());
		}

		String id = config.getId();
		Atividade atividade = atividades.get(id);

		// se id informado nao existe
		if (atividade == null) {
			throw new AtividadeException("Atividade (roteiro ou prova) " + id + " não cadastrada no sistema");
		}

		GregorianCalendar dataAtual = new GregorianCalendar();
		ArrayList<String> ips = Configuration.getInstance().getIpsAutorizados();
		String ipCaller = config.getIp();

		// se for prova
		if (Constants.PATTERN_PROVA.matcher(id).matches()) {
			// se o aluno submete de um IP invalido
			// String ipCaller = config.getIp();
			// System.out.println("Validator.IP: " + ipCaller);

			Stream<String> ipStream = ips.stream().filter(ip -> ipCaller.startsWith(ip));
			if (ipStream.count() == 0) { // nao esta nos ips autorizados
				throw new AtividadeException("Envio a partir de IP nao autorizado: " + ipCaller
						+ ". Envios sao possivels apenas a partir de IPs oriundos de: "
						+ Arrays.toString(ips.toArray()));
			}
			ipStream.close();
			if (atividade.getDataHora().after(dataAtual)
					|| ((Roteiro) atividade).getDataHoraLimiteEnvioNormal().before(dataAtual)) {

				throw new AtividadeException(
						"Envio da prova " + id + " possivel apenas entre " + Util.formatDate(atividade.getDataHora())
								+ " e " + Util.formatDate(((Roteiro) atividade).getDataHoraLimiteEnvioNormal())
								+ ". A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
			}
			// se for prova de reposicao e o aluno tiver feito o download da prova
			// correspondente entao neo permite
			// ele fazer download
			// se id for de uma prova de revisao tem que ver na prova correspondente a ela
			if (Constants.PATTERN_PROVA_REPOSICAO.matcher(id).matches()) {
				String idProvaPratica = "PP" + id.substring(2);
				List<String> fizeramDownload = Util.alunosDownload(idProvaPratica);
				if (fizeramDownload.size() == 0) {
					throw new AtividadeException("Registro de downloads da prova " + idProvaPratica
							+ " não encontrados. Favor contactar o prof. com urgencia!");
				}
				if (fizeramDownload.contains(config.getMatricula())) {
					throw new AtividadeException(
							"Ja existe um registro de download da prova " + idProvaPratica + " para o estudante "
									+ config.getMatricula() + ". Envio da prova de reposicao nao permitido!!! ");
				}
			}

		} else { // eh roteiro
			// se roteiro informado esta fora do prazo de recebimento
			if (atividade.getDataHora().after(dataAtual)
					|| ((Roteiro) atividade).getDataHoraLimiteEnvioAtraso().before(dataAtual)) {

				throw new AtividadeException(
						"Envio do roteiro " + id + " possivel apenas entre " + Util.formatDate(atividade.getDataHora())
								+ " e " + Util.formatDate(((Roteiro) atividade).getDataHoraLimiteEnvioAtraso())
								+ ". A hora atual do servidor eh: " + Util.formatDate(new GregorianCalendar()));
			}

			// o primeiro envio pode ser feito de qualquer lugar. Entretanto, se o aluno nao assinar a ata 
			// ele vai levar falta no controle academico. O sistema nao computa mais as faltas. 
			// Apenas tem que lidar com o calculo da media dos roteiros considerando isso (pegando
			// a data do primeiro envio do arquivo). 
			/*
			Atividade pp3 = atividades.get("PP1-" + config.getId().substring(4));
			if (pp3 == null) {
				throw new AtividadeException("Prova PP3-" + config.getId().substring(4) + " nao localizada!");
			}
			Atividade roteiro = atividades.get(config.getId());
			if (roteiro == null) {
				throw new AtividadeException("Roteiro " + config.getId() + " nao localizado!");
			}

			//tenta buscar a submissao de um estudante para determinado roteiro
			Submission sub = Util.getSubmissionForStudent(roteiro.getId(), config.getMatricula());
			if (roteiro.getDataHora().before(pp3.getDataHora())) {
				if (sub == null || sub.getArquivoSubmetido() == null) { // nao tem primeira submissao ainda
					GregorianCalendar now = new GregorianCalendar();

					Stream<String> ipStream = ips.stream().filter(ip -> ipCaller.startsWith(ip));
					
					if (ipStream.count() == 0) { // nao esta nos ips autorizados
						throw new AtividadeException("Primeiro envio a partir de IP nao autorizado: " + ipCaller
								+ ". Envios sao possivels apenas a partir de IPs oriundos de: "
								+ Arrays.toString(ips.toArray()));
					}
				}
			}
			*/

		}

	}
}
