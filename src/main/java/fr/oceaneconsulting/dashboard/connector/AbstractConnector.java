package fr.oceaneconsulting.dashboard.connector;

import java.util.List;

import fr.oceaneconsulting.dashboard.model.Project;
import fr.oceaneconsulting.dashboard.model.Request;
import fr.oceaneconsulting.dashboard.model.Treatment;

public abstract class AbstractConnector<U, V, W> implements Connector{

	protected abstract List<Project> transformProjectList(List<U> listGenericProject);
	protected abstract Project transformProject(U genericProject);
	protected abstract List<Request> transformRequestList(List<V> listGenericRequest);
	protected abstract Request transformRequest(V genericRequest);
	protected abstract List<Treatment> transformTreatmentList(List<W> listGenericTreatments);
	protected abstract Treatment transformTreatment(W genericTreatment);
}
