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

import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisRequest;
import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisTreatment;
import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisRequestImpact;
import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisRequestType;
import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisTreatmentStatus;
import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.SQLFormatException;

public class MantisRequestDaoImpl implements MantisRequestDao {
	public static final Logger LOGGER = LogManager.getLogger();
	private MantisDAOFactory daoFactory;

	MantisRequestDaoImpl(MantisDAOFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	/**
	 * Request the Mantis database configured in {@link MantisDAOFactory#getInstance()}
	 * to find all MantisRequest by a project id
	 *
	 * @param projectId
	 * @return List of MantisRequest
	 * @throws FormatException
	 */
	@Override
	public List<MantisRequest> getRequestOfProject(int projectId) throws FormatException {
		List<MantisRequest> lstMantisRequests = new ArrayList<>();
		PreparedStatement statement = null;
		ResultSet resultat = null;

		try(Connection connexion = daoFactory.getConnection()) {
			statement = connexion.prepareStatement("SELECT b.value, a.severity, a.date_submitted, c.id, c.bug_id, c.type, c.new_value, c.date_modified "
					+ "FROM `mantis_bug_table` AS a "
					+ "LEFT JOIN `mantis_custom_field_string_table` AS b ON a.id = b.bug_id "
					+ "JOIN `mantis_bug_history_table` AS c ON a.id = c.bug_id "
					+ "WHERE a.project_id = ? "
					+ "AND b.field_id = 3 "
					+ "AND (c.type = 1 OR ( c.type = 0 AND c.field_name LIKE 'status') );");
			statement.setInt(1, projectId);
			resultat = statement.executeQuery();
			while (resultat.next()) {
				// On recupere les infos
				MantisRequest mantisRequest = new MantisRequest(
						resultat.getInt("bug_id"),
						MantisRequestType.valueOfIgnoreSpaceAndCase(resultat.getString("value")),
						MantisRequestImpact.valueOfId(resultat.getInt("severity")),
						new ArrayList<>(),
						ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC+1")).plusSeconds(resultat.getLong("date_submitted"))
						);
				MantisTreatment mantisTreatment = new MantisTreatment(
						resultat.getInt("id"),
						( resultat.getInt("type") == 1 ) ?
								MantisTreatmentStatus.NOUVEAU : MantisTreatmentStatus.valueOfId(resultat.getInt("new_value")),
								ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC+1")).plusSeconds(resultat.getLong("date_modified"))
						);
				// Si la liste est vide ou que le dernier element est different de celui en cour
				if (lstMantisRequests.isEmpty() ) {
					mantisRequest.getLstMantisTreatments().add(mantisTreatment);
					lstMantisRequests.add(mantisRequest);
				} else if(!lstMantisRequests.get(lstMantisRequests.size() - 1).getId().equals(mantisRequest.getId())) {
					mantisRequest.getLstMantisTreatments().add(mantisTreatment);
					lstMantisRequests.add(mantisRequest);
					// Sinon on recupere le dernier et on lui ajoute juste le traitement
				} else  {
					lstMantisRequests.get(lstMantisRequests.size() - 1).getLstMantisTreatments().add(mantisTreatment);
				}

			}
		} catch (SQLException e) {
			LOGGER.warn("A database access error was occured");
			throw new SQLFormatException("Error during MantisRequest or MantisTreatment mapping");
		}

		return lstMantisRequests;
	}

	/**
	 * Request the Mantis database configured in {@link MantisDAOFactory#getInstance()}
	 * to find all MantisRequest of a project from a timestamp in seconds
	 *
	 * @param projectId
	 * @param from A long corresponding to the number of second since 1970-01-01T00:00:00Z
	 * @return List of MantisRequest
	 * @throws FormatException
	 */
	@Override
	public List<MantisRequest> getRequestOfProjectFrom(int projectId, long from) throws FormatException {
		List<MantisRequest> lstMantisRequests = new ArrayList<>();
		PreparedStatement statement = null;
		ResultSet resultat = null;

		try(Connection connexion = daoFactory.getConnection()) {
			statement = connexion.prepareStatement("SELECT b.value, a.severity, a.date_submitted, c.id, c.bug_id, c.type, c.new_value, c.date_modified "
					+ "FROM `mantis_bug_table` AS a "
					+ "LEFT JOIN `mantis_custom_field_string_table` AS b ON a.id = b.bug_id "
					+ "JOIN `mantis_bug_history_table` AS c ON a.id = c.bug_id "
					+ "WHERE a.project_id = ? "
					+ "AND b.field_id = 3 "
					+ "AND (c.type = 1 OR ( c.type = 0 AND c.field_name LIKE 'status') )"
					+ "AND a.last_updated > ?;");
			statement.setInt(1, projectId);
			statement.setLong(2, from);
			resultat = statement.executeQuery();
			while (resultat.next()) {
				// On recupere les infos
				MantisRequest mantisRequest = new MantisRequest(
						resultat.getInt("bug_id"),
						MantisRequestType.valueOfIgnoreSpaceAndCase(resultat.getString("value")),
						MantisRequestImpact.valueOfId(resultat.getInt("severity")),
						new ArrayList<>(),
						ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC+1")).plusSeconds(resultat.getLong("date_submitted"))
						);
				MantisTreatment mantisTreatment = new MantisTreatment(
						resultat.getInt("id"),
						( resultat.getInt("type") == 1 ) ?
								MantisTreatmentStatus.NOUVEAU : MantisTreatmentStatus.valueOfId(resultat.getInt("new_value")),
								ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC+1")).plusSeconds(resultat.getLong("date_modified"))
						);
				// Si la liste est vide ou que le dernier element est different de celui en cour
				if (lstMantisRequests.isEmpty() ) {
					mantisRequest.getLstMantisTreatments().add(mantisTreatment);
					lstMantisRequests.add(mantisRequest);
				} else if(!lstMantisRequests.get(lstMantisRequests.size() - 1).getId().equals(mantisRequest.getId())) {
					mantisRequest.getLstMantisTreatments().add(mantisTreatment);
					lstMantisRequests.add(mantisRequest);
					// Sinon on recupere le dernier et on lui ajoute juste le traitement
				} else  {
					lstMantisRequests.get(lstMantisRequests.size() - 1).getLstMantisTreatments().add(mantisTreatment);
				}

			}
		} catch (SQLException e) {
			LOGGER.warn("A database access error was occured");
			throw new SQLFormatException("Error during MantisRequest or MantisTreatment mapping");
		}
		return lstMantisRequests;
	}

	/**
	 * Request the Mantis database configured in {@link MantisDAOFactory#getInstance()}
	 * to find a specific MantisRequest by its id
	 *
	 * @param requestId
	 * @return MantisRequest
	 * @throws FormatException
	 */
	@Override
	public MantisRequest getRequest(int requestId) throws FormatException {
		MantisRequest mantisRequest = null;
		PreparedStatement statement = null;
		ResultSet resultat = null;

		try(Connection connexion = daoFactory.getConnection()) {
			statement = connexion.prepareStatement("SELECT * FROM `mantis_bug_table` AS A INNER JOIN `mantis_custom_field_string_table` AS B ON A.id = B.bug_id WHERE A.id = ?;");
			statement.setInt(1, requestId);
			resultat = statement.executeQuery();

			while (resultat.next()) {
				mantisRequest = new MantisRequest(
						resultat.getInt("id"),
						MantisRequestType.valueOfIgnoreSpaceAndCase(resultat.getString("value")),
						MantisRequestImpact.valueOfId(resultat.getInt("severity")),
						new ArrayList<>(),
						ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC+1")).plusSeconds(resultat.getLong("date_submitted"))
						);
			}
		} catch (SQLException e) {
			LOGGER.warn("A database access error was occured");
			throw new SQLFormatException("Error during MantisRequest mapping");
		}
		return mantisRequest;
	}

}
