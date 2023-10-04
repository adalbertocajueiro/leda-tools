package br.edu.ufcg.leda.util;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A class that represents a pair (fileName,folder) where fileName is a file
 * name and folder is the folder where the file will be extracted.
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileFolder {
	private String fileName;
	private File folder;

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof FileFolder) {
			String pureFileName = this.extractPureFileName(this.fileName);
			result = pureFileName.equalsIgnoreCase(((FileFolder) obj)
					.getFileName());
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
