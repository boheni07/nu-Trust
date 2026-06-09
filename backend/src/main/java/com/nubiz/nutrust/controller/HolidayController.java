package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.HolidayCreateRequest;
import com.nubiz.nutrust.dto.HolidayResponse;
import com.nubiz.nutrust.dto.HolidayUpdateRequest;
import com.nubiz.nutrust.service.HolidayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system/holidays")
@RequiredArgsConstructor
public class HolidayController {

	private final HolidayService holidayService;

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@PostMapping
	public ResponseEntity<HolidayResponse> create(
		@RequestHeader("X-Company-Id") Long companyId,
		@Valid @RequestBody HolidayCreateRequest request
	) {
		HolidayResponse response = holidayService.create(companyId, request);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<HolidayResponse> update(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id,
		@Valid @RequestBody HolidayUpdateRequest request
	) {
		HolidayResponse response = holidayService.update(companyId, id, request);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id
	) {
		holidayService.delete(companyId, id);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN','SUPPORT')")
	@GetMapping
	public ResponseEntity<List<HolidayResponse>> findAll(
		@RequestHeader("X-Company-Id") Long companyId
	) {
		List<HolidayResponse> responses = holidayService.findAll(companyId);
		return ResponseEntity.ok(responses);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN','SUPPORT')")
	@GetMapping("/{id}")
	public ResponseEntity<HolidayResponse> findById(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id
	) {
		HolidayResponse response = holidayService.findById(companyId, id);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/range")
	public ResponseEntity<List<HolidayResponse>> findByDateRange(
		@RequestHeader("X-Company-Id") Long companyId,
		@RequestParam String startDate,
		@RequestParam String endDate
	) {
		List<HolidayResponse> responses = holidayService.findByDateRange(companyId, startDate, endDate);
		return ResponseEntity.ok(responses);
	}
}
