package com.example.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private String senderType;
    private String content;
    private String messageType;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private LocalDateTime sentAt;
    private String status;
}
