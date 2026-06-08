package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.UserCreateRequest;
import com.nubiz.nutrust.dto.UserResponse;
import com.nubiz.nutrust.dto.UserUpdateRequest;
import com.nubiz.nutrust.entity.Role;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.entity.UserStatus;
import com.nubiz.nutrust.repository.RoleRepository;
import com.nubiz.nutrust.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new RuntimeException("Current user not found"));
	}

	@Transactional
	public UserResponse create(UserCreateRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already in use");
		}

		User user = User.builder()
			.email(request.getEmail())
			.password(passwordEncoder.encode(request.getPassword()))
			.name(request.getName())
			.phone(request.getPhone())
			.companyId(request.getCompanyId())
			.status(UserStatus.ACTIVE)
			.build();

		Role role = roleRepository.findByName(request.getRole())
			.orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));
		user.getRoles().add(role);

		return UserResponse.from(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public List<UserResponse> list(String nameFilter) {
		User adminUser = getCurrentUser();
		Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<User> users = resolveScopeQuery(adminUser, nameFilter).query(pageable);
		
		return users.getContent().stream()
			.map(UserResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public UserResponse getById(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found: " + id));
		return UserResponse.from(user);
	}

	@Transactional
	public UserResponse update(Long id, UserUpdateRequest request) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found: " + id));

		if (request.getEmail() != null) user.setEmail(request.getEmail());
		if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
		if (request.getName() != null) user.setName(request.getName());
		if (request.getPhone() != null) user.setPhone(request.getPhone());
		if (request.getStatus() != null) user.setStatus(UserStatus.valueOf(request.getStatus()));

		return UserResponse.from(userRepository.save(user));
	}

	@Transactional
	public void delete(Long id) {
		if (!userRepository.existsById(id)) {
			throw new RuntimeException("User not found: " + id);
		}
		userRepository.deleteById(id);
	}

	@Transactional
	public UserResponse setStatus(Long id, UserStatus status) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("User not found: " + id));
		user.setStatus(status);
		return UserResponse.from(userRepository.save(user));
	}

	private ScopeQuery resolveScopeQuery(User adminUser, String nameFilter) {
		boolean isAdmin = adminUser.getRoles().stream()
			.anyMatch(r -> "ADMIN".equals(r.getName()));

		if (isAdmin) {
			return pageable -> nameFilter != null && !nameFilter.isBlank()
				? userRepository.findAll(pageable)
				: userRepository.findAll(pageable);
		}

		Long companyId = adminUser.getCompanyId();
		if (nameFilter != null && !nameFilter.isBlank()) {
			return pageable -> userRepository.findByCompanyIdAndNameContaining(companyId, nameFilter, pageable);
		}
		return pageable -> userRepository.findByCompanyId(companyId, pageable);
	}

	@FunctionalInterface
	private interface ScopeQuery {
		Page<User> query(Pageable pageable);
	}
}
