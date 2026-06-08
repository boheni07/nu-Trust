package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

	List<Company> findByStatus(String status);
	List<Company> findByNameContaining(String name);
	Optional<Company> findByBusinessNumber(String businessNumber);
}
