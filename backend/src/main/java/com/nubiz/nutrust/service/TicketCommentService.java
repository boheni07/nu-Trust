package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.TicketCommentCreateRequest;
import com.nubiz.nutrust.dto.TicketCommentResponse;
import com.nubiz.nutrust.entity.Ticket;
import com.nubiz.nutrust.entity.TicketComment;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.repository.TicketCommentRepository;
import com.nubiz.nutrust.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;

    private User getCurrentUser(Principal principal) {
        String email = principal.getName();
        // TODO: Use UserRepository to fetch by email or inject AuthContext
        throw new IllegalStateException("AuthContext not implemented yet");
    }

    @Transactional
    public TicketCommentResponse create(TicketCommentCreateRequest request, Principal principal) {
        Ticket ticket = ticketRepository.findById(request.ticketId())
            .orElseThrow(() -> new IllegalArgumentException("티켓을 찾을 수 없습니다."));

        User writer = getCurrentUser(principal);

        TicketComment comment = TicketComment.builder()
            .ticket(ticket)
            .writer(writer)
            .content(request.content())
            .build();

        if (request.parentId() != null) {
            TicketComment parent = commentRepository.findById(request.parentId())
                .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다."));
            comment.setParent(parent);
        }

        comment = commentRepository.save(comment);
        return TicketCommentResponse.from(comment);
    }

    @Transactional(readOnly = true)
    public Page<TicketCommentResponse> getByTicket(Long ticketId, Pageable pageable) {
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId, pageable)
            .map(TicketCommentResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<TicketCommentResponse> getRootComments(Long ticketId, Pageable pageable) {
        return commentRepository.findByTicketIdAndParentIsNullOrderByCreatedAtAsc(ticketId, pageable)
            .map(TicketCommentResponse::from);
    }
}
