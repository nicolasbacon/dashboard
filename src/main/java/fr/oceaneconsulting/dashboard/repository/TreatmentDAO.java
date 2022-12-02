package fr.oceaneconsulting.dashboard.repository;

import org.springframework.data.repository.CrudRepository;

import fr.oceaneconsulting.dashboard.model.Treatment;

public interface TreatmentDAO extends CrudRepository<Treatment, Integer> {

}
