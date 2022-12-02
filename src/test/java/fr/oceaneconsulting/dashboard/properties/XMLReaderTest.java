package fr.oceaneconsulting.dashboard.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.naming.ConfigurationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;

import fr.oceaneconsulting.dashboard.DashboardApplication;
import fr.oceaneconsulting.dashboard.exception.XMLFormatException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.repository.ProjectDAO;

@SpringBootTest
public class XMLReaderTest {
	
	private final String PATH_FILE_FILTER_TREATMENTS = DashboardApplication.PATH_FILE_FILTER_TREATMENTS;

	@Autowired
	ProjectDAO projectDAO;

	@Test
	void readProjects_should_throw_NullPointerException_if_pathToXML_is_null() {
		// GIVEN
		String pathToXML = null;
		// WHEN
		assertThrows(NullPointerException.class, () -> {
			XMLReader.getInstance().readProjects(pathToXML);
		});
	}

	@Test
	void readProjects_should_throw_FileNotFoundException_if_pathToXML_is_not_valid() {
		// GIVEN
		String pathToXML = "";
		// WHEN
		assertThrows(FileNotFoundException.class, () -> {
			XMLReader.getInstance().readProjects(pathToXML);
		});
	}

	@Test
	void getDocumentFromPath_should_return_document_when_pathFile_is_valid() {
		// GIVEN
		String pathFile = PATH_FILE_FILTER_TREATMENTS;
		// WHEN
		try {
			Method methode = XMLReader.class.getDeclaredMethod("getDocumentFromPath", String.class);
			methode.setAccessible(true);
			Document result = (Document)methode.invoke(XMLReader.getInstance(), pathFile);
			assertNotNull(result);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void getListATraiterOcdm_should_throw_NullPointerException_if_pathToXML_is_null() {
		// GIVEN
		String pathToXML = null;
		Project project = new Project();
		// WHEN
		assertThrows(NullPointerException.class, () -> {
			XMLReader.getInstance().getListOfStringProcessedByOCDM(pathToXML, project);
		});
	}

	@Test
	void getListATraiterOcdm_should_throw_NullPointerException_if_project_is_null() {
		// GIVEN
		String pathToXML = PATH_FILE_FILTER_TREATMENTS;
		Project project = null;
		// WHEN
		assertThrows(NullPointerException.class, () -> {
			XMLReader.getInstance().getListOfStringProcessedByOCDM(pathToXML, project);
		});
	}

	@Test
	void getListATraiterOcdm_should_return_not_empty_List() throws FileNotFoundException, ConfigurationException, XMLFormatException {
		// GIVEN
		String pathToXML = PATH_FILE_FILTER_TREATMENTS;
		List<Project> listProject = projectDAO.findAll();
		// WHEN
		for (Project project : listProject) {
			assertThat(XMLReader.getInstance().getListOfStringProcessedByOCDM(pathToXML, project)).isNotEmpty();
		}
	}

	@Test
	void getListATraiterClient_should_throw_NullPointerException_if_pathToXML_is_null() {
		// GIVEN
		String pathToXML = null;
		List<Project> listProject = projectDAO.findAll();
		// WHEN
		for (Project project : listProject) {
			assertThrows(NullPointerException.class, () -> {
				XMLReader.getInstance().getListOfStringProcessedByCustomer(pathToXML, project);
			});
		}
	}

	@Test
	void getListATraiterClient_should_throw_NullPointerException_if_project_is_null() {
		// GIVEN
		String pathToXML = PATH_FILE_FILTER_TREATMENTS;
		Project project = null;
		// WHEN
		assertThrows(NullPointerException.class, () -> {
			XMLReader.getInstance().getListOfStringProcessedByCustomer(pathToXML, project);
		});
	}

	@Test
	void getListATraiterClient_should_return_not_empty_List() throws FileNotFoundException, ConfigurationException, XMLFormatException {
		// GIVEN
		String pathToXML = PATH_FILE_FILTER_TREATMENTS;
		List<Project> listProject = projectDAO.findAll();
		// WHEN
		for (Project project : listProject) {
			assertThat(XMLReader.getInstance().getListOfStringProcessedByCustomer(pathToXML, project)).isNotEmpty();
		}
	}
}
