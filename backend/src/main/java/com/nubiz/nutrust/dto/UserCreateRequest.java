package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreateRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Size(min = 8)
	private String password;

	@NotBlank
	private String name;

	private String phone;

	private Long companyId;

	private String role;
}
