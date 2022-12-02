package fr.oceaneconsulting.dashboard.connector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ConnectorFatoryTest {

	@Test
	void getConnectorInstance_should_throw_NullPointerException_when_parameter_is_null() {
		// GIVEN
		String connectorNameNull = null;
		// WHEN
		assertThrows(NullPointerException.class, () -> {
			ConnectorFactory.getConnectorInstance(connectorNameNull);
		});
	}

	@Test
	void getConnectorInstance_should_throw_IllegalArgumentException_when_parameter_is_not_valid_connector_name() {
		// GIVEN
		String connectorNameNull = "Unknown";
		// WHEN
		assertThrows(IllegalArgumentException.class, () -> {
			ConnectorFactory.getConnectorInstance(connectorNameNull);
		});
	}

	@ParameterizedTest(name="{0} doit retourner une instance de Connector")
	@ValueSource(strings = {ConnectorFactory.MANTIS_CONNECTOR, ConnectorFactory.REDMINE_CONNECTOR})
	void getConnectorInstance_should_return_instance_of_Connector(String arg) {
		// WHEN
		Object instance = ConnectorFactory.getConnectorInstance(arg);
		assertThat(instance).isInstanceOf(Connector.class);
	}

}
