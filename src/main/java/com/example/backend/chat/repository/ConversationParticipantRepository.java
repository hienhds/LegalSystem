package com.example.backend.chat.repository;

import com.example.backend.chat.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    
    // Find participant in a conversation
    Optional<ConversationParticipant> findByConversation_ConversationIdAndUserId(
        Long conversationId, 
        Long userId
    );
    
    // Get all participants of a conversation
    List<ConversationParticipant> findByConversation_ConversationId(Long conversationId);
    
    // Get all conversations for a user
    List<ConversationParticipant> findByUserId(Long userId);
}
