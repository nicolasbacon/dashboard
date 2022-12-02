package fr.oceaneconsulting.dashboard.properties;

import java.io.FileNotFoundException;
import java.util.List;

import javax.naming.ConfigurationException;

import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.model.Project;

public interface ConfigurationReader {

	/**
	 * This method requires the ability to read a file from pathFile
	 * and return a list of Projects with only the original id and origin
	 *
	 * @param pathFile The path of the file
	 * @return The list of Projects to update (with only the original id and origin)
	 * @throws FormatException If the file does not have a valid structure
	 * @throws FileNotFoundException If the pathFile is not valid
	 * @throws ConfigurationException If there are any configuration errors
	 */
	public List<Project> readProjects(String pathFile) throws FormatException, FileNotFoundException, ConfigurationException;
}
