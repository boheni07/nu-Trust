package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.CompanyCreateRequest;
import com.nubiz.nutrust.dto.CompanyResponse;
import com.nubiz.nutrust.dto.CompanyUpdateRequest;
import com.nubiz.nutrust.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

	private final CompanyService companyService;

	@PostMapping
	public ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(companyService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<CompanyResponse>> list(
		@RequestParam(required = false) String name
	) {
		if (name != null && !name.isBlank()) {
			return ResponseEntity.ok(companyService.searchByName(name));
		}
		return ResponseEntity.ok(companyService.list());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CompanyResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(companyService.get(id));
	}

	@PutMapping
	public ResponseEntity<CompanyResponse> update(@Valid @RequestBody CompanyUpdateRequest request) {
		return ResponseEntity.ok(companyService.update(request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		companyService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
