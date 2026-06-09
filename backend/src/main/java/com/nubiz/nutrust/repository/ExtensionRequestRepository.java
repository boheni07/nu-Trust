package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.ExtensionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtensionRequestRepository extends JpaRepository<ExtensionRequest, Long> {
    List<ExtensionRequest> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
}
