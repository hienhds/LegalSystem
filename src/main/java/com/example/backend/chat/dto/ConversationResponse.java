package com.example.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    private Long conversationId;
    private Long otherUserId;
    private String otherUserName;
    private String otherUserAvatar;
    private String otherUserType; // "CITIZEN" or "LAWYER"
    private String lastMessageText;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
    private Boolean isOnline;
}
