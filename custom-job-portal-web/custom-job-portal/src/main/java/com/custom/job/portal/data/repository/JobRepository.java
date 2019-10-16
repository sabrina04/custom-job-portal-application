package com.custom.job.portal.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.custom.job.portal.data.model.Job;

/**
 * 
 * @author sabrina
 *
 */

@Repository
public interface JobRepository extends CrudRepository<Job, UUID> {
	
	Optional<Job> findByTitleAndLocationAndDeleted(String title, String location, boolean deleted);
	
	Optional<Job> findByIdAndDeleted(UUID id, boolean deleted);
	
	List<Job> findByDeleted(boolean deleted);

}
