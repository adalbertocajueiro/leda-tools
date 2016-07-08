package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Utilities {

	public static final String JAVASCRIPT_FILE_NAME = "chart.js";
	public static final String HTML_FILE_NAME = "index.html";
	// public static final String WEB_FOLDER = "webChart";
	public static final String RESOURCES_WEB_FOLDER = "/web";
	public static final String JAVASCRIPT_FOLDER = "/static/js";
	public static final String DATA_TAG = "##DATA##";
	private static final int BUFFER_SIZE = 4096;

	/*
	 * public static void callMethod(final Sorting sortImplementation, final
	 * Integer[] argument) { ExecutorService executor =
	 * Executors.newCachedThreadPool(); Callable<Object> task = new
	 * Callable<Object>() { public Object call() {
	 * sortImplementation.sort(argument); return argument; } }; Future<Object>
	 * future = executor.submit(task); try { Object result = future.get(5,
	 * TimeUnit.SECONDS); } catch (TimeoutException ex) { // handle the timeout
	 * } catch (InterruptedException e) { // handle the interrupts } catch
	 * (ExecutionException e) { // handle other exceptions } finally {
	 * future.cancel(true); // may or may not desire this } }
	 */

	public static File createWebFolder(File targetFolder) throws IOException {
		URL file = Utilities.class.getResource(RESOURCES_WEB_FOLDER);
		//boolean dir = isDirectory(file);
		//System.out.println("Local folder: " + dir + " : " + file.getPath());
		File webFolder = unzip(file, targetFolder);
		return webFolder;
	}

	public static void main(String[] args) throws IOException {
		File targetFolder = new File(
				"D:\\UFCG\\leda\\leda-tools\\leda-chart\\target");
		// Utilities.createWebFolder(folder);
	}

	public static boolean isDirectory(URL url) throws IOException {
		String protocol = url.getProtocol();
		if (protocol.equals("file")) {
			System.out.println("IS REAL FILE");
			return new File(url.getFile()).isDirectory();
		}
		if (protocol.equals("jar")) {
			System.out.println("IS JAR FILE");
			String file = url.getFile();
			int bangIndex = file.indexOf('!');
			String jarPath = file.substring(bangIndex + 2);
			file = new URL(file.substring(0, bangIndex)).getFile();
			ZipFile zip = new ZipFile(file);
			ZipEntry entry = zip.getEntry(jarPath);
			boolean isDirectory = entry.isDirectory();
			if (!isDirectory) {
				InputStream input = zip.getInputStream(entry);
				isDirectory = input == null;
				if (input != null)
					input.close();
			}
			zip.close();
			return isDirectory;
		}
		throw new RuntimeException("Invalid protocol: " + protocol);
	}

	public static File unzip(URL folderOrJar, File destDir) throws IOException {

		File webFolder = new File(destDir, RESOURCES_WEB_FOLDER);

		if (!destDir.exists()) {
			destDir.mkdir();
		}
		if(folderOrJar.getProtocol().equals("file")){
			File localfolder = new File(folderOrJar.getPath());
			copyFolder(localfolder, webFolder);
		} else if (folderOrJar.getProtocol().equals("jar")) {
			String file = folderOrJar.getFile();
			int bangIndex = file.indexOf('!');
			String jarPath = file.substring(bangIndex + 2);
			// System.out.println("JAR PATH: " + jarPath);
			file = new URL(file.substring(0, bangIndex)).getFile();
			ZipFile zip = new ZipFile(file);
			ZipEntry entryWeb = zip.getEntry(jarPath);
			//System.out.println("UNZIP JAR FILE: " + folderOrJar.getFile());
			//System.out.println("UNZIP: " + folderOrJar.getFile().substring(5, bangIndex));
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(
					new File(folderOrJar.getFile().substring(5, bangIndex))));
			ZipEntry entry = zipIn.getNextEntry();

			// iterates over entries in the zip file
			while (entry != null) {
				// System.out.println("Entry: " + entry.getName());

				String filePath = destDir.getAbsolutePath() + File.separator
						+ entry.getName();
				if (entry.getName().startsWith(entryWeb.getName())) {
					if (!entry.isDirectory()) {
						String submittedFilePath = getPathFileName(entry);
						File fileParentDir = new File(destDir.getAbsolutePath()
								+ File.separator + submittedFilePath);
						if (!fileParentDir.exists()) {
							fileParentDir.mkdirs();
						}
						extractFile(zipIn, filePath);
					} else {
						// if the entry is a directory, make the directory
						File dir = new File(filePath);
						if (!dir.exists()) {
							dir.mkdir();
						}
					}
				}
				zipIn.closeEntry();
				entry = zipIn.getNextEntry();
			}
			zipIn.close();
			zip.close();

			webFolder = new File(destDir, jarPath);
		}

		return webFolder;
	}

	private static String getPathFileName(ZipEntry entry) {

		String result = entry.getName();
		int index = result.lastIndexOf("/");
		if (index != -1) {
			result = result.substring(0, index);
		}
		return result;
	}

	private static void extractFile(ZipInputStream zipIn, String filePath)
			throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public static StringBuilder readFile(String filePath) throws IOException {
		StringBuilder result = new StringBuilder();
		InputStream is = Utilities.class.getResourceAsStream(filePath);
		InputStreamReader isr = null;
		FileInputStream fis  = null;
		if(is == null){
			fis = new FileInputStream(filePath);
			isr = new InputStreamReader(fis);
		}else{
			isr = new InputStreamReader(is);
		}
		
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
			result.append(line);
			result.append("\n");
		}
		if(is != null){
			is.close();
		}
		if(fis != null){
			fis.close();
		}
		br.close();

		return result;
	}

	public static void writeFile(String filePath, String content)
			throws IOException {
		URL fileURL = Utilities.class.getResource(filePath);
		File file = null;
		if(fileURL == null){
			file = new File(filePath);
		} else{
			file = new File(fileURL.getFile());
		}
		
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content.toString());
		bw.flush();
		fw.close();
		bw.close();
	}

	public static void addDataToFinalJavaScript(File webFolder, String data)
			throws IOException {
		StringBuilder content = readFile(webFolder + JAVASCRIPT_FOLDER
				+ File.separator + JAVASCRIPT_FILE_NAME);
		String contentString = content.toString();
		contentString = contentString.replace(DATA_TAG, data);
		writeFile(webFolder.getAbsolutePath() + JAVASCRIPT_FOLDER
				+ File.separator + JAVASCRIPT_FILE_NAME, contentString);
	}

	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();

			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();

		}
	}
}
