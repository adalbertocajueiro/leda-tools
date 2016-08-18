package br.edu.ufcg.ccc.leda.util;

import java.io.File;

/**
 * A class that represents a pair (fileName,folder) where fileName is a file
 * name and folder is the folder where the file will be extracted.
 * 
 * @author Adalberto
 *
 */
public class FileFolder {
	private String fileName;
	private File folder;

	public FileFolder() {
		super();
	}

	public FileFolder(String fileName, File folder) {
		super();
		this.fileName = fileName;
		this.folder = folder;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof FileFolder) {
			String pureFileName = this.extractPureFileName(this.fileName);
			result = pureFileName.equalsIgnoreCase(((FileFolder) obj)
					.getFileName());
			int i = 0;
		}
		return result;
	}

	private String extractPureFileName(String pathFileName) {
		String result = pathFileName;
		int index = pathFileName.lastIndexOf('/');
		if (index != -1) {
			result = pathFileName.substring(index + 1);
		}
		return result;
	}

	public String getFullPath() {
		return this.folder.getPath() + File.separator + this.fileName;
	}

	@Override
	public String toString() {

		return this.fileName + "(" + this.folder.getPath() + ")";
	}

}
