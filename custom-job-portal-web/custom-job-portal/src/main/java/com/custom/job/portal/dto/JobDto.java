package com.custom.job.portal.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author sabrina
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {

	private UUID id;

	private String jobId;
	
	@NotNull
	private String title;
	
	@NotNull
	private String location;
	
	private String normalizedTitle;
}
