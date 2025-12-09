package com.example.backend.chat.dto;

import com.example.backend.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    private Long conversationId;
    private Long senderId;
    private String senderType; // "CITIZEN" or "LAWYER"
    private String content;
    private String messageType; // "TEXT", "IMAGE", "FILE"
    private String fileUrl;
    private String fileName;
    private Long fileSize;
}
