package fr.oceaneconsulting.dashboard.properties;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

import javax.naming.ConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.oceaneconsulting.dashboard.DashboardApplication;
import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.XMLFormatException;
import fr.oceaneconsulting.dashboard.model.Project;

public abstract class AbstractReader {
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	private final static String PATH_FILE_LIST_PROJECTS = DashboardApplication.PATH_FILE_LIST_PROJECTS;
	private final static String PATH_FILE_FILTER_TREATMENTS = DashboardApplication.PATH_FILE_FILTER_TREATMENTS;

	/**
	 * Gets the correct ConfigurationReader based on the file extension
	 * to get the list of projects to update
	 *
	 * @param pathFile The path of the file where to look for the list of projects to retrieve
	 * @return The list of Projects to update (with only the original id and origin)
	 * @throws FileNotFoundException If the file with path file was not found
	 * @throws ConfigurationException If there are any configuration errors
	 * @throws FormatException If the structure of the file is not valid
	 */
	public static List<Project> readProjects(String pathFile) throws FileNotFoundException {
		if (pathFile == null) {
			throw new NullPointerException("The file path is null");
		}
		if (pathFile.isEmpty()) {
			throw new IllegalArgumentException("The file path must be not empty");
		}
		Optional<String> extension = Optional.ofNullable(pathFile)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(pathFile.lastIndexOf(".") + 1));
		switch (extension.get()) {
		case "xml":
			return XMLReader.getInstance().readProjects(pathFile);
		default:
			LOGGER.warn("The file extension : " + pathFile + ", is not available");
			throw new IllegalArgumentException("The file extension : " + pathFile + ", is not available");
		}
	}

	/**
	 * Gets the correct ConfigurationReader based on the file extension
	 * to get the list of Strings to be considered for processing by OCDM
	 *
	 * @param pathFile The path of the file where to look for the configuration
	 * @param project The project for which to search the configuration
	 * @return List of String to be considered for processing by OCDM
	 * @throws FileNotFoundException If the file was not found
	 * @throws ConfigurationException If there are any configuration errors in DocumentBuilderFactory
	 * @throws XMLFormatException If the file does not have a valid structure
	 */
	public static List<String> getListOfStringProcessedByOCDM(String pathFile, Project project) throws FileNotFoundException {
		Optional<String> extension = Optional.ofNullable(pathFile)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(pathFile.lastIndexOf(".") + 1));
		switch (extension.get()) {
		case "xml":
			return XMLReader.getInstance().getListOfStringProcessedByOCDM(pathFile, project);
		default:
			LOGGER.warn("The file extension : " + pathFile + ", is not available");
			throw new IllegalArgumentException("The file extension : " + pathFile + ", is not available");
		}
	}

	/**
	 * Gets the correct ConfigurationReader based on the file extension
	 * to get the list of Strings to be considered for processing by customer
	 *
	 * @param pathFile The path of the file where to look for the configuration
	 * @param project The project for which to search the configuration
	 * @return List of String to be considered for processing by OCDM
	 * @throws FileNotFoundException If the file was not found
	 * @throws ConfigurationException If there are any configuration errors in DocumentBuilderFactory
	 * @throws XMLFormatException If the file does not have a valid structure
	 */
	public static List<String> getListOfStringProcessedByCustomer(String pathFile, Project project) throws FileNotFoundException {
		Optional<String> extension = Optional.ofNullable(pathFile)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(pathFile.lastIndexOf(".") + 1));
		switch (extension.get()) {
		case "xml":
			return XMLReader.getInstance().getListOfStringProcessedByCustomer(pathFile, project);
		default:
			LOGGER.warn("The file extension : " + pathFile + ", is not available");
			throw new IllegalArgumentException("The file extension : " + pathFile + ", is not available");
		}
	}

	public static void generateConfigFile() {
		if (!new File(PATH_FILE_FILTER_TREATMENTS).getParentFile().exists())
			new File(PATH_FILE_FILTER_TREATMENTS).getParentFile().mkdir();
		if (!new File(PATH_FILE_LIST_PROJECTS).exists())
			generateListProjectsConfigFile();
		if (!new File(PATH_FILE_FILTER_TREATMENTS).exists())
			generateConfigRequestConfigFile();
	}
	
	private static void generateListProjectsConfigFile() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element projects = doc.createElement("projects");
			doc.appendChild(projects);
			Comment commentaire = doc.createComment("<project>\n"
					+ "            <originId>88</originId>\n"
					+ "            <origin>Mantis</origin>\n"
					+ "        </project>");
			projects.appendChild(commentaire);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult resultat = new StreamResult(new File(PATH_FILE_LIST_PROJECTS));
			
			transformer.transform(source, resultat);
		} catch (ParserConfigurationException | TransformerException e) {
			LOGGER.warn("Impossible to generate the file: " + PATH_FILE_LIST_PROJECTS);
		}
	}
	
	private static void generateConfigRequestConfigFile() {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element config = doc.createElement("config");
			doc.appendChild(config);
			Element mantis = doc.createElement("Mantis");
			Element redmine = doc.createElement("Redmine");
			Element specificConfig = doc.createElement("specific-config");
			config.appendChild(mantis);
			config.appendChild(redmine);
			config.appendChild(specificConfig);
			Element aTraiterOcdm = doc.createElement("A-traiter-OCDM");
			aTraiterOcdm.appendChild(doc.createTextNode("\n"
					+ "                NOUVEAU,\n"
					+ "                REOUVERT,\n"
					+ "                RETOUR_CLIENT_EFFECTUE,\n"
					+ "                ANALYSE_EN_COURS,\n"
					+ "                A_TRAITER,\n"
					+ "                RESOLUTION_EN_COURS,\n"
					+ "                A_RECETTER_USINE,\n"
					+ "                VALIDE_USINE\n        "));
			mantis.appendChild(aTraiterOcdm);
			Element aTraiterClient = doc.createElement("A-traiter-client");
			aTraiterClient.appendChild(doc.createTextNode("\n"
					+ "                EN_ATTENTE_RETOUR_CLIENT,\n"
					+ "                TRAITE,\n"
					+ "                A_DISPOSITION\n        "));
			mantis.appendChild(aTraiterClient);
			aTraiterOcdm = doc.createElement("A-traiter-OCDM");
			aTraiterOcdm.appendChild(doc.createTextNode("\n"
					+ "                NOUVEAU,\n"
					+ "                EN_COURS,\n"
					+ "                COMMENTAIRE,\n"
					+ "                FERME,\n"
					+ "                REJETE,\n"
					+ "                COMPLETE,\n"
					+ "                INITIE,\n"
					+ "                VALIDE,\n"
					+ "                DEMANDE_DABANDON,\n"
					+ "                A_CHIFFRER,\n"
					+ "                DEVIS_A_EMETTRE,\n"
					+ "                A_REALISER,\n"
					+ "                A_CORRIGER,\n"
					+ "                PERSISTANT\n        "));
			redmine.appendChild(aTraiterOcdm);
			
			aTraiterClient = doc.createElement("A-traiter-client");
			aTraiterClient.appendChild(doc.createTextNode("\n"
					+ "                RESOLU,\n"
					+ "                LIVRE,\n"
					+ "                RECETTE_EFFECTUEE,\n"
					+ "                EN_ATTENTE_DE_COMMANDE,\n"
					+ "                A_DEFINIR,\n"
					+ "                A_HOMOLOGUER\n        "));
			redmine.appendChild(aTraiterClient);
			
			
			Element project = doc.createElement("project");
			specificConfig.appendChild(project);
			project.setAttribute("id", "Id d'origine du projet");
			project.setAttribute("origin", "Le bug tracker d'origine du projet");
			aTraiterOcdm = doc.createElement("A-traiter-OCDM");
			aTraiterOcdm.appendChild(doc.createTextNode("\n            "));
			aTraiterClient = doc.createElement("A-traiter-client");
			aTraiterClient.appendChild(doc.createTextNode("\n            "));
			project.appendChild(aTraiterOcdm);
			project.appendChild(aTraiterClient);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult resultat = new StreamResult(new File(PATH_FILE_FILTER_TREATMENTS));
			
			transformer.transform(source, resultat);
		} catch (ParserConfigurationException | TransformerException e) {
			LOGGER.warn("Impossible to generate the file: " + PATH_FILE_FILTER_TREATMENTS);
		}
	}

}
