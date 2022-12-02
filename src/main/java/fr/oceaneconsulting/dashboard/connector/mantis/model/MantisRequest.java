package fr.oceaneconsulting.dashboard.connector.mantis.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisRequestImpact;
import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisRequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MantisRequest {

	private Integer id;

	private MantisRequestType type;

	private MantisRequestImpact impact;

	@JsonAlias("history")
	private List<MantisTreatment> lstMantisTreatments = new ArrayList<>();

	@JsonAlias("created_at")
	private ZonedDateTime createdAt;

	@JsonProperty("severity")
	public void setMantisRequestImpact(Map<String, String> impact) {
		this.impact = MantisRequestImpact.valueOfId(Integer.valueOf(impact.get("id")));
	}

	/**
	 * The issue type is in json array,
	 * we must access custom_field params to see
	 * the different field and research which one
	 * has id 3, which corresponds to the Request Type.
	 * @param customField
	 */
	@JsonProperty("custom_fields")
	public void setIssueType(Map<String, Object>[] customField) {
		for (Map<String, Object> map : customField) {
			@SuppressWarnings("unchecked")
			Map<String, Object> field = (Map<String, Object>) map.get("field");
			String value = (String) map.get("value");

			if (field.get("id").equals(3)) {
				this.type = MantisRequestType.valueOfIgnoreSpaceAndCase(value);
			}
		}
	}
}
