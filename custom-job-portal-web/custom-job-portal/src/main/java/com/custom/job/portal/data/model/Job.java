package com.custom.job.portal.data.model;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author sabrina
 *
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name ="id", nullable = false)
	private UUID id;
	
	@NotNull
	@Column(name ="job_id", nullable = false)
	private String jobId;
	
	@NotNull
	@Column(name = "title", nullable = false)
	private String title;
	
	@NotNull
	@Column(name = "location", nullable = false)
	private String location;
	
	@Column(name = "normalized_title", nullable = false)
	private String normalizedTitle;
	
	@NotNull
	@Column(name = "created_on", nullable = false)
	private LocalDateTime createdOn;
	
	@Column(name = "deleted_on")
	private LocalDateTime deletedOn;
	
	@Column(name = "updated_on")
	private LocalDateTime updateOn;
	
	@Column(name = "deleted")
	private Boolean deleted = false;
	
}
