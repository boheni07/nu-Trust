package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.ExtensionRequestCreate;
import com.nubiz.nutrust.dto.ExtensionRequestResponse;
import com.nubiz.nutrust.entity.ExtensionRequest;
import com.nubiz.nutrust.repository.ExtensionRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtensionRequestService {

    private final ExtensionRequestRepository extensionRequestRepository;

    @Transactional
    public ExtensionRequestResponse createExtensionRequest(Long ticketId, ExtensionRequestCreate request) {
        ExtensionRequest extensionRequest = ExtensionRequest.builder()
            .ticketId(ticketId)
            .userId(request.userId())
            .reason(request.reason())
            .requestedExtendDays(request.requestedExtendDays())
            .status("PENDING")
            .createdAt(LocalDateTime.now())
            .build();

        ExtensionRequest saved = extensionRequestRepository.save(extensionRequest);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ExtensionRequestResponse> getRequestsByTicket(Long ticketId) {
        List<ExtensionRequest> requests = extensionRequestRepository.findByTicketIdOrderByCreatedAtDesc(ticketId);
        return requests.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ExtensionRequestResponse approveExtensionRequest(Long requestId) {
        ExtensionRequest request = extensionRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Extension request not found: " + requestId));
        
        request.setStatus("APPROVED");
        request.setUpdatedAt(LocalDateTime.now());
        
        return toResponse(extensionRequestRepository.save(request));
    }

    @Transactional
    public ExtensionRequestResponse rejectExtensionRequest(Long requestId) {
        ExtensionRequest request = extensionRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Extension request not found: " + requestId));
        
        request.setStatus("REJECTED");
        request.setUpdatedAt(LocalDateTime.now());
        
        return toResponse(extensionRequestRepository.save(request));
    }

    private ExtensionRequestResponse toResponse(ExtensionRequest request) {
        return new ExtensionRequestResponse(
            request.getId(),
            request.getTicketId(),
            request.getUserId(),
            request.getReason(),
            request.getRequestedExtendDays(),
            request.getStatus(),
            request.getCreatedAt(),
            request.getUpdatedAt()
        );
    }
}
