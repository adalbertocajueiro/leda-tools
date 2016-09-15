package br.edu.ufcg.ccc.leda.submission.util;

import java.io.File;

public class FileCopy {
	private File file;
	private Student owner;
	private Student copier;
	public FileCopy(File file, Student owner, Student copier) {
		super();
		this.file = file;
		this.owner = owner;
		this.copier = copier;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Student getOwner() {
		return owner;
	}
	public void setOwner(Student owner) {
		this.owner = owner;
	}
	public Student getCopier() {
		return copier;
	}
	public void setCopier(Student copier) {
		this.copier = copier;
	}
	
	
}
