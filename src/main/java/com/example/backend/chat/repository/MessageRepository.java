package com.example.backend.chat.repository;

import com.example.backend.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Get messages for a conversation with pagination (newest first)
    Page<Message> findByConversation_ConversationIdOrderBySentAtDesc(
        Long conversationId, 
        Pageable pageable
    );
    
    // Count unread messages for a user in a conversation
    Long countByConversation_ConversationIdAndSenderIdNotAndStatus(
        Long conversationId, 
        Long userId, 
        Message.MessageStatus status
    );
}
