package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.*;
import com.nubiz.nutrust.entity.TicketChatMessage;
import com.nubiz.nutrust.repository.TicketChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final TicketChatMessageRepository ticketChatMessageRepository;

    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageSend request, Long senderId) {
        TicketChatMessage message = TicketChatMessage.builder()
            .ticketId(request.ticketId())
            .userId(senderId)
            .content(request.content())
            .imageUrl(request.imageUrl())
            .isEdited(false)
            .sentAt(LocalDateTime.now())
            .build();
        
        TicketChatMessage saved = ticketChatMessageRepository.save(message);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long ticketId, LocalDateTime since) {
        List<TicketChatMessage> messages = since != null 
            ? ticketChatMessageRepository.findByTicketIdAndSentAtAfterOrderBySentAtAsc(ticketId, since, org.springframework.data.domain.PageRequest.of(0, 1000)).getContent()
            : ticketChatMessageRepository.findByTicketIdOrderBySentAtAsc(ticketId);
        
        return messages.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countUnread(Long ticketId, Long userId, LocalDateTime since) {
        return ticketChatMessageRepository.countByTicketIdAndSentAtAfter(ticketId, since);
    }

    @Transactional(readOnly = true)
    public ConversationResponse getConversation(Long ticketId) {
        List<TicketChatMessage> messages = ticketChatMessageRepository.findByTicketIdOrderBySentAtAsc(ticketId);
        if (messages.isEmpty()) {
            return null;
        }
        TicketChatMessage last = messages.get(messages.size() - 1);
        return new ConversationResponse(
            ticketId,
            null,
            last.getContent(),
            last.getSentAt(),
            0
        );
    }

    @Transactional
    public ChatMessageResponse editMessage(Long messageId, String newContent) {
        TicketChatMessage message = ticketChatMessageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Chat message not found: " + messageId));
        message.setContent(newContent);
        message.setIsEdited(true);
        return toResponse(message);
    }

    private ChatMessageResponse toResponse(TicketChatMessage message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getTicketId(),
            message.getUserId(),
            message.getContent(),
            message.getImageUrl(),
            message.getIsEdited(),
            message.getSentAt(),
            message.getCreatedAt()
        );
    }
}
