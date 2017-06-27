package plag.runner;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;


public class SimilarityAnalysisResult {
	private File fileStudent1;
	private File fileStudent2;
	private double similarity;
	private File analysisFolderInServer;
	
	public SimilarityAnalysisResult(File fileStudent1, File fileStudent2, 
			double similarity, File analysisFolderInServer) {
		super();
		this.fileStudent1 = fileStudent1;
		this.fileStudent2 = fileStudent2;
		this.similarity = similarity;
		this.analysisFolderInServer = analysisFolderInServer;
	}
	public String getMatriculaStudent1(){
		return PlagRunner.getStudentAnalysisFolderName(this.fileStudent1);
	}
	
	public String getMatriculaStudent2(){
		return PlagRunner.getStudentAnalysisFolderName(this.fileStudent2);
	}
	public File getFileStudent1() {
		return fileStudent1;
	}
	public void setFileStudent1(File fileStudent1) {
		this.fileStudent1 = fileStudent1;
	}
	public File getFileStudent2() {
		return fileStudent2;
	}
	public void setFileStudent2(File fileStudent2) {
		this.fileStudent2 = fileStudent2;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	public File getAnalysisFolderInServer() {
		return analysisFolderInServer;
	}
	public void setAnalysisFolderInServer(File analysisFolderInServer) {
		this.analysisFolderInServer = analysisFolderInServer;
	}
	
	public String generateLinkFileStudent1(){
		Path relativeLink = null;
		File atividadeFolder = fileStudent1.getParentFile().getParentFile().getParentFile(); //referenciador (nome do link a ser criado)
			try {
				relativeLink = generateRelativeLink(fileStudent1, atividadeFolder);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				relativeLink = fileStudent1.toPath();
			}
		return relativeLink != null? relativeLink.toString():"";
	}
	
	public String generateLinkFileStudent2(){
		Path relativeLink = null;
		File atividadeFolder = fileStudent1.getParentFile().getParentFile().getParentFile(); //referenciador (nome do link a ser criado)
			try {
				relativeLink = generateRelativeLink(fileStudent2, atividadeFolder);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				relativeLink = fileStudent2.toPath();
			}
		return relativeLink != null? relativeLink.toString():"";
	}
	
	private Path generateRelativeLink(File referenciado, File referenciador)
			throws MalformedURLException {
		//Path pathAbsolute = Paths.get(referenciado.getAbsolutePath());
		//Path pathBase = Paths.get(referenciador.getAbsolutePath());
		Path pathAbsolute = referenciado.toPath();
		Path pathBase = referenciador.toPath();
		Path pathRelative = pathBase.relativize(pathAbsolute);

		return pathRelative;
	}
}
