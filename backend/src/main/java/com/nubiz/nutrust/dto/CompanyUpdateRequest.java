package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyUpdateRequest {

	@NotNull
	private Long id;

	private String name;
	private String businessNumber;
	private String address;
	private String status;
}
