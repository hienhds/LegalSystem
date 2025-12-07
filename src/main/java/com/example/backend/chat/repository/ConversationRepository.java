package com.example.backend.chat.repository;

import com.example.backend.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    // Find conversation between two users
    @Query("SELECT c FROM Conversation c " +
           "JOIN c.participants p1 " +
           "JOIN c.participants p2 " +
           "WHERE p1.userId = :userId1 AND p2.userId = :userId2")
    Optional<Conversation> findConversationBetweenUsers(
        @Param("userId1") Long userId1, 
        @Param("userId2") Long userId2
    );
    
    // Get all conversations for a user, ordered by last message time
    @Query("SELECT DISTINCT c FROM Conversation c " +
           "JOIN c.participants p " +
           "WHERE p.userId = :userId " +
           "ORDER BY c.lastMessageAt DESC")
    List<Conversation> findUserConversations(@Param("userId") Long userId);
}
