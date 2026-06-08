package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.CompanyCreateRequest;
import com.nubiz.nutrust.dto.CompanyResponse;
import com.nubiz.nutrust.dto.CompanyUpdateRequest;
import com.nubiz.nutrust.entity.Company;
import com.nubiz.nutrust.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

	private final CompanyRepository companyRepository;

	@Transactional
	public CompanyResponse create(CompanyCreateRequest request) {
		Company company = Company.builder()
			.name(request.getName())
			.businessNumber(request.getBusinessNumber())
			.address(request.getAddress())
			.status("ACTIVE")
			.build();

		return CompanyResponse.from(companyRepository.save(company));
	}

	@Transactional(readOnly = true)
	public List<CompanyResponse> list() {
		return companyRepository.findAll()
			.stream()
			.map(CompanyResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public CompanyResponse get(Long id) {
		Company company = companyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Company not found: " + id));
		return CompanyResponse.from(company);
	}

	@Transactional
	public CompanyResponse update(CompanyUpdateRequest request) {
		Company company = companyRepository.findById(request.getId())
			.orElseThrow(() -> new IllegalArgumentException("Company not found: " + request.getId()));

		if (request.getName() != null) company.setName(request.getName());
		if (request.getBusinessNumber() != null) company.setBusinessNumber(request.getBusinessNumber());
		if (request.getAddress() != null) company.setAddress(request.getAddress());
		if (request.getStatus() != null) company.setStatus(request.getStatus());

		return CompanyResponse.from(company);
	}

	@Transactional
	public void delete(Long id) {
		companyRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	public List<CompanyResponse> searchByName(String name) {
		return companyRepository.findByNameContaining(name)
			.stream()
			.map(CompanyResponse::from)
			.toList();
	}
}
