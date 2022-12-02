package fr.oceaneconsulting.dashboard.connector.redmine;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.NotFoundException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.model.Treatment;

class RedmineConnectorTest {

	private RedmineConnector connector;
	private int projectId = 40;
	private int requestId = 7850;

	@BeforeEach
	void initConnector() {
		connector = RedmineConnector.getInstance();
	}

	@AfterEach
	void undefConnector() {
		connector = null;
	}

	@Test
	void getAllProjects_should_return_not_null_list() throws FormatException, NotFoundException {
		List<Project> list = connector.getAllProjects();
		assertThat(list).isNotNull();
	}

	@Test
	void getProjectWithRequests_should_return_project_with_at_least_one_request() throws FormatException, NotFoundException {
		Project project = connector.getProjectWithRequests(projectId);
		assertThat(project.getLstRequests()).isNotEmpty();
	}

	@Test
	void getAllRequests_should_return_not_empty_list_of_request() throws FormatException, NotFoundException {
		List<Request> list = connector.getAllRequests(projectId);
		assertThat(list).isNotEmpty();
	}

	@Test
	void getRequestWithTreatments_should_return_request_with_at_least_one_treatment() throws FormatException, NotFoundException {
		Request request = connector.getRequestWithTreatments(requestId);
		assertThat(request.getLstTreatements()).isNotEmpty();
	}

	@Test
	void getLastestTreatments_should_return_not_empty_list_of_treatment() throws FormatException, NotFoundException {
		List<Treatment> list = connector.getLastestTreatments(projectId);
		assertThat(list).isNotEmpty();
	}

	@Test
	void getAllTreatments_should_return_not_emtpy_list_of_treatment() throws FormatException, NotFoundException {
		List<Treatment> list = connector.getAllTreatments(requestId);
		assertThat(list).isNotEmpty();
	}

	@Test
	void getLastTreatment_should_return_not_null_treatment() throws FormatException, NotFoundException {
		Treatment treatment = connector.getLastTreatment(requestId);
		assertThat(treatment).isNotNull();
	}

	@Test
	void getLastTreatment_should_return_the_last_treatment() throws FormatException, NotFoundException {
		List<Treatment> list = connector.getAllTreatments(requestId);
		Treatment theLastTreatment = null;
		for (Treatment treatment : list) {
			if (theLastTreatment == null || treatment.getUpdatedAt().isAfter(theLastTreatment.getUpdatedAt()))
				theLastTreatment = treatment;
		}
		Treatment treatment = connector.getLastTreatment(requestId);
		assertThat(treatment).isEqualTo(theLastTreatment);
	}

	@Test
	void getProjectUpdate_should_only_return_the_new_data() {
		//TODO
	}

}
