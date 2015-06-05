package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.jdom2.JDOMException;

public class MavenUtility {
	
	public static final String SOURCE_FOLDER = "src/main/java";
	public static final String RESOURCE_FOLDER = "src/main/resources";
	public static final String TEST_FOLDER = "src/test/java";
	public static final String TEST_RESOURCE_FOLDER = "src/test/resources";
	public static final String SITE_FOLDER = "src/site/";
	
	private FilesUtility filesUtility;
	private ReportUtility reportUtility;
	public static final String BASIC_POM_FILE = "/project_pom.xml";
	private final String BASIC_HTML_FILE = "/template-report.html";
	private final String FINAL_REPORT_NAME = "/generated-report.html";
	
	private File submissionsDirectory;
	private File mavenHomeFolder;
	private String artifactId;
	private String name;
	private String testCasesJarFileName;
	private String testSuiteFileName;
	
	public MavenUtility(File submissionsFolder, File mavenHomeFolder, String artifactId, String name,
			String testCasesJarFileName, String testSuiteFileName) {
		this.submissionsDirectory = submissionsFolder;
		this.mavenHomeFolder = mavenHomeFolder;
		this.artifactId = artifactId;
		this.name = name;
		this.testCasesJarFileName = testCasesJarFileName;
		this.testSuiteFileName = testSuiteFileName;
		this.filesUtility = new FilesUtility();
		this.reportUtility = new ReportUtility();
	}

	/**
	 * It creates the project folder to be compiled with maven. It first creates a default folder with
	 * student's name. The original environment files are unpacked in the folder. Then, some files
	 * are overwritten with student's ones.
	 * @param correctionZipFile The environment files released to students.
	 * @param studentZipFile The student's file containing the submission.
	 * @param files A list of files and thrisrespective folder where they must be unpacked.
	 * @throws IOException
	 * @throws JDOMException 
	 */
	public File createCompleteProjectFolder(File correctionZipFile, File studentZipFile, List<String> files) throws IOException, JDOMException{
		String projectFolderName = filesUtility.getFileNameWithoutExtension(studentZipFile);
		File projectFolder = createDefaultProjectFolder(correctionZipFile, projectFolderName);
		unzipSubmittedFiles(projectFolder, studentZipFile, files);
		createPOMFile(submissionsDirectory, studentZipFile);
				
		return projectFolder;
	}

	private void createPOMFile(File submissionDirectory, File studentZipFile) throws IOException, JDOMException {
		
		XMLFileUtility xmlFu = new XMLFileUtility();
		xmlFu.createPOMFile(BASIC_POM_FILE, submissionDirectory, studentZipFile, 
				this.artifactId, this.name, this.testCasesJarFileName, this.testSuiteFileName);
	}

	public void executeMaven(File projectFolder){
		Invoker invoker = new DefaultInvoker();
		System.setProperty("maven.home", this.mavenHomeFolder.getAbsolutePath());
		if (projectFolder.isDirectory()) {
			InvocationRequest request = new DefaultInvocationRequest();
			//request.setPomFile(new File(projectFoder, "./pom.xml"));
			//request.setGoals(Arrays.asList("surefire-report:report-only"));
			request.setGoals(Arrays.asList("site"));
			request.setBaseDirectory(projectFolder);
			try {
				invoker.execute(request);
			} catch (MavenInvocationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateReport(File submissionsFolder, File targetFolder) throws IOException, JDOMException{
		
		String finalPathHtml = targetFolder.getAbsolutePath() + FINAL_REPORT_NAME;
		reportUtility.createAndSaveReport(submissionsFolder, BASIC_HTML_FILE, finalPathHtml);
		
	}
	
	/**
	 * Zip file is the student's submission. Its name is the student's name. 
	 * @param envZipFile
	 * @throws IOException 
	 */
	private File createDefaultProjectFolder(File envZipFile, String projectFolderName) throws IOException{
		
		File projectDir = new File(submissionsDirectory, projectFolderName);
		
		if (!submissionsDirectory.exists()) {
			submissionsDirectory.mkdir();
        }
		if (!projectDir.exists()) {
            projectDir.mkdir();
        }
		
        File sourceFolder = new File(projectDir,SOURCE_FOLDER);
        File resourceFolder = new File(projectDir,RESOURCE_FOLDER);
        File testFolder = new File(projectDir,TEST_FOLDER);
        File testResourceFolder = new File(projectDir,TEST_RESOURCE_FOLDER);
        File siteFolder = new File(projectDir,SITE_FOLDER);
        
        sourceFolder.mkdirs();
        resourceFolder.mkdirs();
        testFolder.mkdirs();
        testResourceFolder.mkdirs();
        siteFolder.mkdirs();
        
        //filesUtility.unzip(envZipFile, sourceFolder);
        filesUtility.unzip(envZipFile, projectDir);
		
        return projectDir;
	}
	
	/**
	 * Zip file is the student's submission. Its name is the student's name. 
	 * @param studentZipFile
	 * @throws IOException 
	 */
	private void unzipSubmittedFiles(File projectDir, File studentZipFile, List<String> files) throws IOException{
			
		//File sourceFolder = new File(projectDir,SOURCE_FOLDER);
           
        //filesUtility.unzip(studentZipFile, sourceFolder, files);
		filesUtility.unzip(studentZipFile, projectDir, files);
		
	}
}
