package com.nubiz.nutrust.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

	private String accessToken;
	private String refreshToken;
	private UserInfo user;

	@Getter
	@Builder
	public static class UserInfo {
		private Long id;
		private String email;
		private String name;
		private String company;
	}
}
