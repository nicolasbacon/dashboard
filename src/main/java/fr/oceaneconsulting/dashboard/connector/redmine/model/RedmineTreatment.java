package fr.oceaneconsulting.dashboard.connector.redmine.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fr.oceaneconsulting.dashboard.connector.redmine.RedmineTreatmentDeserializer;
import fr.oceaneconsulting.dashboard.connector.redmine.model.enums.RedmineTreatmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = RedmineTreatmentDeserializer.class)
public class RedmineTreatment {

	private Integer id;

	private RedmineTreatmentStatus status;

	private ZonedDateTime createdOn;

}
