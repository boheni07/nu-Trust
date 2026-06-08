package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.ProcessingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessingPlanRepository extends JpaRepository<ProcessingPlan, Long> {

    List<ProcessingPlan> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
}
