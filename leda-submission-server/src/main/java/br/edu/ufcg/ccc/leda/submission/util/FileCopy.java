package br.edu.ufcg.ccc.leda.submission.util;

public class FileCopy {
	private String fileName;
	private String owner;
	private Student copier;
	
	public FileCopy(String fileName, String owner, Student copier) {
		super();
		this.fileName = fileName;
		this.owner = owner;
		this.copier = copier;
	}
	@Override
	public String toString() {
		return "Arquivo: " + this.fileName + " enviado por " + copier.getMatricula() + "-" + copier.getNome() + " tem como proprietario original " + this.owner;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Student getCopier() {
		return copier;
	}
	public void setCopier(Student copier) {
		this.copier = copier;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
}
