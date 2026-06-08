package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.UserCreateRequest;
import com.nubiz.nutrust.entity.UserStatus;
import com.nubiz.nutrust.dto.UserResponse;
import com.nubiz.nutrust.dto.UserUpdateRequest;
import com.nubiz.nutrust.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(userService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> list(
		@RequestParam(required = false) String name
	) {
		return ResponseEntity.ok(userService.list(name));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getById(id));
	}

	@PutMapping
	public ResponseEntity<UserResponse> update(@Valid @RequestBody UserUpdateRequest request) {
		return ResponseEntity.ok(userService.update(request.getId(), request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<UserResponse> setStatus(
		@PathVariable Long id,
		@RequestParam UserStatus status
	) {
		return ResponseEntity.ok(userService.setStatus(id, status));
	}
}
