package fr.oceaneconsulting.dashboard.connector.redmine.model;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.oceaneconsulting.dashboard.connector.redmine.model.enums.RedmineRequestImpact;
import fr.oceaneconsulting.dashboard.connector.redmine.model.enums.RedmineRequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedmineRequest {

	private Integer id;

	private RedmineRequestType type;

	private RedmineRequestImpact impact;

	@JsonAlias("created_on")
	private ZonedDateTime createdOn;

	@JsonProperty("priority")
	public void setRedmineRequestImpact(Map<String, String> priority) {
		this.impact = RedmineRequestImpact.valueOfId(Integer.valueOf(priority.get("id")));
	}

	@JsonProperty("tracker")
	public void setRedmineRequestType(Map<String, String> tracker) {
		this.type = RedmineRequestType.valueOfId(Integer.valueOf(tracker.get("id")));
	}
}
