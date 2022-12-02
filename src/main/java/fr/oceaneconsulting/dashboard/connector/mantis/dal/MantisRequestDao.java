package fr.oceaneconsulting.dashboard.connector.mantis.dal;

import java.util.List;

import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisRequest;
import fr.oceaneconsulting.dashboard.exception.FormatException;

public interface MantisRequestDao {

	/**
	 * Get all MantisRequest by a project id
	 *
	 * @param projectId
	 * @return List of MantisRequest
	 * @throws FormatException
	 */
	List<MantisRequest> getRequestOfProject(int projectId) throws FormatException;

	/**
	 * Retrieves all MantisRequests of a project from a timestamp in seconds
	 *
	 * @param projectId
	 * @param from A long corresponding to the number of second since 1970-01-01T00:00:00Z
	 * @return List of MantisRequest
	 * @throws FormatException
	 */
	List<MantisRequest> getRequestOfProjectFrom(int projectId, long from) throws FormatException;

	/**
	 * Gets a specific request by its id
	 *
	 * @param idRequest
	 * @return MantisRequest
	 * @throws FormatException
	 */
	MantisRequest getRequest(int idRequest) throws FormatException;
}
