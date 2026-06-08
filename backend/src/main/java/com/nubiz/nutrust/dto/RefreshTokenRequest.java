package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefreshTokenRequest {

	@NotBlank
	private String refreshToken;
}
