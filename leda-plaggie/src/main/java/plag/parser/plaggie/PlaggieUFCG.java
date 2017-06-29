/* 
 *  Copyright (C) 2006 Aleksi Ahtiainen, Mikko Rahikainen.
 * 
 *  This file is part of Plaggie.
 *
 *  Plaggie is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation; either version 2 of the License,
 *  or (at your option) any later version.
 *
 *  Plaggie is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Plaggie; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301  USA
 */
package plag.parser.plaggie;

import plag.parser.*;
import plag.parser.java.*;
import plag.parser.report.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import br.edu.ufcg.ccc.leda.util.Student;

import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import java.io.*;
import java.net.URI;
import java.net.URL;

/**
 * A compare tool for comparing two assignment submssions and generating a
 * report of them. Use plaggie.properties file to set some properties of the
 * tool. Prints (and prompts) information about the status to the standard
 * output. For command line usage, see README_PLAGGIE file. For configuration,
 * see the configuration file (plaggie.properties).
 *
 */
public class PlaggieUFCG {

	private static File ANALYSIS_FOLDER;

	private static Configuration config;

	private static Runtime runtime;

	private static CodeTokenizer codeTokenizer;

	public static ArrayList detResults = new ArrayList();

	/**
	 * Recebe a pasta contendo todas as subpastas, arquivos de configuracao, etc
	 * ja pronta para rodar a analise. Cria arquivos, pastas e relatorios todos
	 * nessa pasta de analise.
	 * 
	 * @param analysisFolder
	 * @throws Exception
	 */
	public PlaggieUFCG(File analysisFolder, Properties prop) throws Exception {
		ANALYSIS_FOLDER = analysisFolder;
		configure(prop);
	}

	/**
	 * Initializes the config object.
	 * @throws ConfigurationException 
	 */
	private void configure(Properties prop) throws ConfigurationException{
		config = new Configuration(prop);
	}

	/**
	 * Generates a FilenameFilter according to the configuration.
	 */
	private static FilenameFilter generateFilenameFilter() throws Exception {

		// Generate the filename filter
		ArrayList filters = new ArrayList();

		filters.add((FilenameFilter) Class.forName(config.filenameFilter).newInstance());
		filters.add(new ExcludeFilenameFilter(config.excludeFiles));
		filters.add(new SubdirectoryFilter(config.excludeSubdirectories));

		FilenameFilter filter = new MultipleFilenameFilter(filters);
		return filter;
	}

	/**
	 * Returns a list of submissions, that can be found in the directory given
	 * as a parameter. The exact format of the directory hierarchy depends on
	 * the configuration, especially parameter severalSubmissionDirectories.
	 */
	private static ArrayList getDirectorySubmissions(File directory) throws Exception {
		// quando passado apenas um parametro ele se todna a pasta com as
		// submissoes a serem analisadas
		ANALYSIS_FOLDER = directory;

		File[] files = directory.listFiles();
		ArrayList submissions = new ArrayList();

		FilenameFilter filter = generateFilenameFilter();

		int c = 1;

		System.out.print("Generating submissions:");

		for (int i = 0; i < files.length; i++) {
			if ((c % 10) == 0) {
				System.out.print("." + c);
			}
			c++;
			if (files[i].isDirectory()) {
				if (!config.severalSubmissionDirectories) {
					Debug.println("Adding directory submission: " + files[i].getPath());
					DirectorySubmission dirS = new DirectorySubmission(files[i], filter, config.useRecursive);
					submissions.add(dirS);
					Stats.incCounter("submissions");
				} else {
					File subDir = new File(files[i].getPath() + File.separator + config.submissionDirectory);
					if (subDir.isDirectory()) {
						Debug.println("Adding directory submission: " + subDir.getPath());
						DirectorySubmission dirS = new DirectorySubmission(subDir, filter, config.useRecursive);
						submissions.add(dirS);
						Stats.incCounter("submissions");
					}
				}
			} else {
				Debug.println("Adding single file submission: " + files[i].getPath());
				SingleFileSubmission sub = new SingleFileSubmission(files[i]);
				submissions.add(sub);
				Stats.addToDistribution("files_in_submission", 1.0);
				Stats.incCounter("submissions");

			}
		}
		System.out.println("...DONE");

		return submissions;

	}

	/**
	 * Generates a list of detection results using the given list of
	 * submissions. Not all detectoin results are stored in the returned list,
	 * they are filtered out according to some configuration parameters.
	 */
	private static ArrayList generateDetectionResults(ArrayList submissions) throws Exception {

		ArrayList detResults = null;

		// Generate the black list
		HashMap blacklist = new HashMap();
		if (!(config.blacklistFile == null || config.blacklistFile.equals(""))) {
			try {
				File file = new File(ANALYSIS_FOLDER, config.blacklistFile);
				BufferedReader bin = new BufferedReader(new FileReader(file));
				String s;
				Integer dummy = new Integer(0);
				while ((s = bin.readLine()) != null) {
					blacklist.put(s.toUpperCase(), dummy);
				}
			} catch (FileNotFoundException e) {
				System.out.println("Blacklist file " + config.blacklistFile + " not found, no blacklist used.");
			}

		}

		// Create the code excluder
		ArrayList codeExcluders = new ArrayList();

		if (config.excludeInterfaces) {
			codeExcluders.add(new InterfaceCodeExcluder());
		}

		StringTokenizer tokenizer = new StringTokenizer(config.templates, ",");

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			CodeExcluder cE = new ExistingCodeExcluder(createTokenList(token, codeTokenizer),
					config.minimumMatchLength);
			codeExcluders.add(cE);
		}

		CodeExcluder codeExcluder = new MultipleCodeExcluder(codeExcluders);

		TokenSimilarityChecker tokenChecker = new SimpleTokenSimilarityChecker(config.minimumMatchLength, codeExcluder);

		// No file excluders currently used, therefore null's
		SubmissionSimilarityChecker checker;
		if (config.cacheTokenLists) {
			checker = new CachingSimpleSubmissionSimilarityChecker(tokenChecker, codeTokenizer, new HashMap());
		} else {
			checker = new SimpleSubmissionSimilarityChecker(tokenChecker, codeTokenizer);
		}

		// Generate all the submission detection results

		detResults = new ArrayList();

		int totalCalculations = (submissions.size() * (submissions.size() - 1)) / 2;
		Stats.setCounterLimit("similarity_comparisons", totalCalculations);
		Stats.setCounterLimit("submissions", Stats.getCounter("submissions"));
		Stats.setCounterLimit("parsed_files", Stats.getCounter("files_to_parse"));

		final Date startTime = new Date();

		System.out.println("Starting time: " + startTime);
		System.out.println("Similarity value report threshold: " + config.minimumSubmissionSimilarityValue);
		System.out.println("Memory used/free: " + runtime.totalMemory() + " / " + runtime.freeMemory());
		System.out.print("Running " + totalCalculations + " submission comparisons: ");

		/*
		 * TimerTask printStatsTask = new TimerTask() { public void run() {
		 * System.out.println(); Stats.print(System.out);
		 * System.out.println("Memory used/free: "+runtime.totalMemory()+" / "
		 * +runtime.freeMemory()); Date curTime = new Date(); long timeElapsed =
		 * curTime.getTime() - startTime.getTime();
		 * 
		 * int cDone, cTotal;
		 * 
		 * try { cDone = Stats.getCounter("similarity_comparisons"); if (cDone
		 * == 0) { cDone = 1; } cTotal =
		 * Stats.getCounterLimit("similarity_comparisons"); } catch
		 * (StatsException e) {
		 * System.out.println("Estimated end time: Not available"); return; }
		 * 
		 * long timePerComparison = timeElapsed / cDone;
		 * 
		 * long timeLeft = (cTotal-cDone) * timePerComparison;
		 * 
		 * long endTime = curTime.getTime() + timeLeft;
		 * 
		 * Date endDate = new Date(endTime);
		 * System.out.println("Estimated end time: "+endDate); } };
		 * 
		 * Timer timer = new Timer(); timer.schedule(printStatsTask, 0, 5000);
		 */
		for (int i = 0; i < submissions.size(); i++) {
			for (int j = 0; j < i; j++) {
				Submission subA = (Submission) submissions.get(i);
				Submission subB = (Submission) submissions.get(j);
				SubmissionDetectionResult detResult = new SubmissionDetectionResult(subA, subB, checker,
						config.minimumFileSimilarityValueToReport);

				Stats.addToDistribution("submission_similarities_a", detResult.getSimilarityA());
				Stats.addToDistribution("submission_similarities_b", detResult.getSimilarityB());
				Stats.addToDistribution("submission_similarities",
						detResult.getSimilarityA() * detResult.getSimilarityB());
				Stats.addToDistribution("maximum_file_similarities", detResult.getMaxFileSimilarityProduct());
				Stats.addToDistribution(subA.getName(), detResult.getSimilarityA());
				Stats.addToDistribution(subB.getName(), detResult.getSimilarityB());
				boolean onBlacklist = false;
				if (blacklist.get(detResult.getSubmissionA().getName().toUpperCase()) != null) {
					detResult.setBlacklistedA(true);
					onBlacklist = true;
				}
				if (blacklist.get(detResult.getSubmissionB().getName().toUpperCase()) != null) {
					detResult.setBlacklistedB(true);
					onBlacklist = true;
				}

				boolean alreadyAdded = false;
				if (onBlacklist) {
					Stats.incCounter("blacklisted_detection_results");
					if (config.showAllBlacklistedResults) {
						detResults.add(detResult);
						alreadyAdded = true;
					}
				}

				if ((detResult.getSimilarityA() >= config.minimumSubmissionSimilarityValue)
						|| (detResult.getSimilarityB() >= config.minimumSubmissionSimilarityValue)) {
					if (!alreadyAdded) {
						detResults.add(detResult);
					}
					Stats.incCounter("similarity_over_threshold");
				}
				Stats.incCounter("similarity_comparisons");

			}
		}
		// timer.cancel();
		System.out.println();
		System.out.println("Ending time: " + (new Date()));
		System.out.println();
		Stats.print(System.out);

		return detResults;

	}

	/**
	 * Reads the detection results from a file (name read from configuration
	 * file), if they have been stored in it earlier.
	 */
	private static ArrayList readDetectionResultsFromFile() throws Exception {
		ArrayList detResults = null;
		// Read the submission detection result set from the resultFile
		System.out.println("Reading results from file " + config.resultFile);

		PlagSym.init();

		File file = new File(ANALYSIS_FOLDER, config.resultFile);
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));

		detResults = (ArrayList) ois.readObject();

		Stats.setInstance((Stats) ois.readObject());

		ois.close();
		System.out.println("Done.");

		return detResults;
	}

	/**
	 * Stores the given detection results in a file.
	 */
	private static void storeDetectionResultsInFile(ArrayList detResults) throws Exception {
		System.out.println("Generating result file " + config.resultFile);
		File file = new File(ANALYSIS_FOLDER, config.resultFile);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(detResults);
		oos.writeObject(Stats.getInstance());
		oos.close();
		System.out.println("Done.");
	}

	/**
	 * Generates a report of the given detection results and submissions. Exact
	 * format of the report depends on the configuration parameters.
	 */
	private static void generateReport(ArrayList detResults, ArrayList submissions) throws Exception {
		ReportGenerator fileRepGen;

		if (config.fullFileDetectionReports) {
			fileRepGen = new SimpleTextReportGenerator(System.out, config.printTokenLists, codeTokenizer);
		} else if (config.statFileDetectionReports) {
			fileRepGen = new SimpleTextSubmissionReportGenerator.OnlyStatsReportGenerator(System.out);
		} else {
			fileRepGen = null;
		}

		boolean generateFileDetectionReports = true;
		if (fileRepGen == null) {
			generateFileDetectionReports = false;
		}

		SubmissionReportGenerator repGen = null;
		if (config.htmlReport) {
			File dir = new File(ANALYSIS_FOLDER, config.htmlDir);
			repGen = new SimpleHtmlSubmissionReportGenerator(dir, codeTokenizer,
					config.maximumDetectionResultsToReport);
		} else {
			repGen = new SimpleTextSubmissionReportGenerator(System.out, generateFileDetectionReports, fileRepGen);

		}

		System.out.println("************************************************************************");
		System.out.println("   Plaggie report");
		System.out.println("************************************************************************");

		HtmlPrintable statsPrinter = new HtmlPrintable() {
			public void printHtmlReport(PrintStream out) {
				try {

					out.println("<H3>Distribution of similarities, Average: "
							+ Stats.getPercentage(Stats.getDistributionAverage("submission_similarities")) + "</H3>");
					Stats.printHtmlDistribution(out, "submission_similarities", 0.0, 1.0, 0.1, true, '#', 80);
					out.println("<H3>Distribution of similarities A, Average: "
							+ Stats.getPercentage(Stats.getDistributionAverage("submission_similarities_a")) + "</H3>");
					Stats.printHtmlDistribution(out, "submission_similarities_a", 0.0, 1.0, 0.1, true, '#', 80);
					out.println("<H3>Distribution of similarities B, Average: "
							+ Stats.getPercentage(Stats.getDistributionAverage("submission_similarities_b")) + "</H3>");
					Stats.printHtmlDistribution(out, "submission_similarities_b", 0.0, 1.0, 0.1, true, '#', 80);
					out.println("<H3>Distribution of maximum file similarities, Average: "
							+ Stats.getPercentage(Stats.getDistributionAverage("maximum_file_similarities")) + "</H3>");
					out.println("<P>If there are a lot of high similarity values (i.e. > 20%) in this distribution, "
							+ "there is a reason to suspect, that some code provided to the students "
							+ "with the exercise definition or somewhere else is not excluded from the "
							+ "detection. You can use the code exclusion parameters in the configuration "
							+ "file to exclude code from the detection algorithm.");
					Stats.printHtmlDistribution(out, "maximum_file_similarities", 0.0, 1.0, 0.1, true, '#', 80);

					out.println("<H3>Files per submission</H3>");
					Stats.printHtmlDistribution(out, "files_in_submission",
							Stats.getDistributionMin("files_in_submission"),
							Stats.getDistributionMax("files_in_submission"), 1.0, false, '#', 80);
					out.println("<H3>Counters</H3>");
					Stats.printHtml(out);
				} catch (Exception e) {
					out.println("Exception while printing statistics:");
					e.printStackTrace(out);
				}
			}

		};

		repGen.generateReport(detResults, submissions, config, statsPrinter);

		System.out.println("************************************************************************");
		System.out.println("   End of Plaggie report");
		System.out.println("************************************************************************");

	}

	/**
	 * Returns the token list of the given file.
	 */
	private static TokenList createTokenList(String filename, CodeTokenizer tokenizer) throws Exception {

		TokenList tokens = tokenizer.tokenize(new File(filename));

		return tokens;
	}

	/**
	 * Creates the directory for generating an HTML report. If directory exists
	 * and is not empty, prompts user for its removing. If it doesn't exist at
	 * all, prompts user for its creation.
	 */
	private static boolean createHtmlDirectory() throws Exception {

		String sDir = config.htmlDir;
		// File file = new File(ANALYSIS_FOLDER,config.htmlDir);
		File dir = new File(ANALYSIS_FOLDER, sDir);

		BufferedReader b = new BufferedReader(new InputStreamReader(System.in));

		// Check the existence of HTML directory
		if (!dir.exists()) {
			dir.mkdir();
			return true;
			/*
			 * System.out.print("HTML report directory " + sDir +
			 * " does not exist. Do you want to create it ? [Y]/N :"); String
			 * input = b.readLine(); if ((input.length() == 0) ||
			 * (input.toUpperCase().charAt(0) == 'Y')) { dir.mkdir(); return
			 * true; } else { return false; }
			 */
		}

		File[] files = dir.listFiles();

		if (files.length == 0) {
			return true;
		} else {
			// The HTML directory is not empty
			removeDirectory(dir, false);
			return true;
			/*
			 * System.out.print("HTML report directory " + sDir +
			 * " is not empty. Do you want to delete the directory contents and generate a new report? [Y]/n :"
			 * ); String input = b.readLine(); if ((input.length() == 0) ||
			 * (input.toUpperCase().charAt(0) == 'Y')) { removeDirectory(dir,
			 * false); return true; } else { return false; }
			 */ }
	}

	/**
	 * Removes (recursively) the given directory. If removeBaseDir is true, also
	 * the given directory is removed.
	 */
	private static void removeDirectory(File dir, boolean removeBaseDir) throws Exception {
		File[] iter = dir.listFiles();
		for (int i = 0; i < iter.length; i++) {
			File f = iter[i];
			if (f.isDirectory()) {
				removeDirectory(f, true);
			} else {
				if (!f.delete()) {
					throw new IOException("Unable to delete file " + f.getPath());
				}
			}
		}
		if (removeBaseDir) {
			if (!dir.delete()) {
				throw new IOException("Unable to delete directory " + dir.getPath());
			}
		}
	}

	public void run(List<String> fileNames) throws Exception{
			Stats.setInstance(new Stats()); //zerar todos os contadores
			runtime = Runtime.getRuntime();

			//printing counters
			System.out.println("PRINTING COUNTERS");
			Stats.getInstance().counters.forEach((K,V) ->{
				System.out.println(" - " + K + " = " + V);
			});
			//printing countersLimits
			System.out.println("PRINTING COUNTERS LIMITS");
			Stats.getInstance().counterLimits.forEach((K,V) ->{
				System.out.println(" - " + K + " = " + V);
			});
			//printing distributions
			
			// -- Print program info
			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			System.out.println("Running Plaggie - Plagiarism Detection tool");
			System.out.println("Folder: " + ANALYSIS_FOLDER.getAbsolutePath());
			System.out.println("Files to be analysed: ");
			for (String name : fileNames) {
				System.out.println(" - " + name);				
			}

			Debug.setEnabled(config.debugMessages);

			// -- Create or erase the html directory if necessary
			if (config.htmlReport) {
				if (!createHtmlDirectory()) {
					//System.exit(0);
					throw new RuntimeException("Nao foi possivel criar diretorio html");
				}
			}

			// -- Create the code tokenizer object for parsing the source code
			// files
			codeTokenizer = (CodeTokenizer) Class.forName(config.codeTokenizer).newInstance();

			// -- Read and create the submissions, if the results are not
			// -- read from a file
			// ArrayList detResults = null;
			ArrayList submissions = null;

			if ((!config.readResultsFromFile) || (config.readResultsFromFile && config.createResultFile)) {

				Stats.newCounter("submissions");
				Stats.newCounter("parsed_files");
				Stats.newCounter("files_to_parse");
				Stats.newCounter("parse_failures");
				Stats.newCounter("failed_file_comparisons");
				Stats.newCounter("file_comparisons");
				Stats.newCounter("similarity_over_threshold");
				Stats.newCounter("similarity_comparisons");
				Stats.newCounter("blacklisted_detection_results");

				Stats.newDistribution("files_in_submission");
				Stats.newDistribution("submission_similarities");
				Stats.newDistribution("submission_similarities_a");
				Stats.newDistribution("submission_similarities_b");
				Stats.newDistribution("maximum_file_similarities");

				// Create the submissions
				submissions = getDirectorySubmissions(ANALYSIS_FOLDER);

				// -- Create distributions for all the submissions' similarity
				// values
				Iterator iter = submissions.iterator();
				while (iter.hasNext()) {
					Submission sub = (Submission) iter.next();
					Stats.newDistribution(sub.getName());
				}

				// -- Create the detection results
				detResults = generateDetectionResults(submissions);

				if (config.createResultFile) {
					storeDetectionResultsInFile(detResults);
				}
			}

			if (config.readResultsFromFile) {
				// -- Read the results from a file
				detResults = readDetectionResultsFromFile();
			}

			// -- Report the results
			generateReport(detResults, submissions);

	}

}
