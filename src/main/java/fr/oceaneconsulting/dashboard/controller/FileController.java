package fr.oceaneconsulting.dashboard.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import fr.oceaneconsulting.dashboard.service.ProjectService;

@RestController
public class FileController {

	@Autowired
	ProjectService projectService;

	/**
	 * Ask the ProjectService to generate an anomaly diagram by criticality,
	 * then send the file in png format in response if it has been created
	 * otherwise returns null
	 *
	 * @param originId The original id of the bug tracker
	 * @param origin The name of the bug tracker to request
	 * @return ResponseEntity containing an image in png format of the diagram if it has been generated otherwise returns null
	 * @throws FileNotFoundException If the file generated was not found
	 */
	@GetMapping("/{origin}/{originId}/AnomalyByCriticality")
	@ResponseBody
	public ResponseEntity<InputStreamResource> sendAnomaliesChartByCriticality(@PathVariable int originId, @PathVariable String origin) throws FileNotFoundException {
		File diagramme = projectService.getAnomaliesChartByCriticality(originId, origin);
		ResponseEntity<InputStreamResource> response = null;
		if(diagramme.exists()) {
			MediaType contentType = MediaType.IMAGE_PNG;
			InputStream in = new FileInputStream(diagramme);
			response = ResponseEntity
					.ok()
					.contentType(contentType)
					.body(new InputStreamResource(in));
			diagramme.delete();
		}
		return response;
	}

	/**
	 * Ask the ProjectService to generate an anomaly diagram by state,
	 * then send the file in png format in response if it has been created
	 * otherwise returns null
	 *
	 * @param originId The original id of the bug tracker
	 * @param origin The name of the bug tracker to request
	 * @return ResponseEntity containing an image in png format of the diagram if it has been generated otherwise returns null
	 * @throws FileNotFoundException If the file generated was not found
	 */
	@GetMapping("/{origin}/{originId}/AnomalyByState")
	@ResponseBody
	public ResponseEntity<InputStreamResource> sendAnomaliesChartByState(@PathVariable int originId, @PathVariable String origin) throws FileNotFoundException {
		File diagramme = projectService.getAnomaliesChartByState(originId, origin);
		ResponseEntity<InputStreamResource> response = null;
		if (diagramme.exists()) {
			MediaType contentType = MediaType.IMAGE_PNG;
			InputStream in = new FileInputStream(diagramme);
			response = ResponseEntity
					.ok()
					.contentType(contentType)
					.body(new InputStreamResource(in));
			diagramme.delete();
		}
		return response;
	}

	/**
	 * Ask the ProjectService to generate a status follow-up diagram,
	 * then send the file in png format in response if it has been created
	 * otherwise returns null
	 *
	 * @param origin The name of the bug tracker to request
	 * @param originId The original id of the bug tracker
	 * @param startPeriodDate The start date of the search interval
	 * @param endPeriodDate The end date of the search interval
	 * @return ResponseEntity containing an image in png format of the diagram if it has been generated otherwise returns null
	 */
	@GetMapping("/{origin}/{originId}/{startPeriodDate}/{endPeriodDate}/StatusEvolutionChartByWeek")
	@ResponseBody
	public ResponseEntity<InputStreamResource> sendStatusEvolutionChartByWeek(
			@PathVariable int originId, @PathVariable String origin,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date startPeriodDate,
		    @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date endPeriodDate) throws FileNotFoundException
	{
		ZonedDateTime startPeriod = ZonedDateTime.ofInstant(startPeriodDate.toInstant(), ZoneId.systemDefault());
		ZonedDateTime endPeriod = ZonedDateTime.ofInstant(endPeriodDate.toInstant(), ZoneId.systemDefault());
		File diagramme = projectService.getStatusEvolutionChartByWeek(originId, origin, startPeriod, endPeriod);
		ResponseEntity<InputStreamResource> response = null;
		if (diagramme.exists()) {
			MediaType contentType = MediaType.IMAGE_PNG;
			InputStream in = new FileInputStream(diagramme);
			response = ResponseEntity
					.ok()
					.contentType(contentType)
					.body(new InputStreamResource(in));
			diagramme.delete();
		}
		return response;
	}

}
