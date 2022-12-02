package fr.oceaneconsulting.dashboard.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer requestId;

	// Matches the original id of bug tracker
	@NotNull
	private Integer originId;

	@NotNull
	private String type;

	@NotNull
	private String impact;

	@NotNull
	private ZonedDateTime createdAt;

	@ManyToOne(targetEntity = Project.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "project")
	@JsonBackReference
	private Project project;

	@OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
	@ToString.Exclude
	@JsonManagedReference
	@Fetch(FetchMode.JOIN)
	private List<Treatment> lstTreatements = new ArrayList<>();

	public void setLstTreatements(List<Treatment> lstTraitements) {
		for (Treatment treatment : lstTraitements) {
			this.lstTreatements.add(treatment);
			treatment.setRequest(this);
		}
	}

	public Treatment getLastTreatment() {
		Treatment lastTreatment = null;
		for (Treatment treatment : lstTreatements) {
			if (lastTreatment == null) {
				lastTreatment = treatment;
			} else {
				if (lastTreatment.getUpdatedAt().isBefore(treatment.getUpdatedAt())) {
					lastTreatment = treatment;
				}
			}
		}
		if(lastTreatment == null) System.err.println(this);
		return lastTreatment;
	}

	public boolean isCreatedBeforThisDate(ZonedDateTime date) {
		if (this.createdAt.isBefore(date)) return true;
		return false;
	}

	public boolean isClosedBeforThisDate(ZonedDateTime date) {
		if (!this.isCreatedBeforThisDate(date))return false;
		Treatment lastTreatment = this.getLastTreatment();
		if ( lastTreatment.getStatus().equals("FERME")) {
			return lastTreatment.getUpdatedAt().isBefore(date) ?  true : false;
		}
		return false;
	}

	public Treatment getLastTreatmentBeforThisDate(ZonedDateTime startDateInterval) {
		if (this.createdAt.isAfter(startDateInterval)) return null;

		Treatment lastTreatment = null;
		for (Treatment treatment : lstTreatements) {
			if (lastTreatment == null) lastTreatment = treatment;
			else if(treatment.getUpdatedAt().isBefore(startDateInterval) && treatment.getUpdatedAt().isAfter(lastTreatment.getUpdatedAt())) {
				lastTreatment = treatment;
			}
		}
		return lastTreatment;
	}

}
