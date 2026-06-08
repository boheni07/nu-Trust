package com.nubiz.nutrust.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import com.nubiz.nutrust.entity.UserStatus;

@Getter
@Builder
public class UserResponse {

	private Long id;
	private String email;
	private String name;
	private String phone;
	private Long companyId;
	private Set<String> roles;
	private UserStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static UserResponse from(com.nubiz.nutrust.entity.User user) {
		Set<String> roles = user.getRoles().stream()
			.map(role -> role.getName())
			.collect(java.util.stream.Collectors.toSet());

		return UserResponse.builder()
			.id(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.phone(user.getPhone())
			.companyId(user.getCompanyId())
			.status(user.getStatus())
			.roles(roles)
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}
