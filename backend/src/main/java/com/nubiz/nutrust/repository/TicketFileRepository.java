package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.TicketFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketFileRepository extends JpaRepository<TicketFile, Long> {

    List<TicketFile> findByTicketIdOrderByCreatedAtDesc(Long ticketId);
}
