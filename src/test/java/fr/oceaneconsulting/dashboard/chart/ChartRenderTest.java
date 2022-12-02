package fr.oceaneconsulting.dashboard.chart;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.naming.ConfigurationException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.oceaneconsulting.dashboard.DashboardApplication;
import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.properties.AbstractReader;
import fr.oceaneconsulting.dashboard.repository.ProjectDAO;

@SpringBootTest
class ChartRenderTest {
	
	private final String PATH_FILE_FILTER_TREATMENTS = DashboardApplication.PATH_FILE_FILTER_TREATMENTS;

	@Autowired
	ProjectDAO projectDAO;

	@Test
	@Transactional
	void generateChartAnomaliesByCriticality_should_return_valid_name_of_file() {
		// GIVEN
		List<Project> listProject = projectDAO.findAll();

		// WHEN
		for (Project project : listProject) {
			String pathOfDiagram = ChartRender.generateChartAnomaliesByCriticality(project);
			File fileOfDiagram = new File(pathOfDiagram);
			assertThat(fileOfDiagram.exists()).isTrue();
			fileOfDiagram.delete();
		}
	}

	@Test
	@Transactional
	void generateChartAnomaliesByStateFromConfig_should_return_valid_name_of_file() throws FileNotFoundException, ConfigurationException, FormatException {
		// GIVEN
		List<Project> listProject = projectDAO.findAll();

		// WHEN
		for (Project project : listProject) {
			List<String> aTraiterOcdm = AbstractReader.getListOfStringProcessedByOCDM(PATH_FILE_FILTER_TREATMENTS, project);
			List<String> aTraiterClient= AbstractReader.getListOfStringProcessedByCustomer(PATH_FILE_FILTER_TREATMENTS, project);
			String pathOfDiagram = ChartRender.generateChartAnomaliesByStateFromConfig(project, aTraiterOcdm, aTraiterClient);
			File fileOfDiagram = new File(pathOfDiagram);
			assertThat(fileOfDiagram.exists()).isTrue();
			fileOfDiagram.delete();
		}
	}

	@Test
	@Transactional
	void generateStatusEvolutionChartByWeekFromConfig_should_return_valid_name_of_file() throws FileNotFoundException, ConfigurationException, FormatException {
		// GIVEN
		List<Project> listProject = projectDAO.findAll();

		for (Project project : listProject) {
			ZonedDateTime startDate = ZonedDateTime.of(2020, 9, 1, 0, 0, 0, 0, ZoneId.of("UTC+1"));
			ZonedDateTime endDate = ZonedDateTime.of(2020, 10, 1, 0, 0, 0, 0, ZoneId.of("UTC+1"));
			List<String> aTraiterOcdm = AbstractReader.getListOfStringProcessedByOCDM(PATH_FILE_FILTER_TREATMENTS, project);
			List<String> aTraiterClient = AbstractReader.getListOfStringProcessedByCustomer(PATH_FILE_FILTER_TREATMENTS, project);
			String pathOfDiagram = ChartRender.generateStatusEvolutionChartByWeekFromConfig(project, startDate, endDate, aTraiterOcdm, aTraiterClient);
			File fileOfDiagram = new File(pathOfDiagram);
			assertThat(fileOfDiagram.exists()).isTrue();
			fileOfDiagram.delete();
		}
	}
}
