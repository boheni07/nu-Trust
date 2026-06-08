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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;

	@Transactional
	public LoginResponse login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);

		String accessToken = tokenProvider.createAccessToken(authentication);
		String refreshToken = tokenProvider.createRefreshToken(authentication);

		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new RuntimeException("User not found"));

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
			.orElseGet(() -> roleRepository.save(Role.builder()
				.name("CUSTOMER")
				.description("Default customer role")
				.build()));

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

		return login(new LoginRequest(request.getEmail(), request.getPassword()));
	}

	@Transactional(readOnly = true)
	public User getCurrentUser(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("User not found"));
	}
}
