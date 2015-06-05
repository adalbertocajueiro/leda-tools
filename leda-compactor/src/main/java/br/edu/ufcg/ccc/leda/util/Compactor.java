package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
/**
 * This utility class has methods to zip folders.
 *
 */
public class Compactor{
   
    public void zipFolder(File srcFolder, File destZipFile) throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;

        fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);

        addFolderToZip("", srcFolder, zip);
        zip.flush();
        zip.close();
      }
    
    private void addFileToZip(String path, File srcFile, ZipOutputStream zip)
    	      throws Exception {

    	    File folder = srcFile;
    	    if (folder.isDirectory()) {
    	      addFolderToZip(path, srcFile, zip);
    	    } else {
    	      byte[] buf = new byte[1024];
    	      int len;
    	      FileInputStream in = new FileInputStream(srcFile);
    	      zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
    	      while ((len = in.read(buf)) > 0) {
    	        zip.write(buf, 0, len);
    	      }
    	    }
    	  }
    
    private void addFolderToZip(String path, File srcFolder, ZipOutputStream zip)
    	      throws Exception {
    	    File folder = srcFolder;

    	    for (String fileName : folder.list()) {
    	    	File newFolder = new File(srcFolder,fileName);
    	      if (path.equals("")) {
    	    	  addFileToZip(folder.getName(), newFolder, zip);
    	      } else {
    	    	  addFileToZip(path + "/" + folder.getName(), newFolder, zip);
    	      }
    	    }
    	  }
}