package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;
}
