package br.edu.ufcg.ccc.leda.util;

import java.io.IOException;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XMLFilesUtilityTest {
	private static final String xmlFilePath = "D:\\tmp\\test.xml";

	public static void main(String[] args) throws IOException {
		URL url = URL.class.getClass().getResource("/project_pom.xml");
		// InputStream is = url.openStream();

		// InputStream is =
		// XMLFilesUtilityTest.class.getResourceAsStream(xmlFilePath);
		// ResourceBundle rb = ResourceBundle.getBundle(xmlFilePath);

		SAXBuilder saxBuilder = new SAXBuilder();

		// File xmlFile = new File(xmlFilePath);

		try {
			XMLFileUtility fu = new XMLFileUtility();
			String path = url.getFile().substring(1);

			Document doc = fu.loadXMLFile("/project_pom.xml");
			int i = 0;
			// Object loaded = fu.getElement(doc, "employee");

			// loaded = fu.getElementList(doc, "employee");
			/*
			 * if(loaded instanceof List){ for (Element item :
			 * (List<Element>)loaded) { System.out.println("Name: " +
			 * item.getName()); System.out.println("Value: " + item.getName());
			 * } }
			 */
			// Element parent = fu.loadElementList(xmlFilePath, "dependencies");
			/*
			 * Element parent = fu.getElement(doc, "dependencies");
			 * fu.insertElement(doc, parent); fu.writeXMLFile(doc,xmlFilePath);
			 */

			/*
			 * if(loaded instanceof List){ for (Element item :
			 * (List<Element>)loaded) { System.out.println("Name: " +
			 * item.getName()); List<Element> children = item.getChildren(); for
			 * (Element element : children) {
			 * System.out.println("  Children name: " + element.getName());
			 * System.out.println("  Children value: " + element.getValue()); }
			 * } } loaded = fu.loadElement(xmlFilePath, "dependency");
			 */

			/*
			 * Document document = (Document) saxBuilder.build(xmlFile); Element
			 * rootElement = document.getRootElement(); //List listElement =
			 * rootElement.getChildren("employee"); List<Element> listElement =
			 * rootElement.getChildren();
			 * 
			 * for (int i = 0; i < listElement.size(); i++) { Element node =
			 * (Element) listElement.get(i); System.out.println("First Name : "
			 * + node.getChildText("firstname"));
			 * System.out.println("Last Name : " +
			 * node.getChildText("lastname")); System.out.println("Email : " +
			 * node.getChildText("email")); System.out.println("Department : " +
			 * node.getChildText("department")); System.out.println("Salary : "
			 * + node.getChildText("salary")); System.out.println("Address : " +
			 * node.getChildText("address")); }
			 */

		} catch (IOException io) {

			System.out.println(io.getMessage());

		} catch (JDOMException jdomex) {

			System.out.println(jdomex.getMessage());

		}

	}
}
