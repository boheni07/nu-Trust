package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.LoginRequest;
import com.nubiz.nutrust.dto.LoginResponse;
import com.nubiz.nutrust.dto.LogoutRequest;
import com.nubiz.nutrust.dto.RefreshTokenRequest;
import com.nubiz.nutrust.dto.RegisterRequest;
import com.nubiz.nutrust.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		return ResponseEntity.ok(authenticationService.login(request));
	}

	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
		return ResponseEntity.ok(authenticationService.refreshToken(request.getRefreshToken()));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
		authenticationService.logout(request.getRefreshToken(), request.getAccessToken());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/register")
	public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(authenticationService.register(request));
	}
}
