package br.edu.ufcg.ccc.leda.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLFileUtility {

	public void createPOMFile(String pathBasicPomFile,
			File submissionDirectory, File studentZipFile, String artifactId,
			String name, String jarTestFileName, String testSuiteClassName)
			throws IOException, JDOMException {

		Document pomFileDoc = loadXMLFile(pathBasicPomFile);
		Element rootElement = pomFileDoc.getRootElement();

		// it adds basic project information
		Element artifId = this.getElement(rootElement, "artifactId");
		artifId.setText(artifactId);
		Element artifName = this.getElement(rootElement, "name");
		artifName.setText(name);

		// it gets the dependencies child
		Element dependencies = this.getElement(rootElement, "dependencies"); // this.getElement(pomFileDoc,
																				// "dependencies");
		// adds the jar dependencies containing tests (if it is the case)
		Element dependency = this.getDependency(dependencies, "test");
		Element systemPath = new Element("systemPath");
		systemPath.setText("${project.basedir}/lib/" + jarTestFileName);
		dependency.addContent(systemPath);

		Element build = this.getElement(rootElement, "build");
		Element plugins = this.getElement(build, "plugins");
		Element surefire = this.getSurefire(plugins, "maven-surefire-plugin");
		Element surefireIncludes = this.getElement(surefire, "includes");
		Element include = new Element("include");
		include.setText("**/" + testSuiteClassName);
		surefireIncludes.addContent(include);

		// it gets the executions to insert a configuration
		// Element plugins = this.getElement(pomFileDoc, "plugins");
		// Element executions = this.getElement(rootElement, "executions");
		// Element execution = this.getElement(executions, "execution");
		// this.addConfiguration(execution, submissionDirectory, envZipFile,
		// studentZipFile, mavenHomeFolder, files);

		// the target pom file will be in the submissionDirectory/studentName
		// folder. The student name folder
		// is extracted from the studentZipFile.
		FilesUtility fu = new FilesUtility();
		String studentFolderName = fu
				.getFileNameWithoutExtension(studentZipFile);
		File targetFolder = new File(submissionDirectory, studentFolderName);
		if (targetFolder.exists()) {
			this.writeXMLFile(pomFileDoc, targetFolder.getAbsolutePath()
					+ File.separator + "pom.xml");
		} else {
			throw new RuntimeException("Target folder:"
					+ targetFolder.getAbsolutePath() + " does not exist!!!");
		}

	}

	public Document loadXMLFile(String filePath) throws IOException,
			JDOMException {
		SAXBuilder saxBuilder = new SAXBuilder();
		// URL url = URL.class.getClass().getResource(filePath);
		// InputStream is = url.openStream();
		// File xmlFile = new File(filePath);
		// System.out.println("Exists: " + xmlFile.exists() + " path: " +
		// xmlFile.getAbsolutePath());
		InputStream is = XMLFileUtility.class.getResourceAsStream(filePath);
		Document document = saxBuilder.build(is);

		return document;
	}

	public Document loadXMLFile(File file) throws IOException, JDOMException {
		SAXBuilder saxBuilder = new SAXBuilder();
		InputStream is = new FileInputStream(file);
		Document document = saxBuilder.build(is);

		return document;
	}

	public Element getElement(Element parent, String name) {
		Element result = null;
		List<Content> content = parent.getContent();
		for (Content content2 : content) {
			if (content2 instanceof Element) {
				if (((Element) content2).getName().equals(name)) {
					result = (Element) content2;
					break;
				} else {
					result = getElement((Element) content2, name);
					if (result != null) {
						break;
					}
				}
			}
		}
		return result;
	}

	private Element getDependency(Element dependencies, String groupId) {
		Element result = null;
		List<Content> content = dependencies.getContent();
		for (Content content2 : content) {
			if (content2 instanceof Element) {
				if (((Element) content2).getName().equals("dependency")) {
					Element testDependency = this.getElement(
							(Element) content2, "groupId");
					if (testDependency.getText().equals(groupId)) {
						result = (Element) content2;
						break;
					}
				}
			}
		}
		return result;
	}

	private Element getSurefire(Element plugins, String artifactId) {
		Element result = null;
		List<Content> content = plugins.getContent();
		for (Content content2 : content) {
			if (content2 instanceof Element) {
				if (((Element) content2).getName().equals("plugin")) {
					Element plugin = this.getElement((Element) content2,
							"artifactId");
					if (plugin.getText().equals(artifactId)) {
						result = (Element) content2;
						break;
					}
				}
			}
		}
		return result;
	}

	/*
	 * private List<Element> getElementList(Document document, String
	 * elementListName) throws IOException, JDOMException{ List<Element> result
	 * = null; Element rootElement = document.getRootElement(); result =
	 * rootElement.getChildren(elementListName);
	 * 
	 * return result; } private void addCorrectionDependency(Element parent)
	 * throws IOException, JDOMException{ Element dependency = new
	 * Element("dependency"); dependency.addContent(new
	 * Element("groupId").setText("br.edu.ufcg.ccc")); dependency.addContent(new
	 * Element("artifactId").setText("leda-correction"));
	 * dependency.addContent(new Element("version").setText("1.0-SNAPSHOT"));
	 * parent.addContent(dependency); }
	 * 
	 * private void addConfiguration(Element parent, File submissionDirectory,
	 * File envZipFile, File studentZipFile, File mavenHomeFolder, List<String>
	 * files){
	 * 
	 * Element configuration = new Element("configuration");
	 * configuration.addContent(new
	 * Element("submissionDirectory").setText(submissionDirectory
	 * .getAbsolutePath())); configuration.addContent(new
	 * Element("environmentZipFile").setText(envZipFile.getAbsolutePath()));
	 * configuration.addContent(new
	 * Element("studentZipFile").setText(studentZipFile.getAbsolutePath()));
	 * configuration.addContent(new
	 * Element("mavenHomeFolder").setText(mavenHomeFolder.getAbsolutePath()));
	 * Element fileNames = new Element("fileNames"); for (String name : files) {
	 * fileNames.addContent(new Element("name").setText(name)); }
	 * configuration.addContent(fileNames); parent.addContent(configuration); }
	 */

	public void writeXMLFile(Document document, String filePath) {
		try {
			FileWriter writer = new FileWriter(filePath);
			XMLOutputter outputter = new XMLOutputter();
			outputter.setFormat(Format.getPrettyFormat());
			outputter.output(document, writer);
			// outputter.output(document, System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
