package fr.oceaneconsulting.dashboard.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.ConfigurationException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.oceaneconsulting.dashboard.exception.XMLFormatException;
import fr.oceaneconsulting.dashboard.model.Project;

public class XMLReader implements ConfigurationReader{
	
	private static final Logger LOGGER = LogManager.getLogger();

	private static XMLReader instance;
	private XPath xPath;

	private XMLReader() {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		xPath = xPathFactory.newXPath();
	}

	public static XMLReader getInstance() {
		if (instance == null) instance = new XMLReader();
		return instance;
	}

	/**
	 * Read a XML file from pathToXML and return a list of Projects
	 * with only the original id and origin
	 *
	 * @param pathToXML The path of the XML file
	 * @return The list of Projects to update (with only the original id and origin)
	 * @throws FileNotFoundException If the file was not found
	 * @throws ConfigurationException If there are any configuration errors in DocumentBuilderFactory
	 * @throws XMLFormatException If the file does not have a valid structure
	 */
	@Override
	public List<Project> readProjects(String pathToXML) throws FileNotFoundException {
		List<Project> lstProject = null;
		Document doc;
		doc = getDocumentFromPath(pathToXML);
		// Recherche tous les elements <projects>
		NodeList projectsNodeList = doc.getElementsByTagName("project");
		lstProject = new ArrayList<>();
		// Pour chaque élément XML de la liste
		for (int i = 0; i < projectsNodeList.getLength(); i++) {
			Node node = projectsNodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				// Récupère l'élément
				Element projectElement = (Element)node;
				// Crée un nouvel objet de type Project
				Project project = new Project();
				try {
					project.setOriginId(Integer.parseInt(projectElement.getElementsByTagName("originId").item(0).getTextContent()));
					project.setOrigin(projectElement.getElementsByTagName("origin").item(0).getTextContent());
					
					lstProject.add(project);
				} catch (NumberFormatException e) {
					LOGGER.warn("The file format is not valid");
				}
			}
		}

		//throw new XMLFormatException("An error occurred while formatting the projects in the file : " + pathToXML);
		return lstProject;
	}

	/**
	 * Read a XML file from pathToXML to search the Strings to be considered for processing by OCDM
	 *
	 * @param pathToXML The path of the XML file
	 * @param project The project for which to search the configuration
	 * @return List of String to be considered for processing by OCDM
	 * @throws FileNotFoundException If the file was not found
	 * @throws ConfigurationException If there are any configuration errors in DocumentBuilderFactory
	 * @throws XMLFormatException If the file does not have a valid structure
	 */
	public List<String> getListOfStringProcessedByOCDM(String pathToXML, Project project) throws FileNotFoundException {
		List<String> aTraiterOcdm = new ArrayList<>();
		Document document = getDocumentFromPath(pathToXML);

		String id = String.valueOf(project.getOriginId());
		String origin = project.getOrigin();

		if (isProjectExist(document, project)) {
			aTraiterOcdm = evaluateXPath(document, "/config/specific-config/project[@id='"+id+"' and @origin='"+origin+"']/A-traiter-OCDM/text()");
		} else {
			aTraiterOcdm = evaluateXPath(document, "/config/"+origin+"/A-traiter-OCDM/text()");
		}
		return aTraiterOcdm;
	}

	/**
	 * Read a XML file from pathToXML to search the Strings to be considered for processing by customer
	 *
	 * @param pathToXML The path of the XML file
	 * @param project The project for which to search the configuration
	 * @return List of String to be considered for processing by customer
	 * @throws XMLFormatException
	 * @throws ConfigurationException
	 * @throws FileNotFoundException
	 */
	public List<String> getListOfStringProcessedByCustomer(String pathToXML, Project project) throws FileNotFoundException {
		List<String> aTraiterOcdm = new ArrayList<>();
		Document document = getDocumentFromPath(pathToXML);

		String id = String.valueOf(project.getOriginId());
		String origin = project.getOrigin();

		if (isProjectExist(document, project)) {
			aTraiterOcdm = evaluateXPath(document, "/config/specific-config/project[@id='"+id+"' and @origin='"+origin+"']/A-traiter-client/text()");
		} else {
			aTraiterOcdm = evaluateXPath(document, "/config/"+origin+"/A-traiter-client/text()");
		}
		return aTraiterOcdm;
	}

	/**
	 * Retrieve the XML file from path and use DocumentBuilder to build a Document
	 *
	 * @param pathToXML The String path of XML file
	 * @return Document
	 * @throws FileNotFoundException If the file was not found
	 * @throws ConfigurationException If there are any configuration errors in DocumentBuilderFactory
	 * @throws XMLFormatException If the file does not have a valid structure
	 */
	private Document getDocumentFromPath(String pathToXML) throws FileNotFoundException {
		if (pathToXML == null) {
			LOGGER.warn("The pathToXML is null");
			throw new NullPointerException("The file path to XML is null");
		}
		if (pathToXML.isBlank() || !new File(pathToXML).exists()) {
			throw new FileNotFoundException("The file path to XML is not valid");
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// SECURITY
		// Disabling entities references expansion
		factory.setExpandEntityReferences(false);
		try {
			// Enabling secure processing
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			// Disabling DOCTYPE
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			// Disabling external entities declarations
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		} catch (ParserConfigurationException e) {
			LOGGER.warn("An error was occured when set features to javax.xml.parsers.DocumentBuilderFactory");
		}
		Document document = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(pathToXML);
		} catch (SAXException e) {
			LOGGER.warn("An error was occured when parse : " + pathToXML + " to an org.w3c.dom.Document. Caused by : \n" + e.getMessage());
		} catch (IOException e) {
			LOGGER.warn("An error was occured when read : " + pathToXML + ". Caused by : \n" + e.getMessage());
		} catch (ParserConfigurationException e) {
			LOGGER.warn("An error was occured when Creates a new instance of a javax.xml.parsers.DocumentBuilder using the currently configured parameters.");
		}
		return document;
	}

	/**
	 * Return the matching data with xPathExpression from the document
	 * @param document The document to search
	 * @param xpathExpression The expression to be researched
	 * @return List of String
	 */
	private List<String> evaluateXPath(Document document, String xPathExpression) {

		List<String> values = new ArrayList<>();
		String name;
		try {
			name = (String) xPath.evaluate(xPathExpression, document, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			LOGGER.error("Expression : " + xPathExpression +" cannot be evaluated");
			throw new IllegalArgumentException("Expression : " + xPathExpression +" cannot be evaluated");
		}
		values = Arrays.asList(name.split(","));
		Stream<String> stream = values.stream();
		stream = stream.map(String::trim);
		stream = stream.filter(Predicate.not(String::isBlank));
		values = stream.collect(Collectors.toList());
		return values;
	}

	/**
	 * Check if project have a specific configuration in the document
	 * @param document The document to search
	 * @param project The project to be researched
	 * @return Boolean
	 */
	private Boolean isProjectExist(Document document, Project project) {
		String expression = "boolean(/config/specific-config/project[@id='"+project.getOriginId()+"' and @origin='"+project.getOrigin()+"'])";
		Boolean exist = false;
		try {
			exist = (Boolean) xPath.evaluate(expression, document, XPathConstants.BOOLEAN);
		} catch (XPathExpressionException e) {
			LOGGER.error("Expression : " + expression +" cannot be evaluated");
		}
		return exist;
	}

}
