package fr.oceaneconsulting.dashboard.connector;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.oceaneconsulting.dashboard.connector.mantis.MantisBDDConnector;
import fr.oceaneconsulting.dashboard.connector.redmine.RedmineConnector;

/**
 * A Factory to get one or all instance
 * of API consumer like MantisBT, Redmine,
 * Jira.
 *
 */
public abstract class ConnectorFactory {
	
	public static final Logger LOGGER = LogManager.getLogger();

	public static final String MANTIS_CONNECTOR = "Mantis";
	public static final String REDMINE_CONNECTOR = "Redmine";

	/**
	 * Return an instance of Connector allowing to request
	 * an API from its name.
	 *
	 * @param connectorName Name of Connector corresponding to
	 * {@link ConnectorFactory#MANTIS_CONNECTOR} or {@link ConnectorFactory#REDMINE_CONNECTOR}
	 * @return An instance of {@link Connector}
	 */
	public static Connector getConnectorInstance(String connectorName) {
		if (connectorName == null) {
			LOGGER.warn("The connectorName is null");
			throw new NullPointerException("The name of connector must not be null or empty");
		}
		switch (connectorName) {
		case MANTIS_CONNECTOR:
			return MantisBDDConnector.getInstance();
		case REDMINE_CONNECTOR:
			return RedmineConnector.getInstance();
		default:
			LOGGER.warn("The connector : " + connectorName + "does not exist");
			throw new IllegalArgumentException("Unknown Name : " + connectorName);
		}
	}

	/**
	 * Return all instances of Connector allowing to request
	 * an API.
	 *
	 * @return List of all instances of {@link Connector}
	 */
	public static List<Connector> getAllConnectors() {
		List<Connector> lstConnectors = new ArrayList<>();
		lstConnectors.add(MantisBDDConnector.getInstance());
		lstConnectors.add(RedmineConnector.getInstance());
		return lstConnectors;
	}

}
