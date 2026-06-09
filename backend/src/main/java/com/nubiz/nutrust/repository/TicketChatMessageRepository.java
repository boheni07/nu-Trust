package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.TicketChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketChatMessageRepository extends JpaRepository<TicketChatMessage, Long> {

    List<TicketChatMessage> findByTicketIdOrderBySentAtAsc(Long ticketId);

    Page<TicketChatMessage> findByTicketIdOrderBySentAtAsc(Long ticketId, Pageable pageable);

    Page<TicketChatMessage> findByTicketIdAndSentAtAfterOrderBySentAtAsc(Long ticketId, LocalDateTime from, Pageable pageable);

    Long countByTicketId(Long ticketId);

    Long countByTicketIdAndSentAtAfter(Long ticketId, LocalDateTime since);

    List<TicketChatMessage> findByTicketIdAndUserIdOrderBySentAtAsc(Long ticketId, Long userId);
}
