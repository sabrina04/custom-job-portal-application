package com.custom.job.portal.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.custom.job.portal.dto.JobDto;
import com.custom.job.portal.dto.JobPositionDto;
import com.custom.job.portal.service.JobService;

import io.swagger.annotations.Api;

/**
 * 
 * @author sabrina
 * 
 * JobController REST API's provides basic Http POST, GET methods implementation.
 *
 */

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Api("Job Controller Api")
public class JobController {
	
	@Autowired
	private JobService jobService;
	
	/**
	 * saves the JobDto into database.
	 * @param jobDto the incoming job object.
	 * @return the saved JobDto with Http Ok status or when object is not saved properly returns Http BadRequest.
	 */
	@PostMapping("/job")
	public ResponseEntity<JobDto> addJob(@RequestBody @Valid JobDto jobDto) {
		
	  JobDto savedJobDto = this.jobService.addJob(jobDto);
	  
	  if (null != savedJobDto) {
		  return ResponseEntity.ok().body(savedJobDto);
	  }
	  
	  return ResponseEntity.badRequest().build();
	}
	
	/**
	 * retrieves all non-deleted jobs from the database.
	 * @return a list of JobDto with Http Ok status.
	 */
	@GetMapping("/jobs")
	public ResponseEntity<List<JobDto>> getAllJobs() {
		
		List<JobDto> jobDtoList = this.jobService.getAllJobs();
		
		return ResponseEntity.ok().body(jobDtoList);
	}
	
	/**
	 * retrieves job by its id from database.
	 * @param id of the job.
	 * @return the found JobDto with Http status ok or when job not found returns Http NotFound status.
	 */
	@GetMapping("/job/{id}")
	public ResponseEntity<JobDto> getJobById(@PathVariable UUID id) {
		
		JobDto jobDto = this.jobService.getJobById(id);
		
		if (null != jobDto) {
			return ResponseEntity.ok().body(jobDto);
		}
		
		return ResponseEntity.notFound().build();
	}
	
	/**
	 * retrieves all job positions by title and location using external rest api.
	 * @param id of the existing job in the database.
	 * @return a list of JobPositionDto with Http status ok.
	 */
	@GetMapping("/job/{id}/positions")
	public ResponseEntity<List<JobPositionDto>> getJobPostionsById(@PathVariable UUID id) {
		
		List<JobPositionDto> jobPositionDtoList = this.jobService.getJobPostionsById(id);
		
		return ResponseEntity.ok().body(jobPositionDtoList);
	}

}
