package com.custom.job.portal.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.custom.job.portal.data.model.Job;
import com.custom.job.portal.data.repository.JobRepository;
import com.custom.job.portal.dto.JobDto;
import com.custom.job.portal.dto.JobPositionDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author sabrina
 * 
 * The JobService provides basic operations of database model Job.
 *
 */
@Validated
@Service
public class JobService {
	
	private JobRepository jobRepository;
	
	private final RestTemplate restTemplate;
	
	private static final String JOB_SEARCH_URL = "http://api.dataatwork.org/v1/jobs/autocomplete";
	private static final String JOB_POSITION_URL = "https://jobs.github.com/positions.json";
	
	public JobService(RestTemplateBuilder restTemplateBuilder, JobRepository jobRepository) {
		this.restTemplate = restTemplateBuilder.build();
		this.jobRepository = jobRepository;
	}
	
	/**
	 * attempts to save the incoming jobDto object into the database.
	 * @param jobDto is the incoming information about job.
	 * @return the saved JobDto or null.
	 */
	public JobDto addJob(@Valid JobDto jobDto) {
		if (null == jobDto) {
			return null;
		}
		
		// builds the http header and uri to call the external Open Skill API.
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = null;
		try {
			builder = UriComponentsBuilder.fromHttpUrl(JOB_SEARCH_URL).queryParam("begins_with", 
						URLEncoder.encode(jobDto.getTitle(), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
	  
		if (200 == response.getStatusCodeValue()) {
			// parses the json response into JobDto list
			ObjectMapper mapper = new ObjectMapper(); 
			JsonNode root = null; 
			List<JobDto> jobDtos = new ArrayList<>();
			
			try { 
			  root = mapper.readTree(response.getBody()); 
			  Iterator<JsonNode> iterator = root.iterator(); 
		  
			  while (iterator.hasNext()) {
				  JsonNode node = iterator.next();
				  JobDto job = new JobDto();
				  job.setId(null);
				  job.setJobId(node.findValue("uuid").asText());
				  job.setTitle(node.findValue("suggestion").asText());
				  job.setNormalizedTitle(node.findValue("normalized_job_title").asText());
				  job.setLocation(WordUtils.capitalize(jobDto.getLocation()));
				  jobDtos.add(job);
			  }
			} catch (IOException e) { 
			  e.printStackTrace(); 
			}
			
			// gets the first job to store into database
			JobDto closestMatchJob = findClosestMatchJobTitle(jobDtos, jobDto);
			
			if (null == closestMatchJob && !jobDtos.isEmpty()) {
				closestMatchJob = jobDtos.get(0);
			}
		
			Optional<Job> jobOptional = this.jobRepository.findByTitleAndLocationAndDeleted(closestMatchJob.getTitle(), closestMatchJob.getLocation(), false);
			
			if (jobOptional.isPresent()) {
				return this.convertToDto(jobOptional.get());
			}
			
			return this.convertToDto(this.jobRepository.save(this.convertToModel(closestMatchJob)));
		}
		
		return null;
	}
	
	/**
	 * retrieves all non-deleted jobs from the database.
	 * @return a list of JobDto.
	 */
	public List<JobDto> getAllJobs() {
		List<Job> jobList = this.jobRepository.findByDeleted(false);
		return StreamSupport.stream(jobList.spliterator(), false).map(this::convertToDto).collect(Collectors.toList());
	}
	
	/**
	 * retrieves job by its id from database.
	 * @param id of the job.
	 * @return the found JobDto or null.
	 */
	public JobDto getJobById(UUID id) {
		if (null == id) {
			return null;
		}
		
		Optional<Job> jobOptional = this.jobRepository.findByIdAndDeleted(id, false);
		
		if (!jobOptional.isPresent()) {
			return null;
		}
		
		return this.convertToDto(jobOptional.get());
	}
	
	/**
	 * retrieves all job positions by title and location using external rest api.
	 * @param id of the existing job in the database.
	 * @return a list of JobPositionDto.
	 */
	public List<JobPositionDto> getJobPostionsById(UUID id) {
		if (null == id) {
			return null;
		}
		
		JobDto jobDto = this.getJobById(id);
		
		if (null == jobDto) {
			return null;
		}
		
		// builds the uri to fetch the job position data
		UriComponentsBuilder builder = null;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		try {
			builder = UriComponentsBuilder.fromHttpUrl(JOB_POSITION_URL)
						.queryParam("description", URLEncoder.encode(jobDto.getTitle(), "UTF-8"))
						.queryParam("location", URLEncoder.encode(jobDto.getLocation(), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		ResponseEntity<String> response =
		restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
	  
		if (200 == response.getStatusCodeValue()) {
			ObjectMapper mapper = new ObjectMapper(); 
			JsonNode root = null; 
			List<JobPositionDto> jobPositionDtos = new ArrayList<>();
			
			// builds the list from json data
			try { 
			  root = mapper.readTree(response.getBody()); 
			  Iterator<JsonNode> iterator = root.iterator();
		  
			  while (iterator.hasNext()) {
				  JsonNode node = iterator.next();
				  JobPositionDto jobPosition = new JobPositionDto();
				  jobPosition.setId(node.findValue("id").asText());
				  jobPosition.setType(node.findValue("type").asText());
				  jobPosition.setCompany(node.findValue("company").asText());
				  jobPosition.setTitle(node.findValue("title").asText());
				  jobPosition.setLocation(node.findValue("location").asText());
				  jobPosition.setDescription(node.findValue("description").asText());
				  jobPosition.setHowToApply(node.findValue("how_to_apply").asText());
				  jobPosition.setCompanyLogo(node.findValue("company_logo").asText());
				  jobPositionDtos.add(jobPosition);
			  }
			} catch (IOException e) { 
			  e.printStackTrace(); 
			}
			
			return jobPositionDtos;
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * converts Job Dto to Model object
	 * @param jobDto object.
	 * @return Job model.
	 */
	public Job convertToModel(@Valid JobDto jobDto) {
		Job job = new Job();
		job.setId(jobDto.getId());
		job.setJobId(jobDto.getJobId());
		job.setTitle(jobDto.getTitle());
		job.setLocation(jobDto.getLocation());
		job.setNormalizedTitle(jobDto.getNormalizedTitle());
		job.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
		return job;
	}
	
	/**
	 * converts Job model to Dto object.
	 * @param job model.
	 * @return JobDto.
	 */
	public JobDto convertToDto(@Valid Job job) {
		JobDto jobDto = new JobDto();
		jobDto.setId(job.getId());
		jobDto.setJobId(job.getJobId());
		jobDto.setTitle(job.getTitle());
		jobDto.setLocation(job.getLocation());
		jobDto.setNormalizedTitle(job.getNormalizedTitle());
		return jobDto;
	}
	
	private JobDto findClosestMatchJobTitle(List<JobDto> jobList, JobDto target) {
	    int distance = Integer.MAX_VALUE;
	    JobDto jobDto = null;
	    for (JobDto job : jobList) {
	        int currentDistance = StringUtils.getLevenshteinDistance(job.getTitle(), target.getTitle());
	        if(currentDistance < distance) {
	            distance = currentDistance;
	            jobDto = job;
	        }
	    }
	    return jobDto;
	}

}
