package com.custom.job.portal.dto;

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
public class JobPositionDto {

	private String id;
	
	private String type;
	
	@NotNull
	private String company;
	
	private String companyLogo;
	
	@NotNull
	private String title;
	
	@NotNull
	private String location;
	
	private String description;
	
	private String howToApply;
	
}
