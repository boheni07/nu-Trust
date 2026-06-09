package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.ExtensionRequestCreate;
import com.nubiz.nutrust.dto.ExtensionRequestResponse;
import com.nubiz.nutrust.service.ExtensionRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/extensions")
@RequiredArgsConstructor
public class ExtensionRequestController {

    private final ExtensionRequestService extensionRequestService;

    @PostMapping
    public ResponseEntity<ExtensionRequestResponse> create(
            @PathVariable Long ticketId,
            @Valid @RequestBody ExtensionRequestCreate request) {
        
        ExtensionRequestResponse response = extensionRequestService.createExtensionRequest(
            ticketId,
            request
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ExtensionRequestResponse>> getRequests(
            @PathVariable Long ticketId) {
        List<ExtensionRequestResponse> responses = extensionRequestService.getRequestsByTicket(ticketId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<ExtensionRequestResponse> approve(
            @PathVariable Long requestId) {
        ExtensionRequestResponse response = extensionRequestService.approveExtensionRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ExtensionRequestResponse> reject(
            @PathVariable Long requestId) {
        ExtensionRequestResponse response = extensionRequestService.rejectExtensionRequest(requestId);
        return ResponseEntity.ok(response);
    }
}
