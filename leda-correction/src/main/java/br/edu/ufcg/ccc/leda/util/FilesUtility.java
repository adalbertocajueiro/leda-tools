package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
 
/**
 * This utility class has many methods to manipulate files (unzip, copy, delete, etc.)
 *
 */
public class FilesUtility{
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    
    private HashMap<String, String> filesFolders;
    
    public FilesUtility() {
		this.filesFolders = new HashMap<String,String>();
	}

	
    /**
     * Extracts specific files (in the fileNames list) from a zip file specified by the 
     * zipFilePath to a directory specified by destDirectory (will be created if does not exists).
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(File studentZipFile, File projectFolder, List<String> files) throws IOException {
    	
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(studentZipFile));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            
        	String filePath = projectFolder.getAbsolutePath() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file and is in the list of files to be copied, extracts it
            	//FileFolder obj = new FileFolder(entry.getName(), null);
            	String submittedFileName = this.getPureFileName(entry);
            	
            	//it considers only java files
            	if(submittedFileName.endsWith(".java")){           	
	            	int index = files.indexOf(submittedFileName);
	            	if(index != -1){
	            		String targetFolder = this.filesFolders.get(submittedFileName);
	            		if(targetFolder != null){
	            			filePath = projectFolder.getAbsolutePath() + File.separator + targetFolder + 
	            					File.separator + submittedFileName; 
	                		extractFile(zipIn, filePath);
	            		}else{
	            			throw new RuntimeException("File " + submittedFileName + " is not present in the folder structure!");
	            		}
	            		
	            	}
            	}
            } else {
                // if the entry is a directory, make the directory
                //File dir = new File(filePath);
                //if(!dir.exists()){
                //	dir.mkdir();
                //}
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    
   
    /**
     * Extracts all files from a zip file in a to a directory specified by destDir 
     * (will be created if does not exists). This is intended to create the initial folder structure
     * of the project/submission to be analysed.
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(File correctionZipFile, File destDir) throws IOException {
    	
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(correctionZipFile));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            
        	String filePath = destDir.getAbsolutePath() + File.separator + entry.getName();
            if (!entry.isDirectory()) {
            	String submittedFileName = this.getPureFileName(entry);
            	String submittedFilePath = this.getPathFileName(entry);
            	File fileParentDir = new File(destDir.getAbsolutePath() + File.separator + submittedFilePath);
            	if(!fileParentDir.exists()){
            		fileParentDir.mkdirs();
            	}
            	this.filesFolders.put(submittedFileName, submittedFilePath);
            	
           		extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                if(!dir.exists()){
                	dir.mkdirs();
                }
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    
    public String getFileNameWithoutExtension(File file){
    	String result = file.getName();
    	int index = result.lastIndexOf(".");
    	if(index != 1){
    		result = result.substring(0, index);
    	}
    	return result;
    }
    
    private String getPureFileName(ZipEntry entry){
    	
    	String result = entry.getName();
    	int index = result.lastIndexOf("/");
    	if(index != 1){
    		result = result.substring(index + 1);
    	}
    	return result;
    }
    
    private String getPathFileName(ZipEntry entry){
    	
    	String result = entry.getName();
    	int index = result.lastIndexOf("/");
    	if(index != -1){
    		result = result.substring(0,index);
    	}
    	return result;
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}