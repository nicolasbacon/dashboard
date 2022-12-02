package fr.oceaneconsulting.dashboard.connector.mantis;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.oceaneconsulting.dashboard.connector.AbstractConnector;
import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisProject;
import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisRequest;
import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisTreatment;
import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.JSONFormatException;
import fr.oceaneconsulting.dashboard.exception.NotFoundException;
import fr.oceaneconsulting.dashboard.exception.ProjectNotFoundException;
import fr.oceaneconsulting.dashboard.exception.RequestNotFoundException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.model.Treatment;

public class MantisAPIConnector extends AbstractConnector<MantisProject, MantisRequest, MantisTreatment>{
	
	public static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Specify the location of MantisBT API.
	 */
	public static final String URL = "http://10.20.0.206/mantis/api/rest/index.php";

	/**
	 * The Token to access MantisBT API.
	 */
	private static final String TOKEN = "uOV0GNAStXWWilLiyqt7UXBm7oMGSjvh";

	private static MantisAPIConnector instance;

	private MantisAPIConnector() {
	}

	public static MantisAPIConnector getInstance() {
		if (instance == null) {
            instance = new MantisAPIConnector();
        }
        return instance;
	}

	/**
	 * Ask the Mantis API configured in {@link MantisAPIConnector#URL}
	 * to find all activated MantisProject and map them into a Project
	 *
	 * @return List of Project
	 * @throws FormatException If the structure of the projects have changed
	 * @throws NotFoundException If no MantisProject has been found
	 */
	@Override
	public List<Project> getAllProjects() throws FormatException, NotFoundException {
		// Initialise projects list
		List<Project> lstProjects = new ArrayList<>();
		try {
			// Get the request result from mantis
			String result = createRequest("/projects");
			// Get a JsonNode object from the result to get the projects property and to convert to List<MantisProject>
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			List<MantisProject> lstMantisProjects = Arrays.asList(mapper.treeToValue(mapper.readTree(result).get("projects"), MantisProject[].class));
			lstProjects = transformProjectList(lstMantisProjects);
		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new ProjectNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during MantisProject mapping ");}

		return lstProjects;
	}

	/**
	 * Ask the Mantis API configured in {@link MantisAPIConnector#URL}
	 * to find the MantisProject and its MantisRequest but without MantisTreatment
	 * and map them into a Project
	 *
	 * @param projectId The id of the target project
	 * @return Id of MantisProject
	 * @throws FormatException If the structure of the MantisProject or MantisRequest has changed
	 * @throws NotFoundException If no MantisProject has been found
	 */
	@Override
	public Project getProjectWithRequests(int projectId) throws FormatException, NotFoundException{
		// Initialise project
		Project project = new Project();
		try {
			// Get the request result from mantis
			String result = createRequest("/projects/"+projectId);
			// Get a JsonNode object from the result to get the projects property then get first element to convert to Project
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			MantisProject mantisProject = mapper.treeToValue(mapper.readTree(result).get("projects").get(0), MantisProject.class);
			project = transformProject(mantisProject);
		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new ProjectNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during MantisProject mapping ");}
		// Retrieve all Requests for this project
		project.setLstRequests(getAllRequests(projectId));
		return project;
	}

	/**
	 * Ask the Mantis API configured in {@link MantisAPIConnector#URL}
	 * to find all the MantisRequest and its MantisTreatment of MantisProject
	 *
	 * @param projectId The id of the target project
	 * @return Id of MantisProject
	 * @throws FormatException If the structure of the MantisRequest or MantisTreatment has changed
	 * @throws NotFoundException If no MantisRequest has been found
	 */
	@Override
	public List<Request> getAllRequests(int projectId) throws FormatException, NotFoundException {
		// Initialise Requests list
		List<Request> lstRequest= new ArrayList<>();
		try {
			// Get the request result from mantis
			String result = createRequest("/issues?project_id=" + projectId);

			// Get a JsonNode object from the result to get the Requests property to convert to List<Request>
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			List<MantisRequest> lstMantisRequest = Arrays.asList(mapper.treeToValue(mapper.readTree(result).get("issues"), MantisRequest[].class));
			lstMantisRequest.stream().forEach(
					mantisRequest -> mantisRequest.setLstMantisTreatments(
							mantisRequest.getLstMantisTreatments().stream().filter(mantisTreatment -> mantisTreatment != null).collect(Collectors.toList())
							)
					);
			lstRequest = transformRequestList(lstMantisRequest);
		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new RequestNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during MantisRequest mapping ");}
		return lstRequest;
	}

	/**
	 * Ask the Mantis API configured in {@link MantisAPIConnector#URL}
	 * to find the MantisRequest and its MantisTreatment
	 *
	 * @param requestId The id of the target request
	 * @return Id of MantisRequest
	 * @throws FormatException If the structure of the MantisRequest or MantisTreatment has changed
	 * @throws NotFoundException If no MantisRequest has been found
	 */
	@Override
	public Request getRequestWithTreatments(int requestId) throws FormatException, NotFoundException {
		Request Request = new Request();

		try {
			// Get the Request result from mantis
			String result = createRequest("/issues/" + requestId);
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			JsonNode node = mapper.readTree(result).get("issues").get(0);
			MantisRequest mantisRequest = mapper.treeToValue(node, MantisRequest.class);
			List<MantisTreatment> lstMantisTreatments = Arrays.asList(mapper.treeToValue(node.get("history"), MantisTreatment[].class));
			lstMantisTreatments = lstMantisTreatments.stream().filter(e -> e != null).collect(Collectors.toList());
			Request = transformRequest(mantisRequest);
			Request.setLstTreatements(transformTreatmentList(lstMantisTreatments));

		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new RequestNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during MantisRequest mapping ");}
		return Request;
	}

	/**
	 * Ask the Mantis API configured in {@link MantisAPIConnector#URL}
	 * to find all the latest MantisTreatment of MantisProject
	 *
	 * @param projectId The id of the target project
	 * @return Id of MantisProject
	 * @throws FormatException If the structure of the MantisRequest or MantisTreatment has changed
	 * @throws NotFoundException If no MantisRequest has been found
	 */
	@Override
	public List<Treatment> getLastestTreatments(int projectId) throws FormatException, NotFoundException {
		List<Treatment> lstTreatments = new ArrayList<>();

		try {
			// Get the request result from mantis
			String result = createRequest("/issues?project_id=" + projectId);
			ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
			JsonNode node = mapper.readTree(result).get("issues");
			for (int i = 0; i < node.size(); i++) {
				JsonNode Request = node.get(i);
				List<MantisTreatment> lstMantisTreatments = Arrays.asList(mapper.treeToValue(Request.get("history"), MantisTreatment[].class));
				lstMantisTreatments = lstMantisTreatments.stream().filter(e -> e != null).collect(Collectors.toList());
				lstTreatments.add(transformTreatment(lstMantisTreatments.get(lstMantisTreatments.size() - 1)));
			}
		} catch (NotFoundException e) {
			LOGGER.warn("The requested resource has not been found");
			throw new RequestNotFoundException();
		} catch (JsonProcessingException e) {
			LOGGER.warn("Error when processing the response");
			throw new JSONFormatException("Error during MantisRequest or MantisTreatment mapping ");}
		return lstTreatments;
	}

	/**
	 * Ask the Mantis API configured in {@link MantisAPIConnector#URL}
	 * to find all MantisTreatment of MantisRequest
	 *
	 * @param requestId The id of the target request
	 * @return Id of MantisRequest
	 * @throws FormatException If the structure of the MantisRequest or MantisTreatment has changed
	 * @throws NotFoundException If no MantisRequest has been found
	 */
	@Override
	public List<Treatment> getAllTreatments(int requestId) throws FormatException, NotFoundException {
		List<Treatment> treatments;
		treatments = getRequestWithTreatments(requestId).getLstTreatements();
		return treatments;
	}

	/**
	 * Ask the Mantis API configured in {@link MantisAPIConnector#URL}
	 * to find the last MantisTreatment of MantisRequest
	 *
	 * @param requestId The id of the target request
	 * @return Id of MantisRequest
	 * @throws FormatException If the structure of the MantisRequest or MantisTreatment has changed
	 * @throws NotFoundException If no MantisRequest has been found
	 */
	@Override
	public Treatment getLastTreatment(int requestId) throws FormatException, NotFoundException {
		List<Treatment> treatments = getAllTreatments(requestId);
		return treatments.get(treatments.size() - 1);
	}
	/**
	 * @deprecated
	 * Ask the Mantis API configured in {@link MantisAPIConnector#URL}
	 * to find all new MantisRequest or MantisTreatment of Project
	 *
	 * @param project Project wich to update
	 * @return Project with only new data
	 * @throws FormatException If the structure of the MantisRequest or MantisTreatment has changed
	 * @throws NotFoundException If no MantisRequest has been found
	 */
	@Deprecated
	@Override
	public Project getProjectUpdate(Project project) throws NotFoundException, FormatException {
		return null;
	}

	/**
	 * Create List of Project from List of MantisProject
	 *
	 * @param listGenericProject List of MantisProject
	 * @return List of Project
	 */
	@Override
	protected
	List<Project> transformProjectList(List<MantisProject> listMantisProject) {
		List<Project> lstProjects = new ArrayList<>();
		for (MantisProject mantisProject : listMantisProject) {
			Project project = new Project();
			project.setOriginId(mantisProject.getId());
			project.setName(mantisProject.getName());
			project.setEnable(true);
			project.setOrigin(mantisProject.getOrigin());
			lstProjects.add(project);
		}
		return lstProjects;
	}

	/**
	 * Create Project from MantisProject
	 *
	 * @param MantisProject
	 * @return Project
	 */
	@Override
	protected
	Project transformProject(MantisProject mantisProject) {
		Project project = new Project();
		project.setProjectId(mantisProject.getId());
		project.setName(mantisProject.getName());
		project.setEnable(true);
		project.setOrigin(mantisProject.getOrigin());
		return project;
	}

	/**
	 * Create List of Request from List of MantisRequest
	 *
	 * @param listMantisRequest List of MantisRequest
	 * @return List of Request
	 */
	@Override
	protected
	List<Request> transformRequestList(List<MantisRequest> listMantisRequest) {
		List<Request> listRequest = new ArrayList<>();
		for (MantisRequest mantisRequest: listMantisRequest) {
			Request request = new Request(
					null,
					mantisRequest.getId(),
					mantisRequest.getType().toString(),
					mantisRequest.getImpact().toString(),
					mantisRequest.getCreatedAt(),
					null,
					new ArrayList<>()
					);
			request.setLstTreatements(transformTreatmentList(mantisRequest.getLstMantisTreatments()));
			listRequest.add(request);
		}
		return listRequest;
	}

	/**
	 * Create Request from MantisRequest
	 *
	 * @param MantisRequest
	 * @return Request
	 */
	@Override
	protected
	Request transformRequest(MantisRequest mantisRequest) {
		Request request = new Request(
				null,
				mantisRequest.getId(),
				mantisRequest.getType().toString(),
				mantisRequest.getImpact().toString(),
				mantisRequest.getCreatedAt(),
				null,
				new ArrayList<>()
				);
		return request;
	}

	/**
	 * Create List of Treatment from List of MantisTreatment
	 *
	 * @param listMantisTreatment List of MantisTreatment
	 * @return List of Treatment
	 */
	@Override
	protected
	List<Treatment> transformTreatmentList(List<MantisTreatment> listMantisTreatment) {
		List<Treatment> listTreatment = new ArrayList<>();
		for (MantisTreatment mantisTreatment: listMantisTreatment) {
			listTreatment.add(new Treatment(
					null,
					mantisTreatment.getIdTreatment(),
					mantisTreatment.getStatus().toString(),
					mantisTreatment.getCreatedAt(),
					null
					));
		}
		return listTreatment;
	}

	/**
	 * Create Treatment from MantisTreatment
	 *
	 * @param MantisTreatment
	 * @return Treatment
	 */
	@Override
	protected
	Treatment transformTreatment(MantisTreatment mantisTreatment) {
		Treatment treatment = new Treatment(
				null,
				mantisTreatment.getIdTreatment(),
				mantisTreatment.getStatus().toString(),
				mantisTreatment.getCreatedAt(),
				null
				);
		return treatment;
	}

	/**
	 * Execute Request to the ressource was
	 * specified used {@link MantisConnector#URL}
	 *
	 * @param resource the requested url resource
	 * @return The response of API
	 * @throws NotFoundException If no resources has been found
	 */
	private String createRequest(String resource) throws NotFoundException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", TOKEN);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(URL + resource, HttpMethod.GET, entity, String.class);
		} catch (HttpClientErrorException e) {
			LOGGER.warn("Unable to get a response for the requested resource");
			throw new NotFoundException("Resource not found") {
				private static final long serialVersionUID = -6584864306713682984L;
			};
		}
		return response.hasBody() ? response.getBody() : null;
	}

}
