package com.nubiz.nutrust.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CompanyResponse {

	private Long id;
	private String name;
	private String businessNumber;
	private String address;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static CompanyResponse from(com.nubiz.nutrust.entity.Company company) {
		return CompanyResponse.builder()
			.id(company.getId())
			.name(company.getName())
			.businessNumber(company.getBusinessNumber())
			.address(company.getAddress())
			.status(company.getStatus())
			.createdAt(company.getCreatedAt())
			.updatedAt(company.getUpdatedAt())
			.build();
	}
}
