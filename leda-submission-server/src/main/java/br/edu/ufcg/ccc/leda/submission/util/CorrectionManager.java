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
import java.util.stream.Collectors;

import com.google.gdata.util.ServiceException;

public class CorrectionManager {

	private Timer scheduler;
	public static final long CORRECTION_TIMER_DELAY = 30 * 60 * 1000;
	private ArrayList<Thread> executing;
	private static CorrectionManager instance;
	
	protected CorrectionManager() {
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
			System.out.println("Verificando atividades a corrigir...");
			File[] atividades = Constants.CURRENT_SEMESTER_FOLDER.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						boolean resp = false;
						if (pathname.isDirectory()) {
							resp = Constants.PATTERN_ROTEIRO.matcher(pathname.getName()).matches()
									|| Constants.PATTERN_PROVA.matcher(pathname.getName()).matches();
						}
						return resp;
					}
				
			});
			
			List<Thread> terminated = executing.stream()
					.filter(t -> t.getState().equals(State.TERMINATED))
					.collect(Collectors.toList());
			//System.out.println("Threads antigas e terminadas a serem removidas: " + terminated.size());
			terminated.forEach(t -> executing.remove(t));

			for (int i = 0; i < atividades.length; i++) {
				try {
					if (canCorrect(atividades[i])) {
						// se a thread de correcao nao estiver na lista
						// entao adiciona ela.
						if (!executing.stream().map(Thread::getName)
								.collect(Collectors.toList())
								.contains(atividades[i].getName())) {

							System.out.println("Iniciando correcao de " + atividades[i].getName());
							AutomaticCorrector corrector = new AutomaticCorrector();
							Thread task = corrector.corrigirRoteiro(atividades[i].getName());
							executing.add(task);
						}
					}else{
						System.out.println("Atividade " + atividades[i] + " nao pode ser corrigida porque ainda nao fechou o envio ou porque ja foi corrigido");
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
	private boolean canCorrect(File atividadeId)
			throws ConfigurationException, IOException, ServiceException {
		boolean result = false;
		// tem que ver se a data atual ultrapassou a data limite de envio
		// com atraso
		Map<String, Atividade> atividades = Configuration.getInstance().getAtividades();
		Atividade ativ = atividades.get(atividadeId.getName());
		if (ativ != null && ativ instanceof Roteiro) {
			GregorianCalendar current = new GregorianCalendar();
			result = current.after(((Roteiro) ativ).getDataHoraLimiteEnvioAtraso());
		} else {
			throw new RuntimeException(
					"Atividade nao localizada (CorrectionTimerTask.canCorrect): " + atividadeId.getName());
		}
		// verifica se existe um arquivo target/generated-report.html (indicando que ja
		// foi corrigido)
		//File targetFolder = new File(roteiro,"target");
		File report = new File(atividadeId,Constants.GENERATED_REPORT_FILE);
		result = result && !report.exists();
		//File report = new File(roteiro, MAVEN_OUTPUT_FILE);
		//result = result && !report.exists();

		
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
