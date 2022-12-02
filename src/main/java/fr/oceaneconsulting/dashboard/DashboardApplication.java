package fr.oceaneconsulting.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.oceaneconsulting.dashboard.properties.AbstractReader;
import fr.oceaneconsulting.dashboard.service.ApplicationService;

@SpringBootApplication
public class DashboardApplication implements CommandLineRunner{
	
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String APP_CONFIG_DIRECTORY = System.getProperty("user.dir") + FILE_SEPARATOR + "config";
	public static final String NAME_OF_FILE_LIST_PROJECTS = "List_of_projects_to_display.xml";
	public static final String NAME_OF_FILE_FILTER_TREATMENTS = "Filter_treatments_by_projects.xml";
	
	/**
	 * The path of the file contains the list of project to update
	 */
	public static final String PATH_FILE_LIST_PROJECTS = APP_CONFIG_DIRECTORY + FILE_SEPARATOR + NAME_OF_FILE_LIST_PROJECTS;

	/**
	 * The path of the file contains the configuration of the enums of the requests 
	 */
	public static final String PATH_FILE_FILTER_TREATMENTS = APP_CONFIG_DIRECTORY + FILE_SEPARATOR + NAME_OF_FILE_FILTER_TREATMENTS;

	public static void main(String[] args) {
		SpringApplication.run(DashboardApplication.class, args);
	}
	
	@Autowired
	ApplicationService applicationService ;

	@Override
	public void run(String... args) throws Exception {
		AbstractReader.generateConfigFile();
		applicationService.updateDatabase();
	}

}
