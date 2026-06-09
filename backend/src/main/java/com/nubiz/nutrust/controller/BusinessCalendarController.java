package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.BusinessCalendarCreateRequest;
import com.nubiz.nutrust.dto.BusinessCalendarResponse;
import com.nubiz.nutrust.dto.BusinessCalendarUpdateRequest;
import com.nubiz.nutrust.service.BusinessCalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system/business-calendars")
@RequiredArgsConstructor
public class BusinessCalendarController {

	private final BusinessCalendarService businessCalendarService;

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@PostMapping
	public ResponseEntity<BusinessCalendarResponse> create(
		@RequestHeader("X-Company-Id") Long companyId,
		@Valid @RequestBody BusinessCalendarCreateRequest request
	) {
		BusinessCalendarResponse response = businessCalendarService.create(companyId, request);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<BusinessCalendarResponse> update(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id,
		@Valid @RequestBody BusinessCalendarUpdateRequest request
	) {
		BusinessCalendarResponse response = businessCalendarService.update(companyId, id, request);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyAUTHORITY('ADMIN','COMPANY_ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id
	) {
		businessCalendarService.delete(companyId, id);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN','SUPPORT')")
	@GetMapping
	public ResponseEntity<List<BusinessCalendarResponse>> findAll(
		@RequestHeader("X-Company-Id") Long companyId
	) {
		List<BusinessCalendarResponse> responses = businessCalendarService.findAll(companyId);
		return ResponseEntity.ok(responses);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN','SUPPORT')")
	@GetMapping("/{id}")
	public ResponseEntity<BusinessCalendarResponse> findById(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id
	) {
		BusinessCalendarResponse response = businessCalendarService.findById(companyId, id);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@GetMapping("/default")
	public ResponseEntity<BusinessCalendarResponse> findDefault(
		@RequestHeader("X-Company-Id") Long companyId
	) {
		BusinessCalendarResponse response = businessCalendarService.findDefault(companyId);
		return ResponseEntity.ok(response);
	}
}
