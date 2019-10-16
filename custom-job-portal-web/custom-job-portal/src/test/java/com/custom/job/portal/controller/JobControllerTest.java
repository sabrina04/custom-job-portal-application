package com.custom.job.portal.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.custom.job.portal.controller.JobController;
import com.custom.job.portal.dto.JobDto;
import com.custom.job.portal.dto.JobPositionDto;
import com.custom.job.portal.service.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = JobController.class)
public class JobControllerTest {
	
	MockMvc mockMvc;
	
	@Autowired
	protected WebApplicationContext webApplicationContext;
	
	@Autowired
	private JobController jobController;
	
	@MockBean
	private JobService jobService;
	
	private UUID id = UUID.randomUUID();
	
	private JobDto jobDto = new JobDto(id, "6b0b1cc3de799472d984e6346b929e51", "Software Architect", "New York", "software architect");
	
	private JobPositionDto jobPositionDto = new JobPositionDto("482c1dee-aebe-48b1-92d5-043452eb721f", "Full Time", "ABC Company",
			null, "Software Architect", "New York", null, null);
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.jobController).build();
	}
	
	@Test
	public void testAddJobOk() throws Exception {
		
		String jsonString = mapToJson(jobDto);
		
		when(jobService.addJob(jobDto)).thenReturn(jobDto);
		
		this.mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(jsonString))
			.andDo(print()).andExpect(status().isOk())
			.andExpect(content().string(containsString("\"6b0b1cc3de799472d984e6346b929e51\"")))
			.andExpect(content().string(containsString("\"Software Architect\"")))
			.andExpect(content().string(containsString("\"New York\"")))
			.andExpect(content().string(containsString("\"software architect\"")));
		
	}
	
	@Test
	public void testAddJobNotFound() throws Exception {
		
		String jsonString = mapToJson(null);
		
		when(jobService.addJob(null)).thenReturn(null);
		
		this.mockMvc.perform(post("/job").contentType(MediaType.APPLICATION_JSON).content(jsonString))
			.andDo(print()).andExpect(status().isBadRequest()).andReturn();
	}
	
	@Test
	public void testGetAllJobs() throws Exception {
		
		when(jobService.getAllJobs()).thenReturn(Arrays.asList(jobDto));
		
		this.mockMvc.perform(get("/jobs")).andDo(print()).andExpect(status().isOk())
			.andExpect(content().string(containsString("\"6b0b1cc3de799472d984e6346b929e51\"")))
			.andExpect(content().string(containsString("\"Software Architect\"")))
			.andExpect(content().string(containsString("\"New York\"")))
			.andExpect(content().string(containsString("\"software architect\"")));
	}
	
	@Test
	public void testGetJobByIdOk() throws Exception {
				
		when(jobService.getJobById(id)).thenReturn(jobDto);
		
		this.mockMvc.perform(get(String.format("/job/%s", id))).andDo(print()).andExpect(status().isOk())
			.andExpect(content().string(containsString("\"6b0b1cc3de799472d984e6346b929e51\"")))
			.andExpect(content().string(containsString("\"Software Architect\"")))
			.andExpect(content().string(containsString("\"New York\"")))
			.andExpect(content().string(containsString("\"software architect\"")));
	}
	
	@Test
	public void testGetJobByIdNotFound() throws Exception {
		
		UUID randomId = UUID.randomUUID();
		
		when(jobService.getJobById(randomId)).thenReturn(null);
		
		this.mockMvc.perform(get(String.format("/job/%s", randomId))).andDo(print()).andExpect(status().isNotFound()).andReturn();
	}
	
	@Test
	public void testGetJobByIdNull() throws Exception {
		
		UUID nullId = null;
		
		when(jobService.getJobById(nullId)).thenReturn(null);
		
		this.mockMvc.perform(get(String.format("/job/%s", nullId))).andDo(print()).andExpect(status().isBadRequest()).andReturn();
	}
	
	@Test
	public void testGetJobPostionsByIdOk() throws Exception {
		
		when(jobService.getJobPostionsById(id)).thenReturn(Arrays.asList(jobPositionDto));
		
		this.mockMvc.perform(get(String.format("/job/%s/positions", id))).andDo(print()).andExpect(status().isOk())
			.andExpect(content().string(containsString("\"482c1dee-aebe-48b1-92d5-043452eb721f\"")))
			.andExpect(content().string(containsString("\"Software Architect\"")))
			.andExpect(content().string(containsString("\"Full Time\"")))
			.andExpect(content().string(containsString("\"New York\"")))
			.andExpect(content().string(containsString("\"ABC Company\"")));
	}
	
	@Test
	public void testGetJobPostionsByIdNotFound() throws Exception {
		
		UUID randomId = UUID.randomUUID();
		
		when(jobService.getJobPostionsById(randomId)).thenReturn(Collections.emptyList());
		
		this.mockMvc.perform(get(String.format("/job/%s/positions", randomId))).andDo(print())
			.andExpect(status().isOk()).andReturn();
	}
	
	@Test
	public void testGetJobPostionsByIdNull() throws Exception {
		
		UUID nullId = null;
		
		when(jobService.getJobPostionsById(nullId)).thenReturn(null);
		
		this.mockMvc.perform(get(String.format("/job/%s/positions", nullId))).andDo(print())
			.andExpect(status().isBadRequest()).andReturn();
	}

	private String mapToJson(Object obj) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
	}
}
