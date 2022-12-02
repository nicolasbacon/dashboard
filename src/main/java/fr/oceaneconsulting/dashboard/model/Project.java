package fr.oceaneconsulting.dashboard.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer projectId;

	// Matches the original id of bug tracker
	@NotNull
	private Integer originId;

	@NotNull
	private String name;

	@NotNull
	private boolean isEnable;

	// Matches the original bug tracker
	@NotNull
	private String origin;

	@NotNull
	@JsonIgnoreProperties
	private ZonedDateTime lastUpdate;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	@ToString.Exclude
	@JsonManagedReference
	@Fetch(FetchMode.JOIN)
	private List<Request> lstRequests = new ArrayList<>();

	public void setLstRequests(List<Request> lstRequest) {
		for (Request request : lstRequest) {
			this.lstRequests.add(request);
			request.setProject(this);
		}
	}

	/**
	 * Override equals methode to compare a project according to
	 * its originId and origin
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Project other = (Project) obj;
		return Objects.equals(origin, other.origin) && Objects.equals(originId, other.originId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(origin, originId);
	}

}
