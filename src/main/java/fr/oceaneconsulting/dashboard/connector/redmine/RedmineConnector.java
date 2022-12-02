package fr.oceaneconsulting.dashboard.connector.redmine;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.oceaneconsulting.dashboard.connector.AbstractConnector;
import fr.oceaneconsulting.dashboard.connector.redmine.model.RedmineProject;
import fr.oceaneconsulting.dashboard.connector.redmine.model.RedmineRequest;
import fr.oceaneconsulting.dashboard.connector.redmine.model.RedmineTreatment;
import fr.oceaneconsulting.dashboard.connector.redmine.model.enums.RedmineTreatmentStatus;
import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.JSONFormatException;
import fr.oceaneconsulting.dashboard.exception.NotFoundException;
import fr.oceaneconsulting.dashboard.exception.ProjectNotFoundException;
import fr.oceaneconsulting.dashboard.exception.RequestNotFoundException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.model.Treatment;

@Component
public class RedmineConnector extends AbstractConnector<RedmineProject, RedmineRequest, RedmineTreatment>{
	
	public static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Specify the location of Redmine API.
	 */
	public static final String URL = "http://10.20.0.65/";

	/**
	 * The Token to access Redmine API.
	 */
	private static final String TOKEN = "f6e26f7f244263587c7633e1619f94a27f43e329";

	private static RedmineConnector instance;

	private RedmineConnector() {
	}

	public static RedmineConnector getInstance() {
		if (instance == null) {
			instance = new RedmineConnector();
		}
		return instance;
	}

	/**
	 * Ask the Redmine API REST from {@link RedmineConnector#URL}
	 * with X-Redmine-API-Key token {@link RedmineConnector#TOKEN} and
	 * to find all activated RedmineProject and map them into a Project
	 *
	 * @return List of Project
	 * @throws FormatException If the structure of the projects have changed
	 * @throws NotFoundException If no RedmineProject has been found
	 */
	@Override
	public List<Project> getAllProjects() throws FormatException, NotFoundException {
		// Initialise projects list
		List<Project> lstProjects = new ArrayList<>();
		try {
			// Get the request result from redmine
			String result = createRequest("/projects");
			// Get a JsonNode object from the result to get the projects property and to convert to List<MantisProject>
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			List<RedmineProject> lstMantisProjects = Arrays.asList(mapper.treeToValue(mapper.readTree(result).get("projects"), RedmineProject[].class));
			lstProjects = transformProjectList(lstMantisProjects);
		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new ProjectNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during RedmineProject mapping");}

		return lstProjects;
	}

	/**
	 * Ask the Redmine API REST from {@link RedmineConnector#URL}
	 * with X-Redmine-API-Key token {@link RedmineConnector#TOKEN}
	 * to find the RedmineProject and its RedmineRequest
	 * and map them into a Project
	 *
	 * @param projectId The id of the target project
	 * @return Project
	 * @throws FormatException If the structure of the projects have changed
	 * @throws NotFoundException If no RedmineProject has been found
	 */
	@Override
	public Project getProjectWithRequests(int projectId) throws NotFoundException, FormatException {
		// Initialise project
		Project project = new Project();
		try {
			// Get the request result from mantis
			String result = createRequest("/projects/"+projectId);
			// Get a JsonNode object from the result to get the projects property then get first element to convert to Project
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			RedmineProject redmineProject = mapper.treeToValue(mapper.readTree(result).get("project"), RedmineProject.class);
			project = transformProject(redmineProject);
		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new ProjectNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during RedmineProject mapping");}
		// Retrieve all Requests for this project
		project.setLstRequests(getAllRequests(projectId));
		return project;
	}

	/**
	 * Ask the Redmine API REST from {@link RedmineConnector#URL}
	 * with X-Redmine-API-Key token {@link RedmineConnector#TOKEN}
	 * to find all the RedmineRequest and its RedmineTreatment of RedmineProject
	 *
	 * @param projectId The id of the target project
	 * @return List of Request
	 * @throws FormatException If the structure of the request or treatment have changed
	 * @throws NotFoundException If no RedmineRequest has been found
	 */
	@Override
	public List<Request> getAllRequests(int projectId) throws FormatException, NotFoundException {
		// Initialise Requests list
		List<Request> lstRequest= new ArrayList<>();
		try {
			// Get the request result from mantis
			String result = createRequest("/issues.json?project_id=" + projectId);
			// Get a JsonNode object from the result to get the Requests property to convert to List<Request>
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			List<RedmineRequest> lstRedmineRequest = Arrays.asList(mapper.treeToValue(mapper.readTree(result).get("issues"), RedmineRequest[].class));
			lstRequest = transformRequestList(lstRedmineRequest);
			for (Request request : lstRequest) {
				int indexOfRequest = lstRequest.indexOf(request);
				Request requestWithTreatments = getRequestWithTreatments(request.getOriginId());
				lstRequest.set(indexOfRequest, requestWithTreatments);
			}

		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new RequestNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during RedmineRequest or RedmineTreatment mapping");}
		return lstRequest;
	}

	/**
	 * Ask the Redmine API REST from {@link RedmineConnector#URL}
	 * with X-Redmine-API-Key token {@link RedmineConnector#TOKEN}
	 * to find the RedmineRequest and its RedmineTreatment
	 *
	 * @param requestId The id of the target request
	 * @return Request
	 * @throws FormatException If the structure of the request or treatment have changed
	 * @throws NotFoundException If no RedmineRequest has been found
	 */
	@Override
	public Request getRequestWithTreatments(int requestId) throws FormatException, NotFoundException {
		Request request = new Request();
		try {
			// Get the Request result from mantis
			String result = createRequest("/issues/" + requestId + ".json?include=journals");
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			JsonNode node = mapper.readTree(result).get("issue");
			RedmineRequest redmineRequest = mapper.treeToValue(node, RedmineRequest.class);
			request = transformRequest(redmineRequest);
			List<RedmineTreatment> lstRedmineTreatments = Arrays.asList(mapper.treeToValue(node.get("journals"), RedmineTreatment[].class));
			lstRedmineTreatments = lstRedmineTreatments.stream().filter(e -> e != null).collect(Collectors.toList());
			if (!lstRedmineTreatments.isEmpty()) {
				request.setLstTreatements(transformTreatmentList(lstRedmineTreatments));
			} else {
				Treatment treatment = new Treatment();
				int actualStatusId = node.get("status").get("id").asInt();
				treatment.setOriginId(null);
				treatment.setRequest(request);
				treatment.setStatus(RedmineTreatmentStatus.valueOfId(actualStatusId).toString());
				treatment.setUpdatedAt(request.getCreatedAt());
				request.getLstTreatements().add(treatment);
			}
		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new RequestNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during Requests mapping ");}
		return request;
	}

	/**
	 * Ask the Redmine API REST from {@link RedmineConnector#URL}
	 * with X-Redmine-API-Key token {@link RedmineConnector#TOKEN}
	 * to find all the latest RedmineTreatment of RedmineProject
	 *
	 * @param projectId The id of the target project
	 * @return List of Treatment
	 * @throws FormatException If the structure of the request or treatment have changed
	 * @throws NotFoundException If no RedmineRequest has been found
	 */
	@Override
	public List<Treatment> getLastestTreatments(int projectId) throws FormatException, NotFoundException {
		List<Treatment> lstTreatments = new ArrayList<>();

		try {
			// Get the request result from mantis
			String result = createRequest("/issues.json?project_id=" + projectId);
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			JsonNode node = mapper.readTree(result).get("issues");
			for (int i = 0; i < node.size(); i++) {
				JsonNode request = node.get(i);

				lstTreatments.add(new Treatment(
						null,
						null,
						request.get("status").get("name").asText(),
						ZonedDateTime.parse(request.get("updated_on").asText()),
						null
						));

			}
		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new RequestNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during Requests mapping ");}
		return lstTreatments;
	}

	/**
	 * Ask the Redmine API REST from {@link RedmineConnector#URL}
	 * with X-Redmine-API-Key token {@link RedmineConnector#TOKEN}
	 * to find all RedmineTreatment of RedmineRequest
	 *
	 * @param requestId The id of the target request
	 * @return List of Treatment
	 * @throws FormatException If the structure of the request or treatment have changed
	 * @throws NotFoundException If no RedmineRequest has been found
	 */
	@Override
	public List<Treatment> getAllTreatments(int requestId) throws FormatException, NotFoundException {
		List<Treatment> treatments = getRequestWithTreatments(requestId).getLstTreatements();
		return treatments;
	}

	/**
	 * Ask the Redmine API REST from {@link RedmineConnector#URL}
	 * with X-Redmine-API-Key token {@link RedmineConnector#TOKEN}
	 * to find the last RedmineTreatment of RedmineRequest
	 *
	 * @param requestId The id of the target request
	 * @return Treatment
	 * @throws FormatException If the structure of the request or treatment have changed
	 * @throws NotFoundException If no RedmineRequest has been found
	 */
	@Override
	public Treatment getLastTreatment(int requestId) throws FormatException, NotFoundException {
		List<Treatment> treatments = getAllTreatments(requestId);
		return treatments.get(treatments.size() - 1);
	}

	/**
	 * Ask the Redmine API REST from {@link RedmineConnector#URL}
	 * with X-Redmine-API-Key token {@link RedmineConnector#TOKEN}
	 * to find all new RedmineRequest or RedmineTreatment of Project
	 *
	 * @param project Project wich to update
	 * @return Project with only new data
	 * @throws FormatException If the structure of the project or request or treatment have changed
	 * @throws NotFoundException If no RedmineProject or RedmineRequest has been found
	 */
	@Override
	public Project getProjectUpdate(Project project) throws NotFoundException, FormatException  {
		Project tempProject = null;
		try {
			//Get the project and all requests
			String jsonProject = createRequest("/projects/"+project.getOriginId());
			String jsonRequests = createRequest("/issues.json?project_id=" + project.getOriginId() + "&updated_on=>="+project.getLastUpdate().format(DateTimeFormatter.ISO_LOCAL_DATE));

			// Map all objects
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			RedmineProject redmineProject = mapper.treeToValue(mapper.readTree(jsonProject).get("project"), RedmineProject.class);
			List<RedmineRequest> lstRedmineRequest = Arrays.asList(mapper.treeToValue(mapper.readTree(jsonRequests).get("issues"), RedmineRequest[].class));

			//Transforme all objects
			tempProject = transformProject(redmineProject);
			// For all redmine request we get all there treatment
			for (RedmineRequest redmineRequest : lstRedmineRequest) {
				tempProject.getLstRequests().add(getRequestWithTreatments(redmineRequest.getId()));
			}

		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new ProjectNotFoundException("Project was not found");
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during project mapping ");}

		return tempProject;
	}

	/**
	 * Create List of Project from List of RedmineProject
	 *
	 * @param listRedmineProject List of RedmineProject
	 * @return List of Project
	 */
	@Override
	protected
	List<Project> transformProjectList(List<RedmineProject> listRedmineProject) {
		List<Project> lstProjects = new ArrayList<>();
		for (RedmineProject redmineProject : listRedmineProject) {
			Project project = new Project();
			project.setOriginId(redmineProject.getId());
			project.setName(redmineProject.getName());
			project.setEnable(true);
			project.setOrigin(redmineProject.getOrigin());
			lstProjects.add(project);
		}
		return lstProjects;
	}

	/**
	 * Create Project from RedmineProject
	 *
	 * @param RedmineProject
	 * @return Project
	 */
	@Override
	protected
	Project transformProject(RedmineProject redmineProject) {
		Project project = new Project();
		project.setOriginId(redmineProject.getId());
		project.setName(redmineProject.getName());
		project.setEnable(true);
		project.setOrigin(redmineProject.getOrigin());
		return project;
	}

	/**
	 * Create List of Request from List of RedmineRequest
	 *
	 * @param listRedmineRequest List of RedmineRequest
	 * @return List of Request
	 */
	@Override
	protected
	List<Request> transformRequestList(List<RedmineRequest> listRedmineRequest) {
		List<Request> listRequest = new ArrayList<>();
		for (RedmineRequest redmineRequest: listRedmineRequest) {
			listRequest.add(new Request(
					null,
					redmineRequest.getId(),
					redmineRequest.getType().toString(),
					redmineRequest.getImpact().toString(),
					redmineRequest.getCreatedOn(),
					null,
					new ArrayList<>()
					));
		}
		return listRequest;
	}

	/**
	 * Create Request from RedmineRequest
	 *
	 * @param RedmineRequest
	 * @return Request
	 */
	@Override
	protected
	Request transformRequest(RedmineRequest redmineRequest) {
		Request Request = new Request(
				null,
				redmineRequest.getId(),
				redmineRequest.getType().toString(),
				redmineRequest.getImpact().toString(),
				redmineRequest.getCreatedOn(),
				null,
				new ArrayList<>()
				);
		return Request;
	}

	/**
	 * Create List of Treatment from List of RedmineTreatment
	 *
	 * @param listRedmineTreatment List of RedmineTreatment
	 * @return List of Treatment
	 */
	@Override
	protected
	List<Treatment> transformTreatmentList(List<RedmineTreatment> listRedmineTreatment) {
		List<Treatment> listTreatment = new ArrayList<>();
		for (RedmineTreatment redmineTreatment: listRedmineTreatment) {
			listTreatment.add(new Treatment(
					null,
					redmineTreatment.getId(),
					redmineTreatment.getStatus().toString(),
					redmineTreatment.getCreatedOn(),
					null
					));
		}
		return listTreatment;
	}

	/**
	 * Create Treatment from RedmineTreatment
	 *
	 * @param RedmineTreatment
	 * @return Treatment
	 */
	@Override
	protected
	Treatment transformTreatment(RedmineTreatment redmineTreatment) {
		Treatment treatment = new Treatment(
				null,
				redmineTreatment.getId(),
				redmineTreatment.getStatus().toString(),
				redmineTreatment.getCreatedOn(),
				null
				);
		return treatment;
	}

	/**
	 * Execute Request to the ressource was
	 * specified used {@link RedmineConnector#URL}
	 *
	 * @param the requested url resource
	 * @return The response of API
	 * @throws NotFoundException
	 */
	private String createRequest(String resource) throws NotFoundException {
		resource = resource.contains(".json") ? resource : resource.concat(".json");
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Redmine-API-Key", TOKEN);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(URL + resource, HttpMethod.GET, entity, String.class);
		} catch (HttpClientErrorException e) {
			LOGGER.warn("Unable to get a response for the requested resource");
			throw new NotFoundException("Resource not found") {
				private static final long serialVersionUID = -2644937565076869587L;
			};
		}
		return response.hasBody() ? response.getBody() : null;
	}

}
