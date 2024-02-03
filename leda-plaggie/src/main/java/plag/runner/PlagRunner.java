package plag.runner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import br.edu.ufcg.leda.commons.util.XMLFileUtility;
import plag.parser.CachingDetectionResult;
import plag.parser.SubmissionDetectionResult;
import plag.parser.plaggie.PlaggieUFCG;

public class PlagRunner {

	private static final String CONF_FILE = "/plaggie.properties";
	public static final String ANALYSIS_FOLDER_NAME = "analysis";
	private File parentAnalysisFolder;
	private String atividadeId;
	private File currentSemesterFolder;
	private Properties properties;
	//private static double THRESHOLD = 0.99;

	public PlagRunner(File currentSemesterFolder, String atividadeId, File analysisFolderInServer)
			throws URISyntaxException, IOException {
		this.properties = loadConfigFile();
		this.atividadeId = atividadeId;
		this.currentSemesterFolder = currentSemesterFolder;
		this.parentAnalysisFolder = new File(currentSemesterFolder, ANALYSIS_FOLDER_NAME);
	}

	public PlagRunner(Properties prop, File currentSemesterFolder, 
			String atividadeId, File analysisFolderInServer)
			throws URISyntaxException, IOException {
		this.properties = prop;
		this.atividadeId = atividadeId;
		this.currentSemesterFolder = currentSemesterFolder;
		this.parentAnalysisFolder = new File(currentSemesterFolder, ANALYSIS_FOLDER_NAME);
	}

	public File getAnalysisFolder() {
		return parentAnalysisFolder;
	}

	public void setAnalysisFolder(File analysisFolder) {
		this.parentAnalysisFolder = analysisFolder;
	}

	public File getCurrentSemesterFolder() {
		return currentSemesterFolder;
	}

	public void setCurrentSemesterFolder(File currentSemesterFolder) {
		this.currentSemesterFolder = currentSemesterFolder;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getAtividadeId() {
		return atividadeId;
	}

	public void setAtividadeId(String atividadeId) {
		this.atividadeId = atividadeId;
	}

	/**
	 * a analise deve proceder apenas quando existir o projeto de correcao
	 * criado e executado para a atividade. o procedimento eh pegar as os
	 * arquivos a serem considerados diretamente do arquivo pom.xml. depois vai
	 * em todas as turmas e copia os arquivos para comparacao. a pasta das
	 * analises deve ser leda-upload/<current-semester>/analysis cada analise
	 * vai gerar um arquivo .json com as similaridades entre matriculas A
	 * plotagem pode ser por nome
	 * 
	 * @param atividadeId
	 *            o ID da atividade sem a turma. Tem formato RRX,PPX,RXX,PRX,PFX
	 * @throws Exception
	 */
	public List<SimilarityAnalysisResult> runPlagiarismAnalysis(double threshold) throws Exception {
		List<SimilarityAnalysisResult> result = new ArrayList<SimilarityAnalysisResult>();
		List<String> fileNames = new ArrayList<String>();
		// PASTAS CONTENDO AS SUBMISSOES
		List<File> atividadeSubFolders = new ArrayList<File>();

		// pega todas as pastas de todas as turmas dessa atividade
		// coloca os arquivos a serem comparados numa lista
		File[] atividades = this.currentSemesterFolder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().startsWith(atividadeId);
			}
		});
		if (atividades.length > 0) {
			for (int i = 0; i < atividades.length; i++) {
				File atividadeTurma = atividades[i];
				XMLFileUtility xmlUtil = new XMLFileUtility();
				File pomFile = new File(atividadeTurma, "pom.xml");
				if (pomFile.exists()) {
					// TODO tem dependencia com a pasta de submissoes
					File submissoesAtividade = new File(atividadeTurma, "subs");
					if (submissoesAtividade.exists()) {
						atividadeSubFolders.add(submissoesAtividade);
					}
					Document xmlDoc = xmlUtil.loadXMLFile(pomFile.getAbsolutePath());
					Element fileNamesElement = xmlUtil.getElement(xmlDoc.getRootElement(), "fileNames");
					List<Element> files = fileNamesElement.getChildren().stream().filter(e -> e instanceof Element)
							.collect(Collectors.toList());
					for (Element element : files) { // varre os elementos que
													// nao
													// <name>Classe.java</name>
						String className = element.getText();
						fileNames.add(className);
					}
				}
			}
			fileNames = fileNames.stream().distinct().collect(Collectors.toList());
		}

		result = runPlagiarismAnalysis(parentAnalysisFolder, atividadeId, atividadeSubFolders, fileNames);
		result = result.stream().filter(sar -> sar.getSimilarity() > threshold).collect(Collectors.toList());

		return result;
	}

	/**
	 * 
	 * @param analysisFolder
	 * @param atividadeId
	 *            o id da atividade em formato reduzido (sem turma)
	 * @param submissionsSubFolderName
	 * @param fileNames
	 * @return
	 * @throws Exception
	 */
	public ArrayList<SimilarityAnalysisResult> runPlagiarismAnalysis(File parentAnalysisFolder, String atividadeId,
			List<File> submissionFolders, List<String> fileNames) throws Exception {
		// cria a pasta para rodar a analise da atividade
		File realAnalysisFolder = new File(parentAnalysisFolder, atividadeId);
		for (File submissionFolder : submissionFolders) {
			prepareAnalysisFolder(realAnalysisFolder, submissionFolder, fileNames);
		}
		// rodar o plag em analiseAtividadeFolder
		ArrayList detResults = this.plagAnalysis(realAnalysisFolder, fileNames);

		return this.processResults(detResults);
	}

	// pode ser que as configuracoes sejam um parametro ou algumas delas possam
	// ser passadas por parametro para o MOJO
	private Properties loadConfigFile() throws URISyntaxException, IOException {
		Properties prop = new Properties();

		String confFile = CONF_FILE;
		InputStream is = PlagRunner.class.getResourceAsStream(CONF_FILE);
		// URI uri = new URI(is.)
		URL url = PlagRunner.class.getResource(CONF_FILE);
		File fConfFile = new File(url.toURI().getPath());
		if (!fConfFile.exists()) {
			throw new RuntimeException("Configuration file " + confFile + " not found!");
			// System.exit(1);
		}
		prop.load(url.openStream());

		return prop;
	}

	/**
	 * 
	 * Recebe a pasta de submissoes dos alunos e compara os arquivos fornecidos
	 * em determinada lista de arquivos dada por fileNames.
	 * 
	 * @param submissionsFolder
	 * @param fileNames
	 * @throws Exception
	 */
	public ArrayList plagAnalysis(File realAnalysisFolder, List<String> fileNames) throws Exception {
		// precisa: usar uma pasta temporaria (cria uma pasta analysis na pasta
		// da atividade)
		// criar uma pasta para cada aluno (pode ser a matricula)
		// copiar apenas os arquivos especificos informados na lista
		// para a pasta do aluno correspondente.
		// prepareAnalysisFolder(submissionsFolder, fileNames);

		// depois roda a analise na pasta de analises gerada. cria uma
		// configuracao
		// padrao ou especifica
		PlaggieUFCG plag = new PlaggieUFCG(realAnalysisFolder, this.properties);
		plag.run(fileNames);

		return plag.detResults;
	}

	/**
	 * Cria a pasta de analise de um estudante e ja copia os arquivos pra la.
	 * //nome da pasta de analises pode ser uma constante global //cria uma
	 * subpasta "analysis" na pasta pai de submissionsFolder //varre as
	 * subpastas de submissionsFolder (com as submissoes dos alunos) //copia de
	 * cada subpasta os arquivos informados em fileNames (busca recursiva)
	 * 
	 * @param studentFolder
	 * @return
	 * @throws IOException
	 */
	private void prepareAnalysisFolder(File atividadeAnalysisFolder, File submissionsFolder, List<String> fileNames)
			throws IOException {

		//vai pegar as apstas dos alunos
		File[] subFolders = submissionsFolder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) { //filtras apenas as pastas que possuem subpasta target/site
				File successfullCompilation = 
						new File(pathname,"target" + File.separator + "site");
				return pathname.isDirectory() && successfullCompilation.exists();
			}
		});
		if (subFolders.length > 0) {
			for (int i = 0; i < subFolders.length; i++) {
				File studentFolder = subFolders[i];
				File studentAnalysisSubFolder = new File(atividadeAnalysisFolder, studentFolder.getName());
				if (!studentAnalysisSubFolder.exists()) {
					studentAnalysisSubFolder.mkdirs();
				}
				for (String fileName : fileNames) {
					File found = searchFile(studentFolder, fileName);
					if (found != null) {
						// copia normalmente para a pasta do aluno
						// usar Files.copy
						String matricula = getStudentAnalysisFolderName(found);
						if (matricula != null) {
							File studentFileOut = new File(studentAnalysisSubFolder, found.getName());
							Files.copy(found.toPath(), studentFileOut.toPath(), StandardCopyOption.REPLACE_EXISTING);
						}
					}
				}
			}
		}

	}

	/**
	 * O nome do arquivo file tem o padrao $/matricula-NOME/$ onde matrcula eh
	 * um numero de 9 digitos. depois vem o menos e depois o nome
	 * 
	 * @param file
	 * @return
	 */
	protected static String getStudentAnalysisFolderName(File file) {
		String matricula = null;
		Pattern padrao = Pattern.compile("(.*)([0-9]{9}-)(.*)");
		Matcher matcher = padrao.matcher(file.getAbsolutePath());
		if (matcher.matches()) {
			int start = matcher.start(2);
			int end = matcher.end(2);
			matricula = file.getAbsolutePath().substring(start, end - 1);
		}

		return matricula;
	}

	/**
	 * Busca um arquivo em uma pasta recursivamente e retorna o arquivo ou null.
	 * 
	 * @param folder
	 * @param fileName
	 * @return
	 */
	private File searchFile(File folder, String fileName) {
		File found = null;
		if (folder.isDirectory()) {
			// pega todos os arquivos normais e tenta ver se fileName esta la
			File[] realFiles = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isFile() && pathname.getName().endsWith(fileName);
				}
			});

			if (realFiles.length == 1) {
				found = realFiles[0];
			} else { // nao esta na pasta atual e deve buscar nas subpastas
						// pega todos as subpastas e tenta ver se fileName esta
						// la recursivamente
				File[] subFolders = folder.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return pathname.isDirectory();
					}
				});
				if (subFolders.length > 0) {
					for (int i = 0; i < subFolders.length; i++) {
						found = searchFile(subFolders[i], fileName);
						if (found != null) {
							break;
						}
					}
				}
			}
		}
		return found;
	}

	private ArrayList<SimilarityAnalysisResult> processResults(ArrayList results) {
		ArrayList<SimilarityAnalysisResult> analysisResults = new ArrayList<SimilarityAnalysisResult>();
		if (results.size() > 0) {
			results.forEach(sdr -> {
				SubmissionDetectionResult result = (SubmissionDetectionResult) sdr;
				Collection fileResults = result.getFileDetectionResults();
				fileResults.forEach(o -> {
					if (o instanceof CachingDetectionResult) {
						CachingDetectionResult r = (CachingDetectionResult) o;
						try {
							SimilarityAnalysisResult newResult = new SimilarityAnalysisResult(r.getFileA(),
									//r.getFileB(), Math.max(r.getSimilarityA(), r.getSimilarityB()));
									r.getFileB(), result.getMaxFileSimilarityProduct());
							analysisResults.add(newResult);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

			});

		}

		return analysisResults;
	}

	public Document loadXMLFile(File pomFile) throws IOException, JDOMException {
		SAXBuilder saxBuilder = new SAXBuilder();
		// InputStream is = XMLFileUtility.class.getResourceAsStream(filePath);
		Document document = saxBuilder.build(pomFile);

		return document;
	}

	public static void main(String[] args) throws Exception {
		PlagRunner pr = new PlagRunner(new File("D:\\trash2\\leda-upload\\2017.1"), "PP1",
				new File("D:\\UFCG\\leda\\leda-tools\\leda-submission-server\\public\\reports\\analysis"));
		List<SimilarityAnalysisResult> results = pr.runPlagiarismAnalysis(0.99);
		int i = 1;
		for (SimilarityAnalysisResult r : results) {
			System.out.println(i++ + " File: " + r.getFileStudent1().getName() + ": (" + r.getMatriculaStudent1() + ","
					+ r.getMatriculaStudent2() + ") with similariry " + r.getSimilarity());
		}
	}
}
