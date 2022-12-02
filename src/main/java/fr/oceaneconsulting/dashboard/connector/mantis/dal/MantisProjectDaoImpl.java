package fr.oceaneconsulting.dashboard.connector.mantis.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisProject;
import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.SQLFormatException;

public class MantisProjectDaoImpl implements MantisProjectDao {
	public static final Logger LOGGER = LogManager.getLogger();
	private MantisDAOFactory daoFactory;

    MantisProjectDaoImpl(MantisDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

	@Override
	/**
	 * Request the Mantis database configured in {@link MantisDAOFactory#getInstance()}
	 * to find all activated projects
	 *
	 * @return List of all activated MantisProject
	 */
	public List<MantisProject> getEnabledProjects() throws FormatException {
		List<MantisProject> mantisProjects = new ArrayList<>();
        Statement statement = null;
        ResultSet resultat = null;

        try(Connection connexion = daoFactory.getConnection()) {
            statement = connexion.createStatement();
            resultat = statement.executeQuery("SELECT * FROM mantis_project_table WHERE enabled = 1;");

            while (resultat.next()) {
            	MantisProject mantisProject = new MantisProject(
            			resultat.getInt("id"),
            			resultat.getString("name"),
            			resultat.getBoolean("enabled"),
            			"Mantis"
            			);

                mantisProjects.add(mantisProject);
            }
        } catch (SQLException e) {
        	LOGGER.warn("A database access error was occured");
            throw new SQLFormatException("Error during MantisProject mapping");
        }
        return mantisProjects;
	}

	@Override
	/**
	 * Request the Mantis database configured in {@link MantisDAOFactory#getInstance()}
	 * to find a MantisProject by id
	 *
	 * @return MantisProject
	 * @throws FormatException
	 */
	public MantisProject getProject(int projectId) throws FormatException  {
		MantisProject mantisProject = null;
        PreparedStatement statement = null;
        ResultSet resultat = null;

        try(Connection connexion = daoFactory.getConnection()) {
            statement = connexion.prepareStatement("SELECT * FROM `mantis_project_table` AS A WHERE A.id = ?;");
            statement.setInt(1, projectId);
            resultat = statement.executeQuery();

            while (resultat.next()) {
            	mantisProject = new MantisProject(
            			resultat.getInt("id"),
            			resultat.getString("name"),
            			resultat.getBoolean("enabled"),
            			"Mantis"
            			);

            }
        } catch (SQLException e) {
        	LOGGER.warn("A database access error was occured");
        	throw new SQLFormatException("Error during MantisProject mapping");
        }
        return mantisProject;
	}

}
