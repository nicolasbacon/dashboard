package fr.oceaneconsulting.dashboard.repository;

import org.springframework.data.repository.CrudRepository;

import fr.oceaneconsulting.dashboard.model.Request;

public interface RequestDAO extends CrudRepository<Request, Integer> {

	Request findByRequestIdAndProject(int requestId, int projectId);

}
