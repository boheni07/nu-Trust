package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.ProcessingPlanCreateRequest;
import com.nubiz.nutrust.dto.ProcessingPlanResponse;
import com.nubiz.nutrust.service.ProcessingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/plans")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_COMPANY_ADMIN')")
public class ProcessingPlanController {

    private final ProcessingPlanService planService;

    @PostMapping
    public ResponseEntity<ProcessingPlanResponse> create(
        @Valid @RequestBody ProcessingPlanCreateRequest request,
        Principal principal) {
        return ResponseEntity.ok(planService.create(request, principal));
    }

    @PatchMapping("/{planId}/approve")
    public ResponseEntity<ProcessingPlanResponse> approve(@PathVariable Long planId) {
        return ResponseEntity.ok(planService.approve(planId));
    }

    @PatchMapping("/{planId}/reject")
    public ResponseEntity<ProcessingPlanResponse> reject(@PathVariable Long planId) {
        return ResponseEntity.ok(planService.reject(planId));
    }

    @GetMapping
    public ResponseEntity<List<ProcessingPlanResponse>> getByTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(planService.getByTicket(ticketId));
    }
}
