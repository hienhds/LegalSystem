package com.example.backend.chat.service;

import com.example.backend.chat.dto.ConversationResponse;
import com.example.backend.chat.entity.Conversation;
import com.example.backend.chat.entity.ConversationParticipant;
import com.example.backend.chat.repository.ConversationParticipantRepository;
import com.example.backend.chat.repository.ConversationRepository;
import com.example.backend.lawyer.repository.LawyerRepository;
import com.example.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final LawyerRepository lawyerRepository;

    /**
     * Create or get existing conversation between two users
     */
    public Conversation createOrGetConversation(Long userId1, Long userId2, 
                                               ConversationParticipant.UserType type1,
                                               ConversationParticipant.UserType type2) {
        log.info("Creating/getting conversation between user {} and {}", userId1, userId2);
        
        // Check if conversation already exists
        var existingConversation = conversationRepository
                .findConversationBetweenUsers(userId1, userId2);
        
        if (existingConversation.isPresent()) {
            log.info("Conversation already exists: {}", existingConversation.get().getConversationId());
            return existingConversation.get();
        }
        
        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setCreatedAt(LocalDateTime.now());
        conversation = conversationRepository.save(conversation);
        
        // Add participants
        ConversationParticipant participant1 = new ConversationParticipant();
        participant1.setConversation(conversation);
        participant1.setUserId(userId1);
        participant1.setUserType(type1);
        participant1.setUnreadCount(0);
        participantRepository.save(participant1);
        
        ConversationParticipant participant2 = new ConversationParticipant();
        participant2.setConversation(conversation);
        participant2.setUserId(userId2);
        participant2.setUserType(type2);
        participant2.setUnreadCount(0);
        participantRepository.save(participant2);
        
        log.info("New conversation created: {}", conversation.getConversationId());
        return conversation;
    }

    /**
     * Get all conversations for a user
     */
    @Transactional(readOnly = true)
    public List<ConversationResponse> getUserConversations(Long userId) {
        log.info("Getting conversations for user: {}", userId);
        
        List<ConversationParticipant> participants = participantRepository.findByUserId(userId);
        List<ConversationResponse> responses = new ArrayList<>();
        
        for (ConversationParticipant participant : participants) {
            Conversation conversation = participant.getConversation();
            
            // Find the other participant
            List<ConversationParticipant> allParticipants = participantRepository
                    .findByConversation_ConversationId(conversation.getConversationId());
            
            ConversationParticipant otherParticipant = allParticipants.stream()
                    .filter(p -> !p.getUserId().equals(userId))
                    .findFirst()
                    .orElse(null);
            
            if (otherParticipant != null) {
                ConversationResponse response = new ConversationResponse();
                response.setConversationId(conversation.getConversationId());
                response.setOtherUserId(otherParticipant.getUserId());
                response.setOtherUserType(otherParticipant.getUserType().name());
                response.setLastMessageText(conversation.getLastMessageText());
                response.setLastMessageAt(conversation.getLastMessageAt());
                response.setUnreadCount(participant.getUnreadCount());
                response.setIsOnline(false); // TODO: Implement online status tracking
                
                // Get other user's name and avatar
                if (otherParticipant.getUserType() == ConversationParticipant.UserType.LAWYER) {
                    lawyerRepository.findById(otherParticipant.getUserId()).ifPresent(lawyer -> {
                        response.setOtherUserName(lawyer.getUser().getFullName());
                        response.setOtherUserAvatar(lawyer.getUser().getAvatarUrl());
                    });
                } else {
                    userRepository.findById(otherParticipant.getUserId()).ifPresent(user -> {
                        response.setOtherUserName(user.getFullName());
                        response.setOtherUserAvatar(user.getAvatarUrl());
                    });
                }
                
                responses.add(response);
            }
        }
        
        // Sort by last message time (newest first)
        responses.sort((a, b) -> {
            if (a.getLastMessageAt() == null) return 1;
            if (b.getLastMessageAt() == null) return -1;
            return b.getLastMessageAt().compareTo(a.getLastMessageAt());
        });
        
        log.info("Found {} conversations for user {}", responses.size(), userId);
        return responses;
    }
}
