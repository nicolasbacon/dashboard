package fr.oceaneconsulting.dashboard.connector.mantis.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisTreatment;
import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisTreatmentStatus;
import fr.oceaneconsulting.dashboard.exception.SQLFormatException;

public class MantisTreatmentDaoImpl implements MantisTreatmentDao {
	public static final Logger LOGGER = LogManager.getLogger();
	private MantisDAOFactory daoFactory;

	MantisTreatmentDaoImpl(MantisDAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/**
	 * Request the Mantis database configured in {@link MantisDAOFactory#getInstance()}
	 * to find all MantisTreatment of MantisRequest
	 *
	 * @param requestId
	 * @return List of MantisTreatment
	 * @throws SQLFormatException 
	 */
	@Override
	public List<MantisTreatment> getTreatmentOfRequest(int requestId) throws SQLFormatException {
		List<MantisTreatment> lstMantisTreatments = new ArrayList<>();
		PreparedStatement statement = null;
		ResultSet resultat = null;

		try(Connection connexion = daoFactory.getConnection()) {
			statement = connexion.prepareStatement("SELECT * FROM `mantis_bug_history_table` WHERE `bug_id` = ? AND (type = 1 OR ( type = 0 AND field_name LIKE 'status') );");
			statement.setInt(1, requestId);
			resultat = statement.executeQuery();

			while (resultat.next()) {
				MantisTreatment mantisRequest = new MantisTreatment(
						resultat.getInt("id"),
						( resultat.getInt("type") == 1 ) ?
								MantisTreatmentStatus.NOUVEAU : MantisTreatmentStatus.valueOfId(resultat.getInt("new_value")),
						ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC+1")).plusSeconds(resultat.getLong("date_modified"))
						);

				lstMantisTreatments.add(mantisRequest);
			}
		} catch (SQLException e) {
			LOGGER.warn("A database access error was occured");
			throw new SQLFormatException("Error during MantisRequest or MantisTreatment mapping");
		}
		return lstMantisTreatments;
	}

	/**
	 * Request the Mantis database configured in {@link MantisDAOFactory#getInstance()}
	 * to find the lastest MantisTreatment of MantisProject
	 *
	 * @param projectId
	 * @return List of MantisTreatment
	 * @throws SQLFormatException 
	 */
	@Override
	public List<MantisTreatment> getLatestTreatmentsOfProject(int projectId) throws SQLFormatException {
		List<MantisTreatment> lstMantisTreatments = new ArrayList<>();
		PreparedStatement statement = null;
		ResultSet resultat = null;

		try(Connection connexion = daoFactory.getConnection()) {
			statement = connexion.prepareStatement(
					"SELECT * FROM `mantis_bug_history_table` WHERE (bug_id,date_modified) IN"
							+ "	(SELECT "
							+ "		B.id as bug_id,"
							+ "	    	MAX(date_modified) as date_modified"
							+ "	FROM `mantis_bug_history_table` AS A JOIN `mantis_bug_table` AS B ON A.bug_id = B.id "
							+ "	WHERE B.project_id = ? "
							+ "	GROUP BY "
							+ "		bug_id"
							+ "	) "
							+ "AND (type = 1 OR ( type = 0 AND field_name LIKE 'status') );"
					);
			statement.setInt(1, projectId);
			resultat = statement.executeQuery();

			while (resultat.next()) {
				MantisTreatment mantisTreatment = new MantisTreatment(
						resultat.getInt("id"),
						null,
						ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC+1")).plusSeconds(resultat.getLong("date_modified"))
						);
				mantisTreatment.setStatus( ( resultat.getInt("type") == 1 ) ?
						MantisTreatmentStatus.NOUVEAU : MantisTreatmentStatus.valueOfId(resultat.getInt("new_value"))
						);

				lstMantisTreatments.add(mantisTreatment);
			}
		} catch (SQLException e) {
			LOGGER.warn("A database access error was occured");
			throw new SQLFormatException("Error during MantisRequest or MantisTreatment mapping");
		}
		return lstMantisTreatments;
	}

	/**
	 * Request the Mantis database configured in {@link MantisDAOFactory#getInstance()}
	 * to find the last MantisTreatment of MantisRequest
	 *
	 * @param requestId
	 * @return MantisTreatment
	 * @throws SQLFormatException 
	 */
	@Override
	public MantisTreatment getLastTreatmentOfRequest(int requestId) throws SQLFormatException {
		MantisTreatment mantisTreatment = null;
		PreparedStatement statement = null;
		ResultSet resultat = null;
		try(Connection connexion = daoFactory.getConnection()) {
			statement = connexion.prepareStatement(
					"SELECT * FROM `mantis_bug_history_table` WHERE (bug_id,date_modified) IN ("
							+ "	SELECT 	B.id as bug_id,"
							+ "			MAX(date_modified) as date_modified"
							+ "	FROM `mantis_bug_history_table` AS A JOIN `mantis_bug_table` AS B ON A.bug_id = B.id "
							+ "	WHERE B.id = ? "
							+ "    AND (type = 1 OR ( type = 0 AND field_name LIKE 'status') )"
							+ "	GROUP BY bug_id"
							+ ");"
					);
			statement.setInt(1, requestId);
			resultat = statement.executeQuery();

			while (resultat.next()) {
				mantisTreatment = new MantisTreatment(
						resultat.getInt("id"),
						( resultat.getInt("type") == 1 ) ?
								MantisTreatmentStatus.NOUVEAU : MantisTreatmentStatus.valueOfId(resultat.getInt("new_value")),
								ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC+1")).plusSeconds(resultat.getLong("date_modified"))
						);

			}
		} catch (SQLException e) {
			LOGGER.warn("A database access error was occured");
			throw new SQLFormatException("Error during MantisRequest or MantisTreatment mapping");
		}
		return mantisTreatment;

	}

}
