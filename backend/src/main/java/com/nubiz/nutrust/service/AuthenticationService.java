package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.LoginRequest;
import com.nubiz.nutrust.dto.LoginResponse;
import com.nubiz.nutrust.dto.RegisterRequest;
import com.nubiz.nutrust.entity.Role;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.entity.UserStatus;
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
			.companyId(user.getCompanyId())
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
			throw new RuntimeException("Email already in use");
		}

		User user = User.builder()
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.name(request.getName())
			.phone(request.getPhone())
			.companyId(request.getCompanyId())
			.status(UserStatus.ACTIVE)
			.build();

		Role customerRole = roleRepository.findByName("CUSTOMER")
			.orElseThrow(() -> new RuntimeException("Customer role not found"));
		user.getRoles().add(customerRole);

		userRepository.save(user);

		LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
			.id(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.companyId(user.getCompanyId())
			.build();

		return LoginResponse.builder()
			.accessToken("")
			.refreshToken("")
			.user(userInfo)
			.build();
	}

	public LoginResponse refreshToken(String refreshToken) {
		if (!tokenProvider.validateToken(refreshToken)) {
			throw new RuntimeException("Invalid refresh token");
		}

		Authentication authentication = tokenProvider.getAuthentication(refreshToken);
		String email = authentication.getName();

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));

		String storedToken = redisTemplate.opsForValue().get("refreshToken:" + user.getId());
		if (storedToken == null || !storedToken.equals(refreshToken)) {
			throw new RuntimeException("Refresh token not found in Redis");
		}

		String newAccessToken = tokenProvider.createAccessToken(authentication);
		String newRefreshToken = tokenProvider.createRefreshToken(authentication);

		redisTemplate.opsForValue().set(
			"refreshToken:" + user.getId(),
			newRefreshToken,
			refreshTokenExpiration,
			TimeUnit.MILLISECONDS
		);

		LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
			.id(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.companyId(user.getCompanyId())
			.build();

		return LoginResponse.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.user(userInfo)
			.build();
	}

	public void logout(String refreshToken) {
		if (!tokenProvider.validateToken(refreshToken)) {
			throw new RuntimeException("Invalid refresh token");
		}

		Authentication authentication = tokenProvider.getAuthentication(refreshToken);
		String email = authentication.getName();

		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));

		redisTemplate.delete("refreshToken:" + user.getId());
	}
}
