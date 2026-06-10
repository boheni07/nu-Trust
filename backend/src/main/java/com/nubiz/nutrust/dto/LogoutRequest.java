package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {

	@NotBlank(message = "refreshToken은 필수입니다.")
	private String refreshToken;

	@NotBlank(message = "accessToken은 필수입니다.")
	private String accessToken;
}
