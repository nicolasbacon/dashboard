package fr.oceaneconsulting.dashboard.connector.mantis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MantisProject {

	private Integer id;

	private String name;

	private boolean enabled;

	private String origin = "Mantis";
}
