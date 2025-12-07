package com.example.backend.chat.service;

import com.example.backend.chat.dto.MessageRequest;
import com.example.backend.chat.dto.MessageResponse;
import com.example.backend.chat.entity.Conversation;
import com.example.backend.chat.entity.ConversationParticipant;
import com.example.backend.chat.entity.Message;
import com.example.backend.chat.repository.ConversationParticipantRepository;
import com.example.backend.chat.repository.ConversationRepository;
import com.example.backend.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send a message in a conversation
     */
    public MessageResponse sendMessage(MessageRequest request) {
        log.info("Sending message in conversation: {}", request.getConversationId());
        
        // Get conversation
        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Create message
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(request.getSenderId());
        message.setSenderType(Message.SenderType.valueOf(request.getSenderType()));
        message.setContent(request.getContent());
        message.setMessageType(Message.MessageType.valueOf(request.getMessageType()));
        message.setFileUrl(request.getFileUrl());
        message.setFileName(request.getFileName());
        message.setFileSize(request.getFileSize());
        
        // Save message
        message = messageRepository.save(message);
        
        // Update conversation last message
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastMessageText(request.getContent());
        conversationRepository.save(conversation);
        
        // Update unread count for other participants
        List<ConversationParticipant> participants = participantRepository
                .findByConversation_ConversationId(conversation.getConversationId());
        
        for (ConversationParticipant participant : participants) {
            if (!participant.getUserId().equals(request.getSenderId())) {
                participant.setUnreadCount(participant.getUnreadCount() + 1);
                participantRepository.save(participant);
            }
        }
        
        // Convert to response
        MessageResponse response = toMessageResponse(message);
        
        // Broadcast via WebSocket
        messagingTemplate.convertAndSend(
            "/topic/conversations/" + conversation.getConversationId(),
            response
        );
        
        log.info("Message sent successfully: {}", message.getMessageId());
        return response;
    }

    /**
     * Get messages for a conversation with pagination
     */
    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(Long conversationId, int page, int size) {
        log.info("Getting messages for conversation: {}", conversationId);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageRepository
                .findByConversation_ConversationIdOrderBySentAtDesc(conversationId, pageable);
        
        return messages.map(this::toMessageResponse);
    }

    /**
     * Mark messages as read
     */
    public void markAsRead(Long conversationId, Long userId) {
        log.info("Marking messages as read for user {} in conversation {}", userId, conversationId);
        
        // Update participant's unread count
        ConversationParticipant participant = participantRepository
                .findByConversation_ConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));
        
        participant.setUnreadCount(0);
        participantRepository.save(participant);
        
        // Notify other participants via WebSocket
        messagingTemplate.convertAndSend(
            "/topic/conversations/" + conversationId + "/read",
            userId
        );
    }

    /**
     * Convert Message entity to MessageResponse DTO
     */
    private MessageResponse toMessageResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setMessageId(message.getMessageId());
        response.setConversationId(message.getConversation().getConversationId());
        response.setSenderId(message.getSenderId());
        response.setSenderType(message.getSenderType().name());
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType().name());
        response.setFileUrl(message.getFileUrl());
        response.setFileName(message.getFileName());
        response.setFileSize(message.getFileSize());
        response.setSentAt(message.getSentAt());
        response.setStatus(message.getStatus().name());
        return response;
    }
}
