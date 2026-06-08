package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateRequest {

	private Long id;
	@Email
	private String email;
	private String password;
	private String name;
	private String phone;
	private String status;
}
