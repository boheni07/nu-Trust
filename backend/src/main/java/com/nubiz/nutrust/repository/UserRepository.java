package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<User> findByCompanyId(Long companyId);

	boolean existsByCompanyId(Long companyId);

	Page<User> findByCompanyId(Long companyId, Pageable pageable);

	Page<User> findByCompanyIdAndNameContaining(Long companyId, String name, Pageable pageable);

	Page<User> findByCompanyIdAndEmailContaining(Long companyId, String email, Pageable pageable);

	Page<User> findAll(Pageable pageable);
}
