package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.TicketCommentCreateRequest;
import com.nubiz.nutrust.dto.TicketCommentResponse;
import com.nubiz.nutrust.service.TicketCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_ADMIN')")
public class TicketCommentController {

    private final TicketCommentService commentService;

    @PostMapping
    public ResponseEntity<TicketCommentResponse> create(
        @Valid @RequestBody TicketCommentCreateRequest request,
        Principal principal) {
        return ResponseEntity.ok(commentService.create(request, principal));
    }

    @GetMapping
    public ResponseEntity<Page<TicketCommentResponse>> getAll(
        @PathVariable Long ticketId,
        @RequestParam(required = false) String rootOnly,
        @PageableDefault(size = 50) Pageable pageable) {
        if ("true".equalsIgnoreCase(rootOnly)) {
            return ResponseEntity.ok(commentService.getRootComments(ticketId, pageable));
        }
        return ResponseEntity.ok(commentService.getByTicket(ticketId, pageable));
    }
}
