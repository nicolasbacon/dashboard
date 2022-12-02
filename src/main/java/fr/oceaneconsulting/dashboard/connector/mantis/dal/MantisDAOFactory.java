package fr.oceaneconsulting.dashboard.connector.mantis.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MantisDAOFactory {
	
	public static final Logger LOGGER = LogManager.getLogger();

	private String url;
	private String username;
	private String password;

	private static MantisDAOFactory instance;

	/**
	 * The private constructor of MantisDAOFactory to forcing use
	 * getInstance methode to get a singleton
	 *
	 * @param url
	 * @param username
	 * @param password
	 */
	MantisDAOFactory(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	/**
	 * Loads the mysql driver and gets an instance of MantisDAOFactory with
	 * the attributes url, username and password initialized
	 *
	 * @return The instance of MantisDAOFactory
	 */
	public static MantisDAOFactory getInstance() {
		if (instance == null) {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				LOGGER.error("Unable to load the driver : com.mysql.cj.jdbc.Driver");
			}
			instance = new MantisDAOFactory("jdbc:mysql://10.20.0.206:3306/bugtracker", "test", "test");
		}
		return instance;
	}

	/**
	 * Get a Connection to the database with parameters specified into {@link MantisDAOFactory#getInstance()}
	 *
	 * @return A Connection to the database
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	/**
	 * Get a DAO to MantisProject
	 *
	 * @return An instance of MantisProjectDaoImpl
	 */
	public MantisProjectDao getMantisProjectDao() {
		return new MantisProjectDaoImpl(this);
	}

	/**
	 * Get a DAO to MantisRequest
	 *
	 * @return An instance of MantisRequestDaoImpl
	 */
	public MantisRequestDao getMantisRequestDao() {
		return new MantisRequestDaoImpl(this);
	}

	/**
	 * Get a DAO to MantisTreatment
	 *
	 * @return An instance of MantisTreatmentDaoImpl
	 */
	public MantisTreatmentDao getMantisTreatmentDao() {
		return new MantisTreatmentDaoImpl(this);
	}
}
