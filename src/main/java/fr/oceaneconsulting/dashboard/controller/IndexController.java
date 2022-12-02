package fr.oceaneconsulting.dashboard.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.oceaneconsulting.dashboard.exception.FormatException;
import fr.oceaneconsulting.dashboard.exception.NotFoundException;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.service.ApplicationService;
import fr.oceaneconsulting.dashboard.service.ProjectService;

@RestController
@RequestMapping("/")
public class IndexController {

	private final ProjectService projectService;
	private final ApplicationService applicationService;

	/***
	 * Create Controller
	 *
	 * @param ProjectService
	 */
	public IndexController(ProjectService projectService, ApplicationService applicationService) {
		this.projectService = projectService;
		this.applicationService = applicationService;
	}

	/**
	 * Returns a Json response from all projects in database
	 *
	 * @return All projects in Json
	 */
	@GetMapping("/projects")
	@ResponseBody
	public List<Project> getAllProjects() {
		return projectService.getAllProjects();
	}


	/**
	 * Call the ApplicationService to update the database and return "ready" when is finish
	 *
	 * @throws NotFoundException
	 * @throws FormatException
	 * @throws FileNotFoundException
	 */
	@GetMapping("/updateDatabase")
	@ResponseBody
	public String updateDatabase() {
		applicationService.updateDatabase();
		return "ready";
	}

	/**
	 * Call the ApplicationService to update a specific project in database
	 *
	 * @throws NotFoundException
	 * @throws FormatException
	 */
	@PutMapping("/updateProject")
	@ResponseBody
	public Project updateProject(@RequestBody Project projectToUpdate) throws FormatException, NotFoundException {
		return applicationService.updateProjectFromLastUpdate(projectToUpdate);
	}

//	/**
//	 * Returns a Json response from the project
//	 *
//	 * @return A project in Json
//	 * @throws JSONFormatException
//	 * @throws RequestNotFoundException
//	 * @throws ProjectNotFoundException
//	 */
//	@GetMapping("/project/{origin}/{idProject}")
//	@ResponseBody
//	public Project getProject(@PathVariable int idProject, @PathVariable String origin) throws JSONFormatException, ProjectNotFoundException, RequestNotFoundException {
//		return projectService.getProject(idProject, origin);
//	}

//	/**
//	 * Returns a Json response from all lastest treatments
//	 * of project
//	 *
//	 * @return A list of treatments in Json
//	 * @throws JSONFormatException
//	 */
//	@GetMapping("/project/{origin}/{idProject}/treatments")
//	@ResponseBody
//	public List<Treatment> getTreatmentsOfProject(@PathVariable int idProject, @PathVariable String origin) throws JSONFormatException {
//		List<Treatment> treatments = treatmentService.getLatestTreatmentsOfProject(idProject, origin);
//		return treatments;
//	}

//	/**
//	 * Returns a Json response from all Requests of project
//	 *
//	 * @return All Requests for a project in Json
//	 * @throws JSONFormatException
//	 */
//	@GetMapping("/project/{idProject}/requests")
//	@ResponseBody
//	public List<Request> getRequests(@PathVariable int idProject) throws JSONFormatException {
//		return requestService.getAllRequestsOfProject(idProject);
//	}

//	/**
//	 * Returns a Json response from the Request
//	 *
//	 * @return Request in Json
//	 * @throws JSONFormatException
//	 */
//	@GetMapping("/project/request/{origin}/{idRequest}")
//	@ResponseBody
//	public Request getRequest(@PathVariable int idRequest, @PathVariable String origin) throws JSONFormatException {
//		return requestService.getRequest(idRequest,origin);
//	}
}
