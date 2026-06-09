package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.*;
import com.nubiz.nutrust.entity.Project;
import com.nubiz.nutrust.entity.Ticket;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.repository.ProjectRepository;
import com.nubiz.nutrust.repository.TicketRepository;
import com.nubiz.nutrust.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private static final Set<String> VALID_STATUSES = new HashSet<>(Set.of(
        "REGISTERED", "RECEIVED", "PROCESSING", "COMPLETED",
        "DELAYED", "COMPLETION_REQUESTED", "APPROVED"
    ));

    private static final Map<String, Set<String>> VALID_TRANSITIONS = Map.of(
        "REGISTERED", Set.of("RECEIVED"),
        "RECEIVED", Set.of("PROCESSING"),
        "PROCESSING", Set.of("COMPLETED", "DELAYED"),
        "DELAYED", Set.of("COMPLETION_REQUESTED"),
        "COMPLETION_REQUESTED", Set.of("APPROVED", "PROCESSING"),
        "APPROVED", Set.of("COMPLETED")
    );

    public TicketResponse create(TicketCreateRequest request) {
        Project project = projectRepository.findById(request.projectId())
            .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));

        Ticket ticket = Ticket.builder()
            .project(project)
            .title(request.title())
            .description(request.description())
            .type(request.type() != null ? request.type() : "other")
            .priority(request.priority() != null ? request.priority() : "MEDIUM")
            .build();

        if (request.assignedSupporterId() != null) {
            User supporter = userRepository.findById(request.assignedSupporterId())
                .orElseThrow(() -> new IllegalArgumentException("담당자를 찾을 수 없습니다."));
            ticket.setAssignedSupporter(supporter);
        }
        if (request.approverId() != null) {
            User approver = userRepository.findById(request.approverId())
                .orElseThrow(() -> new IllegalArgumentException("승인자를 찾을 수 없습니다."));
            ticket.setApprover(approver);
        }
        if (request.dueDate() != null) {
            ticket.setDueDate(LocalDate.parse(request.dueDate()));
        }

        ticket = ticketRepository.save(ticket);
        return TicketResponse.from(ticket);
    }

    public TicketResponse update(Long ticketId, TicketUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));

        if (request.title() != null) ticket.setTitle(request.title());
        if (request.description() != null) ticket.setDescription(request.description());
        if (request.type() != null) ticket.setType(request.type());
        if (request.priority() != null) ticket.setPriority(request.priority());

        if (request.status() != null) {
            if (!VALID_STATUSES.contains(request.status())) {
                throw new IllegalArgumentException("유효하지 않은 상태입니다: " + request.status());
            }
            String currentStatus = ticket.getStatus();
            Set<String> allowedNext = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());
            if (!allowedNext.contains(request.status())) {
                throw new IllegalArgumentException(
                    "[" + currentStatus + "] → [" + request.status() + "] 전환이 허용되지 않습니다.");
            }
            ticket.setStatus(request.status());
            ticket.setCurrentStatus(request.status());

            if ("COMPLETED".equals(request.status())) {
                ticket.setActualCompletionDate(LocalDate.now());
            }
        }

        if (request.assignedSupporterId() != null) {
            ticket.setAssignedSupporter(userRepository.findById(request.assignedSupporterId())
                .orElseThrow(() -> new IllegalArgumentException("담당자를 찾을 수 없습니다.")));
        }
        if (request.approverId() != null) {
            ticket.setApprover(userRepository.findById(request.approverId())
                .orElseThrow(() -> new IllegalArgumentException("승인자를 찾을 수 없습니다.")));
        }
        if (request.dueDate() != null) {
            ticket.setDueDate(LocalDate.parse(request.dueDate()));
        }
        if (request.clientSatisfaction() != null) {
            ticket.setClientSatisfaction(request.clientSatisfaction());
        }

        ticket = ticketRepository.save(ticket);
        return TicketResponse.from(ticket);
    }

    public void softDelete(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));
        ticket.setDeletedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    @Transactional(readOnly = true)
    public TicketResponse getById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));
        if (ticket.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 티켓입니다.");
        }
        return TicketResponse.from(ticket);
    }

    @Transactional(readOnly = true)
    public Page<TicketSummaryResponse> getByProject(Long projectId, Pageable pageable) {
        return ticketRepository.findByProjectIdAndNotDeleted(projectId, pageable)
            .map(TicketSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TicketSummaryResponse> getByProjectAndStatus(Long projectId, String status, Pageable pageable) {
        return ticketRepository.findByProjectIdAndStatusAndNotDeleted(projectId, status, pageable)
            .map(TicketSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TicketSummaryResponse> search(Long projectId, String keyword, Pageable pageable) {
        return ticketRepository.searchByProjectAndKeyword(projectId, keyword, pageable)
            .map(TicketSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public TicketCountSummaryResponse getCountByStatus(Long projectId) {
        java.util.List<Object[]> rows = ticketRepository.countByProjectIdAndStatus(projectId);
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] row : rows) {
            statusCounts.put((String) row[0], (Long) row[1]);
        }
        return new TicketCountSummaryResponse(statusCounts);
    }

    // WBS 407: Assign supporter to ticket
    public TicketResponse assignSupporter(Long ticketId, Long supportId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));
        if (ticket.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 티켓입니다.");
        }
        User support = userRepository.findById(supportId)
            .orElseThrow(() -> new IllegalArgumentException("대기중인 지지자를 찾을 수 없습니다."));
        ticket.setAssignedSupporter(support);
        ticket = ticketRepository.save(ticket);
        return TicketResponse.from(ticket);
    }

    // WBS 408: Update ticket progress
    public TicketResponse updateProgress(Long ticketId, Integer progress) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));
        if (ticket.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 티켓입니다.");
        }
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("진행률은 0-100 사이여야 합니다.");
        }
        ticket.setProgress(progress);
        ticket = ticketRepository.save(ticket);
        return TicketResponse.from(ticket);
    }

    // WBS 409: Complete ticket (approve/reject)
    public TicketResponse completeTicket(Long ticketId, Boolean approve) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));
        if (ticket.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 티켓입니다.");
        }
        if (!"COMPLETION_REQUESTED".equals(ticket.getCurrentStatus())) {
            throw new IllegalArgumentException("완료 요청 상태가 아닙니다.");
        }
        if (approve) {
            ticket.setStatus("COMPLETED");
            ticket.setCurrentStatus("COMPLETED");
            ticket.setActualCompletionDate(LocalDate.now());
        } else {
            ticket.setStatus("PROCESSING");
            ticket.setCurrentStatus("PROCESSING");
        }
        ticket = ticketRepository.save(ticket);
        return TicketResponse.from(ticket);
    }

    // WBS 410: Delay ticket
    public TicketResponse delayTicket(Long ticketId, String reason) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));
        if (ticket.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 티켓입니다.");
        }
        if (!"PROCESSING".equals(ticket.getCurrentStatus())) {
            throw new IllegalArgumentException("진행중 상태만 지연 처리할 수 있습니다.");
        }
        ticket.setStatus("DELAYED");
        ticket.setCurrentStatus("DELAYED");
        if (reason != null && !reason.isBlank()) {
            ticket.setDescription(ticket.getDescription() + "\n\n[지연 사유] " + reason);
        }
        ticket = ticketRepository.save(ticket);
        return TicketResponse.from(ticket);
    }
}
