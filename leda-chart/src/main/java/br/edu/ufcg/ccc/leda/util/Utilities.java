package br.edu.ufcg.ccc.leda.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import sorting.Sorting;

public class Utilities {

	public static final String JSON_FILE_NAME = "data.json";
	public static final String HTML_FILE_NAME = "index.html";
	// public static final String WEB_FOLDER = "webChart";
	public static final String RESOURCES_WEB_FOLDER = "/web";
	private static final int BUFFER_SIZE = 4096;

	public static void callMethod(final Sorting sortImplementation,
			final Integer[] argument) {
		ExecutorService executor = Executors.newCachedThreadPool();
		Callable<Object> task = new Callable<Object>() {
			public Object call() {
				sortImplementation.sort(argument);
				return argument;
			}
		};
		Future<Object> future = executor.submit(task);
		try {
			Object result = future.get(5, TimeUnit.SECONDS);
		} catch (TimeoutException ex) {
			// handle the timeout
		} catch (InterruptedException e) {
			// handle the interrupts
		} catch (ExecutionException e) {
			// handle other exceptions
		} finally {
			future.cancel(true); // may or may not desire this
		}
	}

	public static File createWebFolder(File targetFolder) throws IOException {
		URL file = Utilities.class.getResource(RESOURCES_WEB_FOLDER);
		// boolean dir = isDirectory(file);
		File webFolder = unzip(file, targetFolder);
		return webFolder;
	}

	public static void main(String[] args) throws IOException {
		File folder = new File(
				"D:\\UFCG\\leda\\leda-tools\\leda-chart\\src\\main\\resources");
		//Utilities.createWebFolder(folder);
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
			return isDirectory;
		}
		throw new RuntimeException("Invalid protocol: " + protocol);
	}

	public static File unzip(URL folder, File destDir) throws IOException {

		if (!destDir.exists()) {
			destDir.mkdir();
		}
		String file = folder.getFile();
		int bangIndex = file.indexOf('!');
		String jarPath = file.substring(bangIndex + 2);
		System.out.println("JAR PATH: " + jarPath);
		file = new URL(file.substring(0, bangIndex)).getFile();
		ZipFile zip = new ZipFile(file);
		ZipEntry entryWeb = zip.getEntry(jarPath);

		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(new File(
				folder.getFile().substring(6, bangIndex))));
		ZipEntry entry = zipIn.getNextEntry();

		// iterates over entries in the zip file
		while (entry != null) {
			// System.out.println("Entry: " + entry.getName());

			String filePath = destDir.getAbsolutePath() + File.separator
					+ entry.getName();
			if (entry.getName().startsWith(entryWeb.getName())) {
				if (!entry.isDirectory()) {
					//String submittedFileName = getPureFileName(entry);
					//String submittedFilePath = getPathFileName(entry);
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
		
		File webFolder = new File(destDir,jarPath);
		return webFolder;
	}

	private static String getPureFileName(ZipEntry entry) {

		String result = entry.getName();
		int index = result.lastIndexOf("/");
		if (index != 1) {
			result = result.substring(index + 1);
		}
		return result;
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

}
