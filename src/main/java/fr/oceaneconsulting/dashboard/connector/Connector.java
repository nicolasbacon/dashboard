package fr.oceaneconsulting.dashboard.connector;

import java.util.List;

import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.NotFoundException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.model.Treatment;

public interface Connector {

	/**
	 * Gets all projects from an instance that implements Connector
	 *
	 * @return ArrayList< Project >
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public List<Project> getAllProjects() throws FormatException, NotFoundException;
	/**
	 * Gets a project from an instance that implements Connector
	 * and all its Requests
	 *
	 * @return ArrayList< Project >
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public Project getProjectWithRequests(int projectId) throws FormatException, NotFoundException;

	/**
	 * Gets all Requests from an instance that implements Connector
	 *
	 * @param idProject
	 * @return
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public List<Request> getAllRequests(int projectId) throws FormatException, NotFoundException;

	/**
	 * Gets a Request from an instance that implements Connector
	 * and all its Treatments
	 *
	 * @param idRequest
	 * @return
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public Request getRequestWithTreatments(int requestId) throws FormatException, NotFoundException;

	/**
	 * Gets all the latest Treatments for a project
	 * from an instance that implements Connector
	 *
	 * @param idProject
	 * @return
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public List<Treatment> getLastestTreatments(int projectId) throws FormatException, NotFoundException;

	/**
	 * Get all Treatments for a Request from an instance
	 * that implements Connector
	 *
	 * @param idRequest
	 * @return
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public List<Treatment> getAllTreatments(int requestId) throws FormatException, NotFoundException;

	/**
	 * Get last Treatments for a Request from an instance
	 * that implements Connector
	 *
	 * @param idRequest
	 * @return
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public Treatment getLastTreatment(int requestId) throws FormatException, NotFoundException;

	/**
	 * Recover the project with only its new data
	 *
	 * @param project The project for which to collect the new data
	 * @return
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	public Project getProjectUpdate(Project project) throws FormatException, NotFoundException;

}
