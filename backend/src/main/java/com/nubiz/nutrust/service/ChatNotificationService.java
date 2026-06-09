package com.nubiz.nutrust.service;

import com.nubiz.nutrust.entity.Ticket;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ChatNotificationService {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@\\[([^\\]]+)\\]\\(user:(\\d+)\\)");
    
    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;

    public Set<Long> notifyParticipantsOnMessage(Long ticketId, Long senderId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null) return new HashSet<>();
        
        Set<Long> notifiedUserIds = new HashSet<>();
        
        User supporter = ticket.getAssignedSupporter();
        if (supporter != null && !supporter.getId().equals(senderId)) {
            notificationService.publish(
                "CHAT_MESSAGE", supporter.getId(), ticketId,
                ticket.getProject().getId(),
                senderId + "님이 " + ticket.getTitle() + "에 메시지를 보냈습니다."
            );
            notifiedUserIds.add(supporter.getId());
        }
        
        User approver = ticket.getApprover();
        if (approver != null && !approver.getId().equals(senderId) && 
            (supporter == null || !approver.getId().equals(supporter.getId()))) {
            notificationService.publish(
                "CHAT_MESSAGE", approver.getId(), ticketId,
                ticket.getProject().getId(),
                senderId + "님이 " + ticket.getTitle() + "에 메시지를 보냈습니다."
            );
            notifiedUserIds.add(approver.getId());
        }
        
        return notifiedUserIds;
    }

    public void notifyMentionedUsers(Long ticketId, Long senderId, String content, Set<Long> notifiedUserIds) {
        Matcher matcher = MENTION_PATTERN.matcher(content);
        Ticket ticket = ticketRepository.findById(ticketId).orElse(null);
        if (ticket == null || !matcher.find()) return;
        
        while (matcher.find()) {
            Long mentionedUserId = Long.parseLong(matcher.group(2));
            if (!notifiedUserIds.contains(mentionedUserId) &&
                !mentionedUserId.equals(senderId)) {
                notificationService.publish(
                    "CHAT_MESSAGE", mentionedUserId, ticketId,
                    ticket.getProject().getId(),
                    senderId + "님이 " + ticket.getTitle() + "을(를) 멘션했습니다."
                );
                notifiedUserIds.add(mentionedUserId);
            }
        }
    }
}
