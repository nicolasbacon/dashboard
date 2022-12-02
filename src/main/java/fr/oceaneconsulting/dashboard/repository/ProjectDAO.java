package fr.oceaneconsulting.dashboard.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import fr.oceaneconsulting.dashboard.model.Project;

public interface ProjectDAO extends CrudRepository<Project, Integer> {

	@Override
	List<Project> findAll();

	Project findByOriginIdAndOrigin(int originId, String origin);

}
