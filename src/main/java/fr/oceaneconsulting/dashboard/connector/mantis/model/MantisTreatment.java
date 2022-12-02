package fr.oceaneconsulting.dashboard.connector.mantis.model;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import fr.oceaneconsulting.dashboard.connector.mantis.MantisTreatmentDeserializer;
import fr.oceaneconsulting.dashboard.connector.mantis.model.enums.MantisTreatmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = MantisTreatmentDeserializer.class)
public class MantisTreatment {

	private Integer idTreatment;

	private MantisTreatmentStatus status;

	private ZonedDateTime createdAt;

}
