package fr.oceaneconsulting.dashboard.connector.mantis.dal;

import java.util.List;

import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisProject;
import fr.oceaneconsulting.dashboard.exception.FormatException;

public interface MantisProjectDao {
	/**
	 * Get all projects activated
	 * @return List of Projects
	 * @throws FormatException
	 */
    List<MantisProject> getEnabledProjects() throws FormatException;

    /**
     * Gets a project by its id
     * @param projectId Project id to be recovered
     * @return The project
     * @throws FormatException
     */
    MantisProject getProject(int projectId) throws FormatException;

}
