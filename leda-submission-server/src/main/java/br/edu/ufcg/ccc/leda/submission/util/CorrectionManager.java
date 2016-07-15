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

public class CorrectionManager {

	private Timer scheduler;
	public static final long CORRECTION_TIMER_DELAY = 30 * 60 * 1000;
	private File roteirosFolder;
	private ArrayList<Thread> executing;
	private static final String MAVEN_OUTPUT_FILE = "maven-output.txt";
	private Configuration configuration;
	/*private static CorrectionManager instance;
	static {
		scheduler = new Timer("Correction timer", true);
		roteirosFolder = new File(new File(FileUtilities.UPLOAD_FOLDER),
				FileUtilities.CURRENT_SEMESTER);
		executing = new ArrayList<Thread>();
	}*/

	protected CorrectionManager(File roteirosFolder, Configuration config) {
		this.roteirosFolder = roteirosFolder;
		this.configuration = config;
		executing = new ArrayList<Thread>();
		scheduler = new Timer("Correction timer", false);
		scheduler.scheduleAtFixedRate(new CorrectionTimerTask(), 1000, CORRECTION_TIMER_DELAY);
	}

	/*public static CorrectionManager getInstance() {
		if (instance == null) {
			instance = new CorrectionManager();
		}
		return instance;
	}*/

	private class CorrectionTimerTask extends TimerTask {

		@Override
		public void run() {
			// filtra as pastas que correspondem a roteiros enviados
			// cada pasta é o proprio identificador do roteiro.
			System.out.println("%%%%%%%% Executando Correction Timer Task em: " + Util.formatDate(new GregorianCalendar()));
			File[] roteiros = roteirosFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					boolean resp = false;
					if (pathname.isDirectory()) {
						// na pratica naovai ter roteiro com identificador X
						// porque ele é substituido quando o upload é feito
						Pattern patternRoteiro = Pattern
								.compile("R[0-9]{2}-[0-9][0-9[X]]");
						resp = patternRoteiro.matcher(pathname.getName())
								.matches();
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
							Thread task = corrector.corrigirRoteiro(roteiros[i]
									.getName());
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
				}
			}
			
		}

		private boolean canCorrect(File roteiro)
				throws ConfigurationException, IOException {
			boolean result = false;
			// tem que ver se a data atual ultrapassou a data limite de envio
			// com atraso
			Map<String, Roteiro> roteiros = configuration.getRoteiros();
			Roteiro rot = roteiros.get(roteiro.getName());
			if (rot != null) {
				GregorianCalendar current = new GregorianCalendar();
				result = current.after(rot.getDataHoraLimiteEnvioAtraso());
			} else {
				throw new RuntimeException(
						"Roteiro nao localizado (CorrectionTimerTask.isReadyTorun)");
			}
			// verifica se existe um arquivo maven-output.txt (indicando que ja
			// foi corrigido)
			File report = new File(roteiro, MAVEN_OUTPUT_FILE);
			result = result && !report.exists();

			
			return result;
		}

	}

	public static void main(String[] args) throws InterruptedException {
		Pattern patternRoteiro = Pattern.compile("R[0-9]{2}-[0-9][0-9[X]]");
		System.out.println(patternRoteiro.matcher("R01-01").matches());
		System.out.println(patternRoteiro.matcher("R01-02").matches());
		System.out.println(patternRoteiro.matcher("R02-02").matches());
		System.out.println(patternRoteiro.matcher("R17-0X").matches());
		Timer timer = new Timer("test", true);
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				System.out.println("Execution of a timer task "
						+ this.toString());

			}
		}, 0, 500);
		System.out.println("other task 1");
		System.out.println("other task 2");
		Thread.sleep(10000);
		System.out.println("End");
	}

}
