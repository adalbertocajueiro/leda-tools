package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class AutomaticCorrector {

	
	/**
	 * Corrige automaticamente um roteiro executando o maven no projeto de correcao.
	 * vamos chamar o maven programaticamente como no leda-correction-tool.
	 * 
	 * @param id - o identificador do roteiro no formaro R0X-0X
	 * @throws IOException 
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public Thread corrigirAtividade(String id) throws IOException, InterruptedException, ExecutionException,  NoSuchMethodException, SecurityException{
		
		File pastaAtividadeACorrigir = new File(Constants.CURRENT_SEMESTER_FOLDER,id);
		//PRECISA FAZER UMAS VALIDADOES PRA VER SE A PASTA ESTA OK ANTES DA CORRECAO

		FileFilter filter = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				//System.out.println("procurando arquivos do id: " + id + " pathname: " + pathname.getName());
				return pathname.getName().startsWith(id) && pathname.getName().contains("correction");
			}
		};
		File[] files = new File[0];
		if(Constants.PATTERN_ROTEIRO.matcher(id).matches() || Constants.PATTERN_ROTEIRO_REVISAO.matcher(id).matches()){			
			//pega os arquivos correction-proj 
			files = Constants.ROTEIROS_FOLDER.listFiles(filter);
		}else if(Constants.PATTERN_PROVA.matcher(id).matches()){
			//pega os arquivos correction-proj 
			files = Constants.PROVAS_FOLDER.listFiles(filter);
		}
		if(files != null){
			//copia o arquivo correction-proj para a pasta de correcao do roteiro e o descompacta
			if(files.length == 1){
				File foutCorrProj = new File(pastaAtividadeACorrigir,files[0].getName());
				Files.copy(files[0].toPath(), foutCorrProj.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Util.unzip(foutCorrProj);
			}
		}

		Thread task = runCorrection(pastaAtividadeACorrigir);
		
		return task;
	}
	
	/*public Thread corrigirProva(String prova) throws IOException, InterruptedException, ExecutionException,  NoSuchMethodException, SecurityException{
		//pega a pasta de uploads. dentro dessa pasta existe a subpasta dos semestres
		File uploadFolder = new File(Constants.UPLOAD_FOLDER_NAME);
		//File uploadFolder = new File("D:\\trash2\\leda-upload");
		
		//pega a subpasta do semestre atual
		File currentSemesterUploadFolder = new File(uploadFolder,Constants.CURRENT_SEMESTER);
		
		//pega a pasta contendo os arquivos dos roteiros enviados pelo docente
		File provasFolder = new File(currentSemesterUploadFolder,Constants.PROVAS_FOLDER_NAME);
		
		//pega a pasta contendo os uploads do roteiro informado (ela vai ser a pasta do
		//projeto maven de correcao a ser executado). as submissoes estarao na sub-pasta subs 
		//para cada pasta de roteiro.
		File pastaProvaCorrigida = new File(currentSemesterUploadFolder,prova);
		//PRECISA FAZER UMAS VALIDADOES PRA VER SE A PASTA ESTA OK ANTES DA CORRECAO
		//System.out.println("Tentanto corrigir prova: " + pastaProvaCorrigida.getAbsolutePath());
		//pega os arquivos correction-proj 
		File[] files = provasFolder.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				//tem arquivos de correcao cadastrado para o roteiro pelo ID
				return pathname.getName().startsWith(prova) && pathname.getName().contains("correction");
			}
		});
		//copia o arquivo correction-proj para a pasta de correcao do roteiro e o descompacta
		if(files.length == 1){
			File foutCorrProj = new File(pastaProvaCorrigida,files[0].getName());
			Files.copy(files[0].toPath(), foutCorrProj.toPath(), StandardCopyOption.REPLACE_EXISTING);
			Util.unzip(foutCorrProj);
		}
			
		//executa o maven salvando em um arquivo de log (maven-output.txt) na pasta 
		//String cdCommand = "cd " + pastaProvaCorrigida.getAbsolutePath();
		//String mavenCommand = "mvn install site --log-file maven-output.txt";
		//Runtime.getRuntime().exec(cdCommand mavenCommand);
		//ProcessBuilder pb = new ProcessBuilder(cdCommand,mavenCommand);
		//pb.start();
		Thread task = runCorrection(pastaProvaCorrigida);
		
		return task;
	}*/
	
	public void executeMaven(File projectFolder) throws MavenInvocationException, IOException{
		Invoker invoker = new DefaultInvoker();
		System.setProperty("maven.home", Constants.MAVEN_HOME_FOLDER);
		if (projectFolder.isDirectory()) {
			InvocationRequest request = new DefaultInvocationRequest();
			//request.setPomFile(new File(projectFoder, "./pom.xml"));
			//request.setGoals(Arrays.asList("surefire-report:report-only"));
			request.setGoals(Arrays.asList("clean", "install", "-e", "--log-file maven-output.txt" ));
			request.setBaseDirectory(projectFolder);
			invoker.execute(request);
		}
		//depois de rodar o amven a thread compacta a pasta
		File zipFile = Util.compact(projectFolder);
		//depois de comapctar, manda para o google driver
	}
	
	
	public Thread runCorrection(File projectFolder) throws InterruptedException, ExecutionException, NoSuchMethodException, SecurityException  {
		Method target = this.getClass().getMethod("executeMaven", new Class[]{File.class});

		Thread mavenThread = new Thread(() -> {
		     try {
				target.invoke(this, new Object[]{projectFolder});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		mavenThread.setName(projectFolder.getName());
		mavenThread.start();
		//System.out.println("Thread " + mavenThread + " iniciado.");
		return mavenThread;
	}
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException, NoSuchMethodException, SecurityException {
		AutomaticCorrector ac = new AutomaticCorrector();
		System.out.println("Corection started");
		ac.corrigirAtividade("R01-01");
		System.out.println("Corection in progress");
	}
	
}
