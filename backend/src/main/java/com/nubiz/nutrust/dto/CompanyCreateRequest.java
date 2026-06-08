package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyCreateRequest {

	@NotBlank
	private String name;

	private String businessNumber;
	private String address;
}
