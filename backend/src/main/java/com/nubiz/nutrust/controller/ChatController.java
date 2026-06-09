package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.ChatMessageResponse;
import com.nubiz.nutrust.dto.ChatMessageSend;
import com.nubiz.nutrust.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long ticketId,
            @Valid @RequestBody ChatMessageSend request) {
        
        ChatMessageResponse response = chatService.sendMessage(request, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long ticketId,
            @RequestParam(required = false) LocalDateTime since) {
        List<ChatMessageResponse> messages = chatService.getMessages(ticketId, since);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/messages/{messageId}")
    public ResponseEntity<ChatMessageResponse> editMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody ChatMessageSend request) {
        
        ChatMessageResponse response = chatService.editMessage(messageId, request.content());
        return ResponseEntity.ok(response);
    }

    @MessageMapping("/chat.{ticketId}")
    public void sendMessageViaWebSocket(@DestinationVariable Long ticketId, ChatMessageSend request) {
        chatService.sendMessage(request, getCurrentUserId());
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
