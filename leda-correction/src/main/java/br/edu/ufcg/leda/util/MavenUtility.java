package br.edu.ufcg.leda.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import br.edu.ufcg.leda.commons.user.Student;
import br.edu.ufcg.leda.commons.util.FilesUtility;
import br.edu.ufcg.leda.commons.util.XMLFileUtility;

public class MavenUtility {

	public static final String SOURCE_FOLDER = "src/main/java";
	public static final String TEST_FOLDER = "src/test/java";
	public static final String MAVEN_OUTPUT_LOG = "maven-output.txt";

	private FilesUtility filesUtility;
	private TestReportUtility reportUtility;
	public static final String BASIC_POM_FILE = "/project_pom.xml";

	private File submissionsDirectory;
	private File mavenHomeFolder;
	private String artifactId;
	private String name;
	private String testCasesJarFileName;
	private String testSuiteFileName;

	public MavenUtility(File submissionsFolder, File mavenHomeFolder,
			String artifactId, String name, String testCasesJarFileName,
			String testSuiteFileName) {
		this.submissionsDirectory = submissionsFolder;
		this.mavenHomeFolder = mavenHomeFolder;
		this.artifactId = artifactId;
		this.name = name;
		this.testCasesJarFileName = testCasesJarFileName;
		this.testSuiteFileName = testSuiteFileName;
		this.filesUtility = new FilesUtility();
		this.reportUtility = new TestReportUtility();
	}

	/**
	 * It creates the project folder to be compiled with maven. It first creates
	 * a default folder with student's name. The original environment files are
	 * unpacked in the folder. Then, some files are overwritten with student's
	 * ones.
	 * 
	 * @param correctionZipFile
	 *            The environment files released to students.
	 * @param studentZipFile
	 *            The student's file containing the submission.
	 * @param files
	 *            A list of files and thrisrespective folder where they must be
	 *            unpacked.
	 * @throws IOException
	 * @throws JDOMException
	 * @throws SAXException
	 */
	public File createCompleteProjectFolder(File correctionZipFile,
			File studentZipFile, List<String> files) throws IOException,
			JDOMException, SAXException {

		String projectFolderName = filesUtility
				.getFileNameWithoutExtension(studentZipFile);
		// System.out.println("Creating folder: " + projectFolderName);
		File projectFolder = createDefaultProjectFolder(correctionZipFile,
				projectFolderName);
		// System.out.println("Default project folder: " +
		// projectFolder.getAbsolutePath());
		unzipSubmittedFiles(projectFolder, studentZipFile, files);
		createPOMFile(submissionsDirectory, studentZipFile);

		return projectFolder;
	}

	private void createPOMFile(File submissionDirectory, File studentZipFile)
			throws IOException, JDOMException, SAXException {

		XMLFileUtility xmlFu = new XMLFileUtility();
		xmlFu.createPOMFile(BASIC_POM_FILE, submissionDirectory,
				studentZipFile, this.artifactId, this.name,
				this.testCasesJarFileName, this.testSuiteFileName);
	}

	public void executeMaven(File projectFolder) {
		Invoker invoker = new DefaultInvoker();
		System.setProperty("maven.home", this.mavenHomeFolder.getAbsolutePath());
		if (projectFolder.isDirectory()) {
			InvocationRequest request = new DefaultInvocationRequest();
			// request.setPomFile(new File(projectFoder, "./pom.xml"));
			// request.setGoals(Arrays.asList("surefire-report:report-only"));
			request.setGoals(Arrays.asList("site", "--log-file "
					+ MAVEN_OUTPUT_LOG));
			request.setBaseDirectory(projectFolder);
			request.setBatchMode(false);
			
			try {
				invoker.execute(request);
			} catch (MavenInvocationException e) {
				e.printStackTrace();
			}
		}
	}

	public void generateReport(File submissionsFolder, File targetFolder, Map<Integer,List<Student>> alunos)
			throws IOException, JDOMException {

		//TODO tem que fazer alguma coisa com a lista de alunos, como por exemplo salvar 
		//o json dos testes para o calculo de proximidade e tambem gerar o relatorio para 
		//a turma completa. se o aluno nao submeteu entao aparece la que nao submeteu.
		// talvez seja melhor gerar o json e depois construir o report baseado no json.
		//String finalPathHtml = targetFolder.getAbsolutePath()
		//		+ FINAL_REPORT_NAME;
		//reportUtility.createAndSaveReport(submissionsFolder, BASIC_HTML_FILE,
		//		targetFolder, finalPathHtml);
		reportUtility.createAndSaveJsonTestReport(submissionsFolder,alunos);
		
		//precisa criar e salvar o json para a correcao ser feita
		reportUtility.createAndSaveJsonCorrectionReport(submissionsFolder, "", alunos);
	}
	
	

	/**
	 * Zip file is the student's submission. Its name is the student's name.
	 * 
	 * @param envZipFile
	 * @throws IOException
	 */
	private File createDefaultProjectFolder(File envZipFile,
			String projectFolderName) throws IOException {

		File projectDir = new File(submissionsDirectory, projectFolderName);

		if (!submissionsDirectory.exists()) {
			submissionsDirectory.mkdirs();
		}
		if (!projectDir.exists()) {
			projectDir.mkdirs();
		}

		File sourceFolder = new File(projectDir, SOURCE_FOLDER);
		// File resourceFolder = new File(projectDir,RESOURCE_FOLDER);
		File testFolder = new File(projectDir, TEST_FOLDER);
		// File testResourceFolder = new File(projectDir,TEST_RESOURCE_FOLDER);
		// File siteFolder = new File(projectDir,SITE_FOLDER);

		sourceFolder.mkdirs();
		// resourceFolder.mkdirs();
		testFolder.mkdirs();
		// testResourceFolder.mkdirs();
		// siteFolder.mkdirs();

		filesUtility.unzip(envZipFile, projectDir);

		return projectDir;
	}

	/**
	 * Zip file is the student's submission. Its name is the student's name.
	 * 
	 * @param studentZipFile
	 * @throws IOException
	 */
	private void unzipSubmittedFiles(File projectDir, File studentZipFile,
			List<String> files) throws IOException {

		// File sourceFolder = new File(projectDir,SOURCE_FOLDER);

		// filesUtility.unzip(studentZipFile, sourceFolder, files);
		filesUtility.unzip(studentZipFile, projectDir, files);

	}
}
