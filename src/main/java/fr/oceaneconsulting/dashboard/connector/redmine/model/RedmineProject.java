package fr.oceaneconsulting.dashboard.connector.redmine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedmineProject {

	private Integer id;

	private String name;

	//private boolean isEnable;

	private String origin = "Redmine";
}
