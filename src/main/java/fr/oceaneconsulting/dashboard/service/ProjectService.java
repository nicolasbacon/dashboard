package fr.oceaneconsulting.dashboard.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.oceaneconsulting.dashboard.DashboardApplication;
import fr.oceaneconsulting.dashboard.chart.ChartRender;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.properties.AbstractReader;
import fr.oceaneconsulting.dashboard.repository.ProjectDAO;

@Service
public class ProjectService {
	
	
	private static final Logger LOGGER = LogManager.getLogger();

	private final String PATH_FILE_LIST_PROJECTS = DashboardApplication.PATH_FILE_LIST_PROJECTS;
	private final String PATH_FILE_FILTER_TREATMENTS = DashboardApplication.PATH_FILE_FILTER_TREATMENTS;

	@Autowired
	ProjectDAO projectDAO;

	/**
	 * Read all project was enumarated in file PATH_FILE_LIST_PROJECTS
	 * for get their origin and originId then retrieve all projects from database
	 *
	 * @return List of all Projects
	 */
	public List<Project> getAllProjects() {
		List<Project> lstProjectToFind = null;
		try {
			lstProjectToFind = AbstractReader.readProjects(PATH_FILE_LIST_PROJECTS);
		} catch (FileNotFoundException e) {
			LOGGER.warn("The pathToXML is blank or doesn't exist");
		}
		for (int i = 0; i < lstProjectToFind.size(); i++) {
			Project currentProject = lstProjectToFind.get(i);
			Project projectInBdd = projectDAO.findByOriginIdAndOrigin(currentProject.getOriginId(), currentProject.getOrigin());
			if (projectInBdd != null) lstProjectToFind.set(i, projectInBdd);
			else lstProjectToFind.remove(i);
		}
		return lstProjectToFind;
	}

	/**
	 * Retrieve a project in database from this originId and origin
	 *
	 * @param originId The original project id
	 * @param origin The name of the original api
	 * @return A Project
	 */
	public Project getProject(int originId, String origin) {
		Project project = projectDAO.findByOriginIdAndOrigin(originId, origin);
		return project;
	}

	/**
	 * Retrive the project from its origin and originId from database and
	 * ask the {@link ChartRender} to generate the anomalies chart by criticality.
	 * @param originId The original project id
	 * @param origin The name of the original api
	 * @return File instance from the file name returned by ChartRender
	 */
	public File getAnomaliesChartByCriticality(int originId, String origin) {
		Project project = getProject(originId, origin);
		String pathToDiagram = ChartRender.generateChartAnomaliesByCriticality(project);
		File diagramme = new File(pathToDiagram);
		return diagramme;
	}

	/**
	 * Retrive the project from its origin and originId from database ,
	 * get its configuration of state from PATH_FILE_FILTER_TREATMENTS and
	 * ask the {@link ChartRender} to generate the anomalies chart by state.
	 * @param originId The original project id
	 * @param origin The name of the original api
	 * @return File instance from the file name returned by ChartRender
	 */
	public File getAnomaliesChartByState(int originId, String origin) {
		Project project = getProject(originId, origin);
		List<String> aTraiterOcdm = new ArrayList<>();
		List<String> aTraiterClient = new ArrayList<>();
		try {
			aTraiterOcdm = AbstractReader.getListOfStringProcessedByOCDM(PATH_FILE_FILTER_TREATMENTS, project);
			aTraiterClient = AbstractReader.getListOfStringProcessedByCustomer(PATH_FILE_FILTER_TREATMENTS, project);
		} catch ( FileNotFoundException e) {
			LOGGER.warn("The pathToXML is blank or doesn't exist");
		}
		String pathToDiagram = ChartRender.generateChartAnomaliesByStateFromConfig(project, aTraiterOcdm, aTraiterClient);
		File diagramme = new File(pathToDiagram);
		return diagramme;
	}

	/**
	 * Retrive the project from its origin and originId from database ,
	 * get its configuration of state from PATH_FILE_FILTER_TREATMENTS and
	 * ask the {@link ChartRender} to generate the status evolution chart by week.
	 * @param originId The original project id
	 * @param origin The name of the original api
	 * @param startPeriod The date on which the graph will start
	 * @param endPeriod The date on which the graph will end
	 * @return File instance from the file name returned by ChartRender
	 */
	public File getStatusEvolutionChartByWeek(int originId, String origin, ZonedDateTime startPeriod, ZonedDateTime endPeriod) {
		Project project = getProject(originId, origin);
		List<String> aTraiterOcdm = new ArrayList<>();
		List<String> aTraiterClient = new ArrayList<>();
		try {
			aTraiterOcdm = AbstractReader.getListOfStringProcessedByOCDM(PATH_FILE_FILTER_TREATMENTS, project);
			aTraiterClient = AbstractReader.getListOfStringProcessedByCustomer(PATH_FILE_FILTER_TREATMENTS, project);
		} catch (FileNotFoundException e) {
			LOGGER.warn("The pathToXML is blank or doesn't exist");
		}
		String pathToDiagram = ChartRender.generateStatusEvolutionChartByWeekFromConfig(project, startPeriod, endPeriod, aTraiterOcdm, aTraiterClient);
		File diagramme = new File(pathToDiagram);
		return diagramme;
	}

}
