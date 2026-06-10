package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.*;
import com.nubiz.nutrust.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_COMPANY_ADMIN')")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketCreateRequest request) {
        return ResponseEntity.ok(ticketService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody TicketUpdateRequest request) {
        return ResponseEntity.ok(ticketService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        ticketService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getById(id));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<TicketSummaryResponse>> getByProject(
        @PathVariable Long projectId,
        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getByProject(projectId, pageable));
    }

    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<Page<TicketSummaryResponse>> getByProjectAndStatus(
        @PathVariable Long projectId,
        @PathVariable String status,
        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getByProjectAndStatus(projectId, status, pageable));
    }

    @GetMapping("/project/{projectId}/search")
    public ResponseEntity<Page<TicketSummaryResponse>> search(
        @PathVariable Long projectId,
        @RequestParam String keyword,
        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ticketService.search(projectId, keyword, pageable));
    }

    @GetMapping("/project/{projectId}/status-counts")
    public ResponseEntity<TicketCountSummaryResponse> getCountByStatus(@PathVariable Long projectId) {
        return ResponseEntity.ok(ticketService.getCountByStatus(projectId));
    }

    // WBS 407
    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignSupporter(
        @PathVariable Long id,
        @RequestParam Long supportId) {
        return ResponseEntity.ok(ticketService.assignSupporter(id, supportId));
    }

    // WBS 408
    @PatchMapping("/{id}/progress")
    public ResponseEntity<TicketResponse> updateProgress(
        @PathVariable Long id,
        @RequestParam Integer progress) {
        return ResponseEntity.ok(ticketService.updateProgress(id, progress));
    }

    // WBS 409
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TicketResponse> completeTicket(
        @PathVariable Long id,
        @RequestParam Boolean approve) {
        return ResponseEntity.ok(ticketService.completeTicket(id, approve));
    }

    // WBS 410
    @PatchMapping("/{id}/delayed")
    public ResponseEntity<TicketResponse> delayTicket(
        @PathVariable Long id,
        @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ticketService.delayTicket(id, reason));
    }
}
