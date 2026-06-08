package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.ProjectSupportManager;
import com.nubiz.nutrust.entity.ProjectSupportManagerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectSupportManagerRepository extends JpaRepository<ProjectSupportManager, ProjectSupportManagerId> {
}
