package fr.oceaneconsulting.dashboard.model;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueStatusAtUpdatedAtPeerRequest",columnNames = { "status", "updatedAt", "request" }) })
public class Treatment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer treatmentId;

	// Matches the original id of bug tracker
	private Integer originId;

	@NotNull
	private String status;

	@NotNull
	private ZonedDateTime updatedAt;

	@ManyToOne(targetEntity = Request.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "request")
	@JsonBackReference
	private Request request;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Treatment other = (Treatment) obj;
		return Objects.equals(treatmentId, other.treatmentId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(treatmentId);
	}

}
