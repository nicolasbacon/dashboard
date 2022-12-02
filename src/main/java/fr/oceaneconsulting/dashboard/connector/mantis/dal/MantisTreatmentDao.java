package fr.oceaneconsulting.dashboard.connector.mantis.dal;

import java.util.List;

import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisTreatment;
import fr.oceaneconsulting.dashboard.exception.SQLFormatException;

public interface MantisTreatmentDao {

	/**
	 * Get all MantisTreatment by a MantisRequest id
	 *
	 * @param requestId
	 * @return List of MantisTreatment
	 * @throws SQLFormatException 
	 */
	List<MantisTreatment> getTreatmentOfRequest(int requestId) throws SQLFormatException;

	/**
	 * Gets the latest MantisTreatment for a MantisProject
	 *
	 * @param projectId
	 * @return List of MantisTreatment
	 * @throws SQLFormatException 
	 */
	List<MantisTreatment> getLatestTreatmentsOfProject(int projectId) throws SQLFormatException;

	/**
	 * Gets the last treatment of a request
	 *
	 * @param requestId
	 * @return MantisTreatment
	 * @throws SQLFormatException 
	 */
	MantisTreatment getLastTreatmentOfRequest(int requestId) throws SQLFormatException;
}
