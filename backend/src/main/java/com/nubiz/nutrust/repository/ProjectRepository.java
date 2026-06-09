package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByCompanyIdAndDeletedAtIsNull(Long companyId);

    List<Project> findByDeletedAtIsNull();

    Optional<Project> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT p FROM Project p WHERE p.deletedAt IS NULL AND " +
           "(p.companyId = :companyId OR p.customerCompanyId = :companyId)")
    List<Project> findByCompanyIdOrCustomerCompanyId(Long companyId);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = :status AND p.deletedAt IS NULL")
    Long countByStatus(@Param("status") String status);
}
