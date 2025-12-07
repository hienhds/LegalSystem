package com.example.backend.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "messages",
    indexes = @Index(name = "idx_conversation_time", columnList = "conversation_id, sent_at DESC")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    @Column(name = "file_url", length = 500)
    private String fileUrl;
    
    @Column(name = "file_name")
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatus status = MessageStatus.SENT;
    
    public enum SenderType {
        CITIZEN,
        LAWYER
    }
    
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE
    }
    
    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ
    }
    
    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
        if (status == null) {
            status = MessageStatus.SENT;
        }
        if (messageType == null) {
            messageType = MessageType.TEXT;
        }
    }
}
