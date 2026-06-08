package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.CompanyCreateRequest;
import com.nubiz.nutrust.dto.CompanyResponse;
import com.nubiz.nutrust.dto.CompanyUpdateRequest;
import com.nubiz.nutrust.entity.Company;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.repository.CompanyRepository;
import com.nubiz.nutrust.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

	private final CompanyRepository companyRepository;
	private final UserRepository userRepository;

	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("Current user not found"));
	}

	@PreAuthorize("hasAuthority('ADMIN')")
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

	@PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
	@Transactional(readOnly = true)
	public List<CompanyResponse> list() {
		User adminUser = getCurrentUser();
		boolean isAdmin = adminUser.getRoles().stream()
			.anyMatch(r -> "ADMIN".equals(r.getName()));
		if (isAdmin) {
			return companyRepository.findAll()
				.stream()
				.map(CompanyResponse::from)
				.toList();
		}
		Long companyId = adminUser.getCompanyId();
		Company myCompany = companyRepository.findById(companyId)
			.orElseThrow(() -> new RuntimeException("Company not found: " + companyId));
		return List.of(CompanyResponse.from(myCompany));
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
	@Transactional(readOnly = true)
	public CompanyResponse get(Long id) {
		User adminUser = getCurrentUser();
		boolean isAdmin = adminUser.getRoles().stream()
			.anyMatch(r -> "ADMIN".equals(r.getName()));
		if (isAdmin) {
			Company company = companyRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Company not found: " + id));
			return CompanyResponse.from(company);
		}
		Long companyId = adminUser.getCompanyId();
		if (!companyId.equals(id)) {
			throw new IllegalArgumentException("You can only view your own company");
		}
		Company company = companyRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Company not found: " + id));
		return CompanyResponse.from(company);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
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

	@PreAuthorize("hasAuthority('ADMIN')")
	@Transactional
	public void delete(Long id) {
		companyRepository.deleteById(id);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
	@Transactional(readOnly = true)
	public List<CompanyResponse> searchByName(String name) {
		User adminUser = getCurrentUser();
		boolean isAdmin = adminUser.getRoles().stream()
			.anyMatch(r -> "ADMIN".equals(r.getName()));
		if (isAdmin) {
			return companyRepository.findByNameContaining(name)
				.stream()
				.map(CompanyResponse::from)
				.toList();
		}
		Long companyId = adminUser.getCompanyId();
		return companyRepository.findById(companyId)
			.filter(c -> c.getName().contains(name))
			.<List<CompanyResponse>>map(c -> List.of(CompanyResponse.from(c)))
			.orElse(List.of());
	}
}
