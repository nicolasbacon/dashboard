package fr.oceaneconsulting.dashboard.connector.mantis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.model.Treatment;

class MantisBDDConnectorTest {

	private MantisBDDConnector connector;
	private int projectId = 88;
	private int requestId = 8881;

	@BeforeEach
	void initConnector() {
		connector = MantisBDDConnector.getInstance();
	}

	@AfterEach
	void undefConnector() {
		connector = null;
	}

	@Test
	void getAllProjects_should_return_not_null_list() throws FormatException {
		List<Project> list = connector.getAllProjects();
		assertThat(list).isNotNull();
	}

	@Test
	void getProjectWithRequests_should_return_project_with_at_least_one_request() throws FormatException {
		Project project = connector.getProjectWithRequests(projectId);
		assertThat(project.getLstRequests()).isNotEmpty();
	}

	@Test
	void getAllRequests_should_return_not_empty_list_of_request() throws FormatException {
		List<Request> list = connector.getAllRequests(projectId);
		assertThat(list).isNotEmpty();
	}

	@Test
	void getRequestWithTreatments_should_return_request_with_at_least_one_treatment() throws FormatException {
		Request request = connector.getRequestWithTreatments(requestId);
		assertThat(request.getLstTreatements()).isNotEmpty();
	}

	@Test
	void getLastestTreatments_should_return_not_empty_list_of_treatment() {
		List<Treatment> list = null;
		try {
			list = connector.getLastestTreatments(projectId);
		} catch (FormatException e) {
			fail();
		}
		assertThat(list).isNotEmpty();
	}

	@Test
	void getAllTreatments_should_return_not_emtpy_list_of_treatment() {
		List<Treatment> list = null;
		try {
			list = connector.getAllTreatments(requestId);
		} catch (FormatException e) {
			fail();
		}
		assertThat(list).isNotEmpty();
	}

	@Test
	void getLastTreatment_should_return_not_null_treatment() {
		Treatment treatment = null;
		try {
			treatment = connector.getLastTreatment(requestId);
		} catch (FormatException e) {
			fail();
		}
		assertThat(treatment).isNotNull();
	}

	@Test
	void getLastTreatment_should_return_the_last_treatment() {
		List<Treatment> list = null;
		try {
			list = connector.getAllTreatments(requestId);
		} catch (FormatException e) {
			fail();
		}
		Treatment theLastTreatment = null;
		for (Treatment treatment : list) {
			if (theLastTreatment == null || treatment.getUpdatedAt().isAfter(theLastTreatment.getUpdatedAt()))
				theLastTreatment = treatment;
		}
		Treatment treatment = null;
		try {
			treatment = connector.getLastTreatment(requestId);
		} catch (FormatException e) {
			fail();
		}
		assertThat(treatment).isEqualTo(theLastTreatment);
	}

	@Test
	void getProjectUpdate_should_only_return_the_new_data() {
		//TODO
	}


}
