package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.ProcessingPlanCreateRequest;
import com.nubiz.nutrust.dto.ProcessingPlanResponse;
import com.nubiz.nutrust.entity.ProcessingPlan;
import com.nubiz.nutrust.entity.Ticket;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.repository.ProcessingPlanRepository;
import com.nubiz.nutrust.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessingPlanService {

    private final ProcessingPlanRepository planRepository;
    private final TicketRepository ticketRepository;

    private User getCurrentUser(Principal principal) {
        String email = principal.getName();
        // TODO: Use UserRepository to fetch by email or inject AuthContext
        throw new IllegalStateException("AuthContext not implemented yet");
    }

    public ProcessingPlanResponse create(ProcessingPlanCreateRequest request, Principal principal) {
        Ticket ticket = ticketRepository.findById(request.ticketId())
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));

        User writer = getCurrentUser(principal);

        ProcessingPlan plan = ProcessingPlan.builder()
            .ticket(ticket)
            .writer(writer)
            .title(request.title())
            .content(request.content())
            .build();

        plan = planRepository.save(plan);
        return ProcessingPlanResponse.from(plan);
    }

    public ProcessingPlanResponse approve(Long planId) {
        ProcessingPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("처리계획을 찾을 수 없습니다."));
        plan.setStatus("APPROVED");
        plan = planRepository.save(plan);
        return ProcessingPlanResponse.from(plan);
    }

    public ProcessingPlanResponse reject(Long planId) {
        ProcessingPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("처리계획을 찾을 수 없습니다."));
        plan.setStatus("REJECTED");
        plan = planRepository.save(plan);
        return ProcessingPlanResponse.from(plan);
    }

    @Transactional(readOnly = true)
    public List<ProcessingPlanResponse> getByTicket(Long ticketId) {
        return planRepository.findByTicketIdOrderByCreatedAtDesc(ticketId)
            .stream()
            .map(ProcessingPlanResponse::from)
            .toList();
    }
}
