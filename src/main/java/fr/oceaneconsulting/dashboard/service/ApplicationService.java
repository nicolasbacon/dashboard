package fr.oceaneconsulting.dashboard.service;

import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.oceaneconsulting.dashboard.DashboardApplication;
import fr.oceaneconsulting.dashboard.connector.Connector;
import fr.oceaneconsulting.dashboard.connector.ConnectorFactory;
import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.NotFoundException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.properties.AbstractReader;
import fr.oceaneconsulting.dashboard.repository.ProjectDAO;
import fr.oceaneconsulting.dashboard.repository.RequestDAO;

@Service
public class ApplicationService {


	private static final Logger LOGGER = LogManager.getLogger();
	private final String PATH_FILE_LIST_PROJECTS = DashboardApplication.PATH_FILE_LIST_PROJECTS;

	@Autowired
	ProjectDAO projectDAO;

	@Autowired
	RequestDAO requestDAO;

	/**
	 * Updates the configured projects from the file whose path is stored in {@link DashboardApplication#FILE_WITH_PROJECT_LIST }
	 *
	 * @return The saved List of projects
	 */
	public List<Project> updateDatabase() {
		List<Project> lstProject = null;
		try {
			lstProject = AbstractReader.readProjects(PATH_FILE_LIST_PROJECTS);
		} catch (FileNotFoundException e) {
			LOGGER.warn("The pathToXML is blank or doesn't exist");
		}
		lstProject = updateDatabaseProjectFromList(lstProject);
		return lstProject;
	}

	/**
	 * Browse the list and retrieve the new data of a project
	 * if it already exists otherwise retrieve all the data of a project
	 * if it doesn't exist yet in database
	 *
	 * @param lstProjectToUpdate The list of projects to be updated
	 * @return The list of Project after save or update in database
	 */
	public List<Project> updateDatabaseProjectFromList(List<Project> lstProjectToUpdate) {
		for (int i = 0; i < lstProjectToUpdate.size(); i++) {
			Project projectFromList = lstProjectToUpdate.get(i);
			Project projectFromBdd = projectDAO.findByOriginIdAndOrigin(projectFromList.getOriginId(), projectFromList.getOrigin());
			Connector connector = ConnectorFactory.getConnectorInstance(projectFromList.getOrigin());
			if (projectFromBdd == null) {
				try {
					projectFromList = connector.getProjectWithRequests(projectFromList.getOriginId());
					projectFromList.setLastUpdate(ZonedDateTime.now());
					lstProjectToUpdate.set(i, projectFromList);
				} catch (FormatException e) {
					LOGGER.error("Error while formatting data");
					lstProjectToUpdate.remove(i);
				} catch (NotFoundException e) {
					LOGGER.error("Unable to retrieve data");
					lstProjectToUpdate.remove(i);
				}
			} else
				try {
					projectFromList = updateProjectFromLastUpdate(projectFromBdd);
					lstProjectToUpdate.set(i, projectFromList);
				} catch (FormatException e) {
					LOGGER.error("Error while formatting data");
				} catch (NotFoundException e) {
					LOGGER.error("Unable to retrieve data");
					lstProjectToUpdate.remove(i);
				}
		}
		List<Project> lstProjectsSaved = new ArrayList<>();
		projectDAO.saveAll(lstProjectToUpdate).forEach(lstProjectsSaved::add);
		return lstProjectsSaved;
	}

	/**
	 * Retrieve the new data of project from the
	 * right connector and assign them to the project
	 * passed as a parameter, save it and return it.
	 *
	 * @return The saved Project
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public Project updateProjectFromLastUpdate(Project projectFromBdd) throws FormatException, NotFoundException {
		Project projectWithOnlyNewData = ConnectorFactory.getConnectorInstance(projectFromBdd.getOrigin()).getProjectUpdate(projectFromBdd);
		projectFromBdd.setEnable(projectWithOnlyNewData.isEnable());
		projectFromBdd.setName(projectWithOnlyNewData.getName());
		projectFromBdd.setLastUpdate(ZonedDateTime.now());
		// On compare les nouvelles request avec celles deja connues
		for (Request requestFromConnector : projectWithOnlyNewData.getLstRequests()) {
			Request requestFromBdd = requestDAO.findByRequestIdAndProject(requestFromConnector.getRequestId(), projectFromBdd.getProjectId());
			if (requestFromBdd != null) {
				requestFromBdd.setLstTreatements(requestFromConnector.getLstTreatements());
				requestDAO.save(requestFromBdd);
			} else requestDAO.save(requestFromConnector);
		}
		return projectDAO.save(projectFromBdd);
	}

}
