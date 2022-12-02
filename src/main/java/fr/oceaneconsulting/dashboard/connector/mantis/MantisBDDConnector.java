package fr.oceaneconsulting.dashboard.connector.mantis;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import fr.oceaneconsulting.dashboard.connector.AbstractConnector;
import fr.oceaneconsulting.dashboard.connector.mantis.dal.MantisDAOFactory;
import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisProject;
import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisRequest;
import fr.oceaneconsulting.dashboard.connector.mantis.model.MantisTreatment;
import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.model.Treatment;

public class MantisBDDConnector extends AbstractConnector<MantisProject, MantisRequest, MantisTreatment>{
	
	private static MantisBDDConnector instance;
	private MantisDAOFactory mantisDAOFactory;

	private MantisBDDConnector() {
		mantisDAOFactory = MantisDAOFactory.getInstance();
	}

	public static MantisBDDConnector getInstance() {
		if (instance == null) instance = new MantisBDDConnector();
        return instance;
	}

	/**
	 * Gets the MantisPojectDao to find all activated MantisProjects
	 * and map them into a List of Project
	 *
	 * @return List of Project
	 * @throws FormatException If the structure of the projects have changed
	 */
	@Override
	public List<Project> getAllProjects() throws FormatException {
		List<MantisProject> lstMantisProjects = mantisDAOFactory.getMantisProjectDao().getEnabledProjects();
		List<Project> lstProjects = transformProjectList(lstMantisProjects);
		return lstProjects;
	}

	/**
	 * Gets the MantisPojectDao and the MantisRequestDao to find a MantisProjects
	 * and all its MantisRequest and map them into a Project
	 *
	 * @param projectId The id of the target project
	 * @return Project
	 * @throws FormatException If the structure of the projects or requests have changed
	 */
	@Override
	public Project getProjectWithRequests(int projectId) throws FormatException {
		MantisProject mantisProject = mantisDAOFactory.getMantisProjectDao().getProject(projectId);
		List<MantisRequest> lstMantisRequest = mantisDAOFactory.getMantisRequestDao().getRequestOfProject(projectId);
		Project project = transformProject(mantisProject);
		project.setLstRequests(transformRequestList(lstMantisRequest));
		return project;
	}

	/**
	 * Gets the MantisRequestDao to find all MantisRequest and all their MantisTreatment
	 * and map them into a List of Request
	 *
	 * @param projectId The id of the target project
	 * @return List of Request
	 * @throws FormatException If the structure of the requests have changed
	 */
	@Override
	public List<Request> getAllRequests(int projectId) throws FormatException {
		List<MantisRequest> lstMantisRequest = mantisDAOFactory.getMantisRequestDao().getRequestOfProject(projectId);
		List<Request> lstRequest = transformRequestList(lstMantisRequest);
		return lstRequest;
	}

	/**
	 * Gets the MantisRequestDao and the MantisTreatmentDao to find a MantisRequest
	 * and all its MantisTreatment and map them into a Request
	 *
	 * @param requestId The id of the target request
	 * @return Request
	 * @throws FormatException If the structure of the projects or requests have changed
	 */
	@Override
	public Request getRequestWithTreatments(int requestId) throws FormatException {
		MantisRequest mantisRequest = mantisDAOFactory.getMantisRequestDao().getRequest(requestId);
		List<MantisTreatment> lstMantisTreatment = mantisDAOFactory.getMantisTreatmentDao().getTreatmentOfRequest(requestId);
		Request request = transformRequest(mantisRequest);
		request.setLstTreatements(transformTreatmentList(lstMantisTreatment));
		return request;
	}

	/**
	 * Gets the MantisTreatmentDao to find all latest MantisTreatment of a project
	 * and map them into a List of Treatment
	 *
	 * @param projectId The id of the target project
	 * @return List of Treatment
	 * @throws FormatException If the structure of the treatment have changed
	 */
	@Override
	public List<Treatment> getLastestTreatments(int projectId) throws FormatException  {
		List<MantisTreatment> lstMantisTreatments = mantisDAOFactory.getMantisTreatmentDao().getLatestTreatmentsOfProject(projectId);
		List<Treatment> lstTreatments = transformTreatmentList(lstMantisTreatments);
		return lstTreatments;
	}

	/**
	 * Gets the MantisTreatmentDao to find all MantisTreatment of a request
	 * and map them into a List of Treatment
	 *
	 * @param projectId The id of the target request
	 * @return List of Treatment
	 * @throws FormatException If the structure of the treatment have changed
	 */
	@Override
	public List<Treatment> getAllTreatments(int requestId) throws FormatException {
		List<MantisTreatment> lstMantisTreatment = mantisDAOFactory.getMantisTreatmentDao().getTreatmentOfRequest(requestId);
		List<Treatment> lstTreatments = transformTreatmentList(lstMantisTreatment);
		return lstTreatments;
	}

	/**
	 * Gets the MantisTreatmentDao to find the last MantisTreatment of a request
	 * and map them into a Treatment
	 *
	 * @param projectId The id of the target request
	 * @return Treatment
	 * @throws FormatException If the structure of the treatment have changed
	 */
	@Override
	public Treatment getLastTreatment(int requestId) throws FormatException {
		MantisTreatment mantisTreatment = mantisDAOFactory.getMantisTreatmentDao().getLastTreatmentOfRequest(requestId);
		Treatment treatment = transformTreatment(mantisTreatment);
		return treatment;
	}

	/**
	 * Gets the MantisProjectDao and MantisRequestDao
	 * to find all new MantisRequest or MantisTreatment of Project from {@link Project#getLastUpdate()}
	 *
	 * @param project Project wich to update
	 * @return Project
	 * @throws FormatException If the structure of the project or request or treatment have changed
	 */
	@Override
	public Project getProjectUpdate(Project project) throws FormatException {
		Project tempProject = null;
		MantisProject mantisProject = mantisDAOFactory.getMantisProjectDao().getProject(project.getOriginId());
		long from;
		if (project.getLastUpdate() == null) from = 0;
		else from = project.getLastUpdate().toInstant().getLong(ChronoField.INSTANT_SECONDS);
		List<MantisRequest> lstRequest = mantisDAOFactory.getMantisRequestDao().getRequestOfProjectFrom(project.getOriginId(), from);
		tempProject = transformProject(mantisProject);
		tempProject.setLstRequests(transformRequestList(lstRequest));
		tempProject.setLastUpdate(ZonedDateTime.now());
		return tempProject;
	}

	/**
	 * Create List of Project from List of MantisProject
	 *
	 * @param listGenericProject List of MantisProject
	 * @return List of Project
	 */
	@Override
	protected
	List<Project> transformProjectList(List<MantisProject> listGenericProject) {
		List<Project> lstProjects = new ArrayList<>();
		for (MantisProject mantisProject : listGenericProject) {
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
		project.setOriginId(mantisProject.getId());
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
		List<Request> lstRequests = new ArrayList<>();
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
			lstRequests.add(request);
		}
		return lstRequests;
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
		Request Request = new Request(
				null,
				mantisRequest.getId(),
				mantisRequest.getType().toString(),
				mantisRequest.getImpact().toString(),
				mantisRequest.getCreatedAt(),
				null,
				new ArrayList<>()
				);
		return Request;
	}

	/**
	 * Create List of Treatment from List of MantisTreatment
	 *
	 * @param listMantisTreatment List of MantisTreatment
	 * @return List of Treatment
	 */
	@Override
	protected
	List<Treatment> transformTreatmentList(List<MantisTreatment> listMantisTreatments) {
		List<Treatment> lstTreatments = new ArrayList<>();
		for (MantisTreatment mantisTreatment: listMantisTreatments) {
			lstTreatments.add(new Treatment(
					null,
					mantisTreatment.getIdTreatment(),
					mantisTreatment.getStatus().toString(),
					mantisTreatment.getCreatedAt(),
					null
					));
		}
		return lstTreatments;
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

}
