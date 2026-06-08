package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.ProjectManager;
import com.nubiz.nutrust.entity.ProjectManagerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectManagerRepository extends JpaRepository<ProjectManager, ProjectManagerId> {
}
