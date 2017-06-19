package plag.runner;

import java.io.File;

public class SimilarityAnalysisResult {
	private File fileStudent1;
	private File fileStudent2;
	private double similarity;
	public SimilarityAnalysisResult(File fileStudent1, File fileStudent2, double similarity) {
		super();
		this.fileStudent1 = fileStudent1;
		this.fileStudent2 = fileStudent2;
		this.similarity = similarity;
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
	
}
