package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gdata.util.ServiceException;

public class CorrectionManager {

	private Timer scheduler;
	public static final long CORRECTION_TIMER_DELAY = 30 * 60 * 1000;
	private File currentSemesterFolder;
	private ArrayList<Thread> executing;
	private static CorrectionManager instance;
	
	protected CorrectionManager() {
		this.currentSemesterFolder = new File(new File(Constants.UPLOAD_FOLDER_NAME),Constants.CURRENT_SEMESTER);
		executing = new ArrayList<Thread>();
		scheduler = new Timer("Correction timer", false);
		scheduler.scheduleAtFixedRate(new CorrectionTimerTask(), 2000, CORRECTION_TIMER_DELAY);
	}

	public static CorrectionManager getInstance() {
		if (instance == null) {
			instance = new CorrectionManager();
		}
		return instance;
	}

	private class CorrectionTimerTask extends TimerTask {

		@Override
		public void run() {
			// filtra as pastas que correspondem a roteiros enviados
			// cada pasta é o proprio identificador do roteiro.
			System.out.println("%%%%%%%% Executando Correction Timer Task em: " + Util.formatDate(new GregorianCalendar()));
			System.out.println("Verificando roteiros a corrigir...");
			File[] roteiros = currentSemesterFolder.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						boolean resp = false;
						if (pathname.isDirectory()) {
							resp = Constants.PATTERN_ROTEIRO.matcher(pathname.getName()).matches();
						}
						return resp;
					}
				
			});

			// procura ver se existe alguma thread que ja terminou e remove
			// ela da coleção
			List<Thread> terminated = executing.stream()
					.filter(t -> t.getState().equals(State.TERMINATED))
					.collect(Collectors.toList());
			//System.out.println("Threads antigas e terminadas a serem removidas: " + terminated.size());
			terminated.forEach(t -> executing.remove(t));

			for (int i = 0; i < roteiros.length; i++) {
				try {
					if (canCorrect(roteiros[i])) {
						// se a thread de correcao nao estiver na lista
						// entao adiciona ela.
						if (!executing.stream().map(Thread::getName)
								.collect(Collectors.toList())
								.contains(roteiros[i].getName())) {

							System.out.println("Iniciando correcao de " + roteiros[i].getName());
							AutomaticCorrector corrector = new AutomaticCorrector();
							Thread task = corrector.corrigirRoteiro(roteiros[i].getName());
							executing.add(task);
						}
					}else{
						System.out.println("Roteiro " + roteiros[i] + " nao pode ser corrigido porque ainda nao fechou o envio ou porque ja foi corrigido");
					}
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			System.out.println("Verificando provas a corrigir...");
			Pattern patternProva = Pattern.compile("P[PRF][1-3]-[0-9][0-9[X]]");
			File[] provas = currentSemesterFolder.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						boolean resp = false;
						if (pathname.isDirectory()) {
							resp = patternProva.matcher(pathname.getName()).matches();
						}
						return resp;
					}
				
			});
			for (int i = 0; i < provas.length; i++) {
				try {
					if (canCorrectProva(provas[i])) {
						// se a thread de correcao nao estiver na lista
						// entao adiciona ela.
						if (!executing.stream().map(Thread::getName)
								.collect(Collectors.toList())
								.contains(provas[i].getName())) {

							System.out.println("Iniciando correcao de " + provas[i].getName());
							AutomaticCorrector corrector = new AutomaticCorrector();
							Thread task = corrector.corrigirProva(provas[i]
									.getName());
							executing.add(task);
						}
					}else{
						System.out.println("Prova " + provas[i] + " nao pode ser corrigido porque ja foi corrigida ou ainda nao fechou o envio");
					}
				} catch (ConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		

	}
	private boolean canCorrect(File roteiro)
			throws ConfigurationException, IOException, ServiceException {
		boolean result = false;
		// tem que ver se a data atual ultrapassou a data limite de envio
		// com atraso
		Map<String, Roteiro> roteiros = Configuration.getInstance().getRoteiros();
		Roteiro rot = roteiros.get(roteiro.getName());
		if (rot != null) {
			GregorianCalendar current = new GregorianCalendar();
			result = current.after(rot.getDataHoraLimiteEnvioAtraso());
		} else {
			throw new RuntimeException(
					"Roteiro nao localizado (CorrectionTimerTask.canCorrect)");
		}
		// verifica se existe um arquivo target/generated-report.html (indicando que ja
		// foi corrigido)
		//File targetFolder = new File(roteiro,"target");
		File report = new File(roteiro,Constants.GENERATED_REPORT_FILE);
		result = result && !report.exists();
		//File report = new File(roteiro, MAVEN_OUTPUT_FILE);
		//result = result && !report.exists();

		
		return result;
	}
	private boolean canCorrectProva(File prova)
			throws ConfigurationException, IOException, ServiceException {
		boolean result = false;
		// tem que ver se a data atual ultrapassou a data limite de envio
		// com atraso
		Map<String, Prova> provas = Configuration.getInstance().getProvas();
		Prova prov = provas.get(prova.getName());
		if (prov != null) {
			GregorianCalendar current = new GregorianCalendar();
			result = current.after(prov.getDataHoraLimiteEnvioNormal());
		} else {
			throw new RuntimeException(
					"Prova nao localizada (CorrectionTimerTask.canCorrectProva)");
		}
		
		// verifica se existe um arquivo maven-output.txt (indicando que ja
		// foi corrigido)

		File report = new File(prova, Constants.GENERATED_REPORT_FILE);
		result = result && !report.exists();
		
		return result;
	}

	
	public ArrayList<Thread> getExecuting() {
		return executing;
	}
	
	public static void main(String[] args) throws InterruptedException, ConfigurationException, IOException, ServiceException {
		Configuration config = Configuration.getInstance();
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		File roteirosFolder = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		CorrectionManager cm = new CorrectionManager();
		cm.canCorrect(new File(roteirosFolder,"R01-01"));
	}

}
