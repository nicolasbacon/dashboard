package fr.oceaneconsulting.dashboard.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import javax.naming.ConfigurationException;

import org.junit.jupiter.api.Test;

import fr.oceaneconsulting.dashboard.DashboardApplication;
import fr.oceaneconsulting.dashboard.exception.FormatException;

public class AbstractReaderTest {
	
	private final String PATH_FILE_LIST_PROJECTS = DashboardApplication.PATH_FILE_LIST_PROJECTS;
	
	@Test
	void generateConfigFile_should_create_valid_XML_file() {
		AbstractReader.generateConfigFile();
	}

	@Test
	void readProjects_should_throw_NullPointerException_when_pathFile_is_null() {
		// GIVEN
		String pathFile = null;
		// WHEN
		assertThrows(NullPointerException.class, () -> {
			AbstractReader.readProjects(pathFile);
		});
	}

	@Test
	void readProjects_should_throw_IllegalArgumentException_when_pathFile_is_empty() {
		// GIVEN
		String pathFile = "";
		// WHEN
		assertThrows(IllegalArgumentException.class, () -> {
			AbstractReader.readProjects(pathFile);
		});
	}

	@Test
	void readProjects_should_throw_FileNotFoundException_when_file_was_not_found() {
		// GIVEN
		String pathFile = "./file.xml";
		// WHEN
		assertThrows(FileNotFoundException.class, () -> {
			AbstractReader.readProjects(pathFile);
		});
	}

	@Test
	void readProjects_should_throw_IllegalArgumentException_when_pathFile_is_not_valid() {
		// GIVEN
		List<String> listPathFileToTest = Arrays.asList(
				"./file",
				"./file.php",
				"./file.abc"
				);
		// WHEN
		for (String string : listPathFileToTest) {
			assertThrows(IllegalArgumentException.class, () -> {
				AbstractReader.readProjects(string);
			});
		}
	}

	@Test
	void readProjects_should_return_projects_when_pathFile_is_valid() throws FileNotFoundException, ConfigurationException, FormatException {
		// WHEN
		assertThat(AbstractReader.readProjects(PATH_FILE_LIST_PROJECTS)).isNotNull();
	}
}
