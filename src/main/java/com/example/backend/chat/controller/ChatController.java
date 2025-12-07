package com.example.backend.chat.controller;

import com.example.backend.chat.dto.ConversationResponse;
import com.example.backend.chat.dto.MessageRequest;
import com.example.backend.chat.dto.MessageResponse;
import com.example.backend.chat.entity.Conversation;
import com.example.backend.chat.entity.ConversationParticipant;
import com.example.backend.chat.service.ConversationService;
import com.example.backend.chat.service.MessageService;
import com.example.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final MessageService messageService;
    private final ConversationService conversationService;

    /**
     * Get all conversations for current user
     */
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getUserConversations(
            @RequestParam Long userId) {
        log.info("Getting conversations for user: {}", userId);
        
        List<ConversationResponse> conversations = conversationService.getUserConversations(userId);
        
        return ResponseEntity.ok(ApiResponse.success(
            conversations,
            "Lấy danh sách cuộc trò chuyện thành công"
        ));
    }

    /**
     * Create or get conversation between two users
     */
    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<Conversation>> createConversation(
            @RequestParam Long userId1,
            @RequestParam Long userId2,
            @RequestParam String userType1,
            @RequestParam String userType2) {
        log.info("Creating conversation between {} and {}", userId1, userId2);
        
        Conversation conversation = conversationService.createOrGetConversation(
            userId1, 
            userId2,
            ConversationParticipant.UserType.valueOf(userType1),
            ConversationParticipant.UserType.valueOf(userType2)
        );
        
        return ResponseEntity.ok(ApiResponse.success(
            conversation,
            "Tạo cuộc trò chuyện thành công"
        ));
    }

    /**
     * Get messages in a conversation (with pagination)
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Getting messages for conversation: {}", conversationId);
        
        Page<MessageResponse> messages = messageService.getMessages(conversationId, page, size);
        
        return ResponseEntity.ok(ApiResponse.success(
            messages,
            "Lấy tin nhắn thành công"
        ));
    }

    /**
     * Send a message (REST API alternative to WebSocket)
     */
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
            @RequestBody MessageRequest request) {
        log.info("Sending message via REST API");
        
        MessageResponse response = messageService.sendMessage(request);
        
        return ResponseEntity.ok(ApiResponse.success(
            response,
            "Gửi tin nhắn thành công"
        ));
    }

    /**
     * Mark messages as read
     */
    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long conversationId,
            @RequestParam Long userId) {
        log.info("Marking messages as read for user {} in conversation {}", userId, conversationId);
        
        messageService.markAsRead(conversationId, userId);
        
        return ResponseEntity.ok(ApiResponse.success(
            null,
            "Đánh dấu đã đọc thành công"
        ));
    }
}
