package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.List;

public class Util {
	public static final String AUTHOR = "author";
	
	public static void addAuthorToFiles(List<File> files, String author) throws IOException{
		for (File file : files) {
			writeAuthor(file.toPath(), author);
		}
	}
	public static void writeAuthor(Path file, String value) throws IOException{

		    UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);

		    // The file attribute
		    String name = AUTHOR;
		    List<String> attributes = view.list();
		    if(!attributes.contains(name)){
			    // Write the properties
			    byte[] bytes = value.getBytes("UTF-8");
			    ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			    writeBuffer.put(bytes);
			    writeBuffer.flip();
			    view.write(name, writeBuffer);
		    }

	}
	
	public static String readAuthor(Path file) throws IOException{
		String valueFromAttributes = null;
	    UserDefinedFileAttributeView view = Files.getFileAttributeView(file, UserDefinedFileAttributeView.class);
	    String name = AUTHOR;
	    ByteBuffer readBuffer = ByteBuffer.allocate(view.size(name));
	    view.read(name, readBuffer);
	    readBuffer.flip();
	    valueFromAttributes = new String(readBuffer.array(), "UTF-8");
	    
	    return valueFromAttributes;
	}
	
	public static List<File> getFiles(File folder, String extension){
		ArrayList<File> files = new ArrayList<File>();
		addFile(folder, files, extension);
		return files; 
	}
	private static void addFile(File folder, List<File> files, String extension){
		if(folder.isDirectory()){
			File[] subFiles = folder.listFiles();
			for (int i = 0; i < subFiles.length; i++) {
				addFile(subFiles[i], files, extension);
			}
		} else if (folder.getName().endsWith(extension)){
			if(!files.contains(folder)){
				files.add(folder);
			}
		} 
	}
}
