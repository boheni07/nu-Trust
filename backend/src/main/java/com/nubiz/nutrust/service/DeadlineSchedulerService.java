package com.nubiz.nutrust.service;

import com.nubiz.nutrust.entity.Ticket;
import com.nubiz.nutrust.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeadlineSchedulerService {

    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void checkOverdueTickets() {
        LocalDate today = LocalDate.now();
        List<Ticket> overdue = ticketRepository.findByDueDateBeforeAndStatusNotIn(today, List.of("DONE", "CLOSED"));
        
        for (Ticket ticket : overdue) {
            try {
                if (ticket.getAssignedSupporter() != null) {
                    notificationService.publish(
                        "OVERDUE_TICKET",
                        ticket.getAssignedSupporter().getId(),
                        ticket.getId(),
                        ticket.getProject().getId(),
                        String.format("마감일 경과: %s", ticket.getTitle())
                    );
                }
                if (ticket.getApprover() != null && 
                    (ticket.getAssignedSupporter() == null || !ticket.getAssignedSupporter().getId().equals(ticket.getApprover().getId()))) {
                    notificationService.publish(
                        "OVERDUE_TICKET",
                        ticket.getApprover().getId(),
                        ticket.getId(),
                        ticket.getProject().getId(),
                        String.format("마감일 경과 (승인자): %s", ticket.getTitle())
                    );
                }
            } catch (Exception e) {
                // Ignored per constraints — no logging
            }
        }
    }
}
