package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.NoticeCreateRequest;
import com.nubiz.nutrust.dto.NoticeResponse;
import com.nubiz.nutrust.dto.NoticeUpdateRequest;
import com.nubiz.nutrust.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

	private final NoticeService noticeService;

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@PostMapping
	public ResponseEntity<NoticeResponse> create(
		@RequestHeader("X-Company-Id") Long companyId,
		@RequestHeader(value = "X-User-Id", required = false) Long userId,
		@Valid @RequestBody NoticeCreateRequest request
	) {
		NoticeResponse response = noticeService.create(companyId, userId, request);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<NoticeResponse> update(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id,
		@Valid @RequestBody NoticeUpdateRequest request
	) {
		NoticeResponse response = noticeService.update(companyId, id, request);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id
	) {
		noticeService.delete(companyId, id);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN','SUPPORT','CUSTOMER)")
	@GetMapping
	public ResponseEntity<List<NoticeResponse>> findAll(
		@RequestHeader("X-Company-Id") Long companyId
	) {
		List<NoticeResponse> responses = noticeService.findAll(companyId);
		return ResponseEntity.ok(responses);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN','SUPPORT','CUSTOMER)")
	@GetMapping("/{id}")
	public ResponseEntity<NoticeResponse> findById(
		@RequestHeader("X-Company-Id") Long companyId,
		@PathVariable Long id
	) {
		NoticeResponse response = noticeService.findById(companyId, id);
		return ResponseEntity.ok(response);
	}
}
