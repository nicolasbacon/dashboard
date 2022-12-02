package fr.oceaneconsulting.dashboard.chart;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.PieStyler.AnnotationType;
import org.knowm.xchart.style.Styler.LegendPosition;

import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisRequestImpact;
import fr.oceaneconsulting.dashboard.connector.redmine.model.enums.RedmineRequestImpact;
import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.model.Treatment;

public abstract class ChartRender {

	private static final String DIRECTORY = System.getProperty("user.dir")+System.getProperty("file.separator")+"tmp"+System.getProperty("file.separator");
	public static final Logger LOGGER = LogManager.getLogger();

	/**
	 *  Generates a histogram of the status of project requests by criticality.<br><br>
	 *
	 *	Requests are sorted by the status of their last treatment and their impact.<br><br>
	 *
	 *	This is represented by a two-dimensional array <br>using a {@literal Map<String, Map<String, Integer>>}.<br><br>
	 *
	 *	The status of the Treatment is the key of the first Map<> and the impact of the Request is the key of the second Map<>.<br>
	 *
	 *<br>Algorithm:
	 *<ul>
	 *	<li>Scans the list of project Requests.</li>
	 *	<li>For each Request retrieves the last Treatment.</li>
	 *	<li>Creates a new key for each new status encountered with a {@literal Map<String, Integer>} value containing all the impacts according to the project origin.</li>
	 *	<li>If a status is already in the {@literal Map<String, Map<String, Integer>>} of the status, retrieves the {@literal Map<String, Integer>} of the impacts corresponding to this status, to increment the value corresponding to this impact.</li>
	 *</ul>
	 * @param project The project with its list of requests
	 * @return The name of the file if it has been generated otherwise return null
	 */
	public static String generateChartAnomaliesByCriticality(Project project) {
		createDiagramDirectoryIfNotExist();
		List<Request> lstRequests = project.getLstRequests();
		//Name of file
		String pathOfFile = DIRECTORY + project.getOrigin()+ project.getProjectId()+"AnomaliesByCriticality.png";;
		// Create Chart
		CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Anomalies par criticité").build();

		// Customize Chart
		chart.getStyler().setChartBackgroundColor(Color.WHITE);
		chart.getStyler().setStacked(true);

		//Add data
		Map<String, Map<String, Integer>> mapOfStatus = new LinkedHashMap<>();
		for (int i = 0; i < lstRequests.size(); i++) {
			Request request = lstRequests.get(i);
			Treatment treatment = request.getLastTreatment();
			if (treatment != null) {
				// On trie les requests ferme
				if (!treatment.getStatus().equals("FERME")) {
					// Si la map des status contient le status du dernier treatment
					if (mapOfStatus.containsKey(treatment.getStatus())) {
						// On recupere la map des impacts pour ce status
						Map<String, Integer> mapOfImpact = mapOfStatus.get(treatment.getStatus());
						// Si la map des impacts contient l'impact de la request
						if (mapOfImpact.containsKey(request.getImpact())) {
							// On incremente la valeur pour cet impact de ce status
							mapOfImpact.replace(request.getImpact(), mapOfImpact.get(request.getImpact())+ 1);
						} else {
							// Sinon on ajoute l'impact a la deuxieme map et on met le nombre a 1
							mapOfImpact.put(request.getImpact(), 1);
						}
						// Sinon on cree une map contenant tout les impacts en fonction de l'origine du projet
					} else {
						Map<String, Integer> mapOfImpact = new LinkedHashMap<>();
						switch (project.getOrigin()) {
						case "Mantis":
							for (MantisRequestImpact impact : MantisRequestImpact.values()) {
								// On ajoute pas l'impact UNKNOWN
								if (!impact.equals(MantisRequestImpact.UNKNOWN)) {
									mapOfImpact.put(impact.toString(), 0);
								}
							}
							break;
						case "Redmine":
							for (RedmineRequestImpact impact : RedmineRequestImpact.values()) {
								if (!impact.equals(RedmineRequestImpact.UNKNOWN)) {
									mapOfImpact.put(impact.toString(), 0);
								}
							}
							break;
						}
						// On incremente la valeur de l'impact pour ce status
						mapOfImpact.replace(request.getImpact(), 1);
						// On ajout la map des impacts pour ce nouveau status
						mapOfStatus.put(treatment.getStatus(), mapOfImpact);
					}
				}
			}
		}
		// Si la map des status n'est pas vide
		if (!mapOfStatus.isEmpty()) {
			for (String status : mapOfStatus.keySet()) {
				chart.addSeries(status, Arrays.asList(mapOfStatus.get(status).keySet().toArray()), new ArrayList<>(mapOfStatus.get(status).values()));
			}
			// Save Chart
			saveChartAsPNGInPath(chart, pathOfFile);
		}
		return pathOfFile;
	}

	/**
	 * Generates a pie chart of the status of project requests
	 * by sorting the states against the given lists.
	 *
	 * @param project The project with its list of requests
	 * @param listToProcessOCDM The list of states considered to be treated by OCDM
	 * @param listToProcessCustomer The list of states considered to be treated by customer
	 * @return The name of the file if it has been generated otherwise return null
	 */
	public static String generateChartAnomaliesByStateFromConfig(Project project, List<String> listToProcessOCDM, List<String> listToProcessCustomer) {
		createDiagramDirectoryIfNotExist();
		List<Request> lstRequests = project.getLstRequests();
		//Name of file
		String pathOfFile = DIRECTORY + project.getOrigin()+ project.getProjectId()+"AnomaliesByState.png";
		// Create Chart
		PieChart chart = new PieChartBuilder().width(800).height(600).title("Anomalies par etat").build();

		// Customize Chart
		chart.getStyler().setAnnotationType(AnnotationType.Value);
		chart.getStyler().setChartBackgroundColor(Color.WHITE);

		//Add data
		int numberATraiterOcdm = 0;
		int numberATraiterClient = 0;

		for (int i = 0; i < lstRequests.size(); i++) {
			Treatment treatment = lstRequests.get(i).getLastTreatment();
			if (treatment != null) {
				if (listToProcessOCDM.contains(treatment.getStatus())) numberATraiterOcdm++;
				if (listToProcessCustomer.contains(treatment.getStatus())) numberATraiterClient++;
			}
		}
		chart.addSeries("A traiter par OCDM", numberATraiterOcdm);
		chart.addSeries("A traiter par le client", numberATraiterClient);
		// Save Chart
		return saveChartAsPNGInPath(chart, pathOfFile);
	}

	/**
	 * Generates a diagram of the Request status per week<br><br>
	 *
	 * This is represented by a two-dimensional array <br>using a {@literal Map<String, Map<Long, Integer>>}
	 *
	 * The key of the first Map<> corresponds to the legend of the diagram and the key of the second Map<> corresponds to the date of the interval
	 *
	 * The Treatment used is the last one created before the date of the interval
	 *
	 *<br>Algorithm:
	 *<ul>
	 *	<li>Calculates the number of weeks between the two given dates and inserts them in a list</li>
	 *	<li>Create a list containing all intervals per week</li>
	 *	<li>Creates two {@literal Map<Long, Integer>} with key long intervals representing the number of seconds since 1970-01-01</li>
	 *	<li>For each interval, browse each Request</li>
	 *	<li>If the Request was not closed before the interval, retrieve the last Treatment before the interval</li>
	 *	<li>Checks which list contains the status of the Treatment and increments the value corresponding to the current interval</li>
	 *</ul>
	 *
	 * @param project The project with its list of requests
	 * @param startPeriod The start date of the search interval
	 * @param endPeriod The end date of the search interval
	 * @param listToProcessOCDM The list of states considered to be treated by OCDM
	 * @param listToProcessCustomer The list of states considered to be treated by customer
	 * @return The name of the file if it has been generated otherwise return null
	 */
	public static String generateStatusEvolutionChartByWeekFromConfig(Project project, ZonedDateTime startPeriod, ZonedDateTime endPeriod, List<String> listToProcessOCDM, List<String> listToProcessCustomer) {
		createDiagramDirectoryIfNotExist();
		String pathOfFile = DIRECTORY + project.getOrigin()+ project.getProjectId()+"StateEvolutionPerPeriod.png";;
		//Create Chart
		XYChart chart = new XYChartBuilder().width(800).height(600).title("Evolution des états par semaines").xAxisTitle("Date").yAxisTitle("Nombre de demandes").build();
		chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
		chart.getStyler().setXAxisLabelRotation(45);
		chart.getStyler().setChartBackgroundColor(Color.WHITE);

		// On compte le nombre de semaine entre les deux dates données
		long nbrWeekBetweenDate = startPeriod.until(endPeriod, ChronoUnit.WEEKS) + 1;
		// On crée une liste contenant toute les intervals par semaine
		List<ZonedDateTime> listOfStartDate = new ArrayList<>();
		for (int i = 0; i < nbrWeekBetweenDate; i++) {
			listOfStartDate.add(startPeriod.plusWeeks(i));
		}
		listOfStartDate.add(endPeriod);

		// On instancie notre map de map par etat
		Map<Long, Integer> mapOfDateInLongForOcdm = new LinkedHashMap<>();
		for (ZonedDateTime interval : listOfStartDate) {
			mapOfDateInLongForOcdm.put(interval.toEpochSecond(), 0);
		}
		Map<Long, Integer> mapOfDateInLongForClient = new LinkedHashMap<>();
		for (ZonedDateTime interval : listOfStartDate) {
			mapOfDateInLongForClient.put(interval.toEpochSecond(), 0);
		}
		Map<String, Map<Long, Integer>> mapOfState = new LinkedHashMap<>();
		mapOfState.put("A traiter OCDM", mapOfDateInLongForOcdm);
		mapOfState.put("En attente retour Client", mapOfDateInLongForClient);

		List<Request> lstRequests = project.getLstRequests();
		// Pour chaque interval de date
		for (ZonedDateTime startDateInterval : listOfStartDate) {
			// On parcourt chaque request
			for (Request request : lstRequests) {
				// Si la request n'etait pas fermer avant l'intervalle
				if (!request.isClosedBeforThisDate(startDateInterval)) {
					// On recupere le dernier traitement avant l'intervalle
					Treatment lastTreatmentBeforStartDate = request.getLastTreatmentBeforThisDate(startDateInterval);
					if (lastTreatmentBeforStartDate != null) {
						// Si on as bien recuperer un traitement on regarde a quel liste appartient le status puis on incremente
						if (listToProcessOCDM.contains(lastTreatmentBeforStartDate.getStatus())) {
							Map<Long, Integer> mapOfDateInLong = mapOfState.get("A traiter OCDM");
							mapOfDateInLong.replace(startDateInterval.toEpochSecond(), mapOfDateInLong.get(startDateInterval.toEpochSecond())+1);
						}
						if (listToProcessCustomer.contains(lastTreatmentBeforStartDate.getStatus())){
							Map<Long, Integer> mapOfDateInLong = mapOfState.get("En attente retour Client");
							mapOfDateInLong.replace(startDateInterval.toEpochSecond(), mapOfDateInLong.get(startDateInterval.toEpochSecond())+1);
						}
					}
				}
			}
		}

		// ADD DATA TO CHART
		for (Entry<String, Map<Long, Integer>> mapOfStatus : mapOfState.entrySet()) {
			List<Long> xData = new ArrayList<>();
			List<Integer> yData = new ArrayList<>();
			for (Entry<Long, Integer> mapOfDate : mapOfStatus.getValue().entrySet()) {
				xData.add(mapOfDate.getKey());
				yData.add(mapOfDate.getValue());
			}
			chart.addSeries(mapOfStatus.getKey(), xData, yData);
		}

		Map<Object, Object> xMarkMap = new TreeMap<>();
		for (ZonedDateTime interval : listOfStartDate) {
			xMarkMap.put(interval.toEpochSecond(), interval.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}

		chart.setCustomXAxisTickLabelsMap(xMarkMap);

		return saveChartAsPNGInPath(chart, pathOfFile);
	}
	
	private static void createDiagramDirectoryIfNotExist() {
		new File(DIRECTORY).mkdir();
	}
	
	/**
	 * Try to save the chart at the specific location and return the path.
	 * If an error is encountered return null.
	 * @param chart The chart to save.
	 * @param pathToSave The specific location where save.
	 * @return The absolut path of the file or null if an error occurred
	 */
	private static String saveChartAsPNGInPath(Chart<?, ?> chart, String pathToSave) {
		try {
			BitmapEncoder.saveBitmapWithDPI(chart, pathToSave , BitmapFormat.PNG, 300);
		} catch (IOException e) {
			LOGGER.error("Unable to save the file for the path : " + pathToSave + ". Caused by :\n" + e.getMessage());
			pathToSave = null;
		}
		return pathToSave;
	}
}
