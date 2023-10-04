package br.edu.ufcg.leda.util;

public class MavenUtilityTest {
	/* 
	public static void main(String[] args) throws IOException, JDOMException {
		File mavenHomeFolder = new File("D:\\apache-maven-3.2.5");
		File projectsFolder = new File(
				"D:\\UFCG\\2016.1\\disciplinas\\leda\\submissions\\PP1-02\\subs");
		File targetFolder = new File(projectsFolder.getParentFile(),"target");

		MavenUtility mu = new MavenUtility(projectsFolder, mavenHomeFolder,
				"br.edu.ufcg.ccc.leda", "Correction Tool Client",
				"Prova-BSTComComparator-tests.jar", "TestSuite.java");
		
		Properties prop = Utilities.loadProperties();
		String url = prop.getProperty(Utilities.SUBMISSION_SERVER_URL);
		System.out.println("Obtaining student lists from server " + url);
		String urlAllStudents = url + "/alunosJson";
		Map<String,Student> alunos = Util.getAllStudents(urlAllStudents);
		alunos.forEach((m,a) -> System.out.println(m + "-" + a.getNome()));
		mu.generateReport(projectsFolder, targetFolder, alunos);
		/*
		// File correctionZipFile = new
		// File("D:\\tmp\\submissions\\Gnomesort-Combsort-environment.zip");
		File correctionZipFile = new File(
				"D:\\UFCG\\2015.2\\disciplinas\\leda\\PP2\\Prova-BSTComComparator-correction-env.zip");
		ArrayList<String> al = new ArrayList<String>();
		al.add("BSTWithComparatorImpl.java");
		// al.add("Combsort.java");
		File studentZipFile = new File(
				"D:\\UFCG\\2015.2\\disciplinas\\leda\\PP2\\subs\\ADSON CESAR DA SILVA.zip");
		mu.createCompleteProjectFolder(correctionZipFile, studentZipFile, al);
		System.out.println("Creation completed");
		
		 * File projectFoder = mu.createCompleteProjectFolder(envZipFile,
		 * studentZipFile, al); System.out.println("Folder created: " +
		 * projectFoder.getAbsolutePath()); mu.executeMaven(projectFoder);
		 * System.out.println("Finished...");
		 
	}
	*/
}
