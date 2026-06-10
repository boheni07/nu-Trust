package com.nubiz.nutrust.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", e.getMessage());
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
		Map<String, String> error = new HashMap<>();
		String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
		if (msg.contains("already") || msg.contains("duplicate")) {
			error.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
		}
		error.put("error", "Internal server error");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "Invalid credentials");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "Authentication failed");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "Access denied");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getFieldErrors().forEach(fieldError ->
			errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> handleDataIntegrity(DataIntegrityViolationException e) {
		Map<String, String> error = new HashMap<>();
		String msg = e.getMessage();
		if (msg != null && msg.toLowerCase().contains("duplicate")) {
			error.put("error", "Already exists");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
		}
		error.put("error", "Data integrity violation");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "Internal server error");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
