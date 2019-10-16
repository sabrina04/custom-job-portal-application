package com.custom.job.portal.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.custom.job.portal.data.model.Job;
import com.custom.job.portal.data.repository.JobRepository;
import com.custom.job.portal.dto.JobDto;
import com.custom.job.portal.dto.JobPositionDto;
import com.custom.job.portal.service.JobService;

public class JobServiceTest {
	
	private JobService jobService;
	private JobRepository jobRepository;
	private RestTemplateBuilder restTemplateBuilder;
	private RestTemplate restTemplate;
	
	private UUID id = UUID.randomUUID();
	private String jobId = "6b0b1cc3de799472d984e6346b929e51";
	private String jobTitle = "Software Architect";
	private String jobLocation = "New York";
	private String normalizedTitle = "software architect";
	
	private LocalDateTime createdDateTime = LocalDateTime.of(2019, 9, 30, 10, 20);
	private Job job = new Job(id, jobId, jobTitle, jobLocation, normalizedTitle, createdDateTime, null, null, false);
	private JobDto jobDto = new JobDto(id, jobId, jobTitle, jobLocation, normalizedTitle);
	private JobPositionDto jobPositionDto = new JobPositionDto("482c1dee-aebe-48b1-92d5-043452eb721f", "Full Time", "ABC Company",
			null, "Software Architect", "New York", null, "abc");
	
	private static final String JOB_SEARCH_URL = "http://api.dataatwork.org/v1/jobs/autocomplete";
	private static final String JOB_POSITION_URL = "https://jobs.github.com/positions.json";
	
	private JobRepository getRepository() {
		JobRepository jobRepository = mock(JobRepository.class);
		when(jobRepository.findByIdAndDeleted(id, false)).thenReturn(Optional.of(job));
		when(jobRepository.findByDeleted(false)).thenReturn(Arrays.asList(job));
		return jobRepository;
	}
	
	@Before
	public void setup() {
		this.jobRepository = getRepository();
		this.restTemplateBuilder = mock(RestTemplateBuilder.class);
		this.restTemplate = mock(RestTemplate.class);
		when(restTemplateBuilder.build()).thenReturn(restTemplate);
		this.jobService = new JobService(restTemplateBuilder, jobRepository);
	}
	
	@Test
	public void testAddJob() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(JOB_SEARCH_URL).queryParam("begins_with", 
				URLEncoder.encode(jobDto.getTitle(), "UTF-8"));
		
		when(this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class)).thenReturn(getJsonJob());
		when(this.jobRepository.findByTitleAndLocationAndDeleted(jobTitle, jobLocation, false)).thenReturn(Optional.empty());
		when(this.jobRepository.save(any(Job.class))).thenReturn(job);
		
		JobDto jobDtoSaved = this.jobService.addJob(jobDto);
		
		assertEquals(jobDto, jobDtoSaved);
	}
	
	@Test
	public void testGetAllJobs() {	
		List<JobDto> jobList = this.jobService.getAllJobs();
		assertEquals(jobList.size(), 1);
		
	}
	
	@Test
	public void testGetJobById() {
		JobDto foundJob = this.jobService.getJobById(id);
		assertEquals(foundJob, jobDto);
	}
	
	@Test
	public void testGetJobPostionsById() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(JOB_POSITION_URL)
				.queryParam("description", URLEncoder.encode(jobDto.getTitle(), "UTF-8"))
				.queryParam("location", URLEncoder.encode(jobDto.getLocation(), "UTF-8"));
		
		when(this.restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class)).thenReturn(getJsonJobPosition());
		
		List<JobPositionDto> jobPositionList = this.jobService.getJobPostionsById(id);
		
		assertEquals(jobPositionList.size(), 1);
		assertEquals(jobPositionList.get(0).getId(), jobPositionDto.getId());
	}

	private ResponseEntity<String> getJsonJob() throws IOException {
		String result = "[{\"uuid\": \"6b0b1cc3de799472d984e6346b929e51\", \"suggestion\": \"Software Architect\", "
				+ "\"normalized_job_title\": \"software architect\", \"parent_uuid\": \"ad7f545d458070129f3ced2d13467813\"}]";
		return ResponseEntity.ok().body(result);
	}
	
	private ResponseEntity<String> getJsonJobPosition() throws IOException {
		String result = "[{ \"id\": \"482c1dee-aebe-48b1-92d5-043452eb721f\", \"type\": \"Full Time\", \"company\": \"ABC Company\","
				+ "\"title\": \"Software Architect\", \"description\": null, "
				+ "\"location\": \"New York\", \"how_to_apply\": \"abc\", \"company_logo\": null}]";
		return ResponseEntity.ok().body(result);
	}
}
