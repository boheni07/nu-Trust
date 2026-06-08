package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.LoginRequest;
import com.nubiz.nutrust.dto.LoginResponse;
import com.nubiz.nutrust.dto.RegisterRequest;
import com.nubiz.nutrust.entity.Role;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.repository.RoleRepository;
import com.nubiz.nutrust.repository.UserRepository;
import com.nubiz.nutrust.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;
	private final StringRedisTemplate redisTemplate;

	@Value("${app.jwt.refreshExpiration}")
	private long refreshTokenExpiration;

	@Transactional
	public LoginResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);

		String accessToken = tokenProvider.createAccessToken(authentication);
		String refreshToken = tokenProvider.createRefreshToken(authentication);

		// RefreshToken Redis 저장
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new RuntimeException("User not found"));
		redisTemplate.opsForValue().set(
			"refreshToken:" + user.getId(),
			refreshToken,
			refreshTokenExpiration,
			TimeUnit.MILLISECONDS
		);

		LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
			.id(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.company(user.getCompany())
			.build();

		return LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.user(userInfo)
			.build();
	}

	@Transactional
	public LoginResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email already exists");
		}

		Role customerRole = roleRepository.findByName("CUSTOMER")
			.orElseThrow(() -> new RuntimeException("Default role not found"));

		User user = User.builder()
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.name(request.getName())
			.phone(request.getPhone())
			.company(request.getCompany())
			.enabled(true)
			.build();

		user.getRoles().add(customerRole);
		userRepository.save(user);

		return LoginResponse.builder()
			.user(LoginResponse.UserInfo.builder()
				.id(user.getId())
				.email(user.getEmail())
				.name(user.getName())
				.company(user.getCompany())
				.build())
			.build();
	}

	@Transactional
	public LoginResponse refreshToken(String refreshToken) {
		if (!tokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("Invalid refresh token");
		}

		Authentication authentication = tokenProvider.getAuthentication(refreshToken);
		String email = authentication.getName();

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// Redis에서 저장된 refreshToken과 일치하는지 확인
		String storedToken = redisTemplate.opsForValue().get("refreshToken:" + user.getId());
		if (storedToken == null || !storedToken.equals(refreshToken)) {
			throw new IllegalArgumentException("Refresh token not found or expired");
		}

		// 새 accessToken 발급
		String newAccessToken = tokenProvider.createAccessToken(authentication);

		LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
			.id(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.company(user.getCompany())
			.build();

		return LoginResponse.builder()
			.accessToken(newAccessToken)
			.refreshToken(refreshToken)
			.user(userInfo)
			.build();
	}

	@Transactional
	public void logout(String refreshToken) {
		if (!tokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("Invalid refresh token");
		}

		Authentication authentication = tokenProvider.getAuthentication(refreshToken);
		String email = authentication.getName();

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));

		// Redis에서 refreshToken 삭제
		redisTemplate.delete("refreshToken:" + user.getId());
	}
}

