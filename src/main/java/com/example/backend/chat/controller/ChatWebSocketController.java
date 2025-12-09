package com.example.backend.chat.controller;

import com.example.backend.chat.dto.MessageRequest;
import com.example.backend.chat.dto.MessageResponse;
import com.example.backend.chat.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final MessageService messageService;

    /**
     * Handle incoming messages from WebSocket clients
     * Client sends to: /app/chat.sendMessage
     * Server broadcasts to: /topic/conversations/{conversationId}
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest request) {
        log.info("Received WebSocket message from user: {}", request.getSenderId());
        messageService.sendMessage(request);
    }

    /**
     * Handle typing indicator
     * Client sends to: /app/chat.typing
     * Server broadcasts to: /topic/conversations/{conversationId}/typing
     */
    @MessageMapping("/chat.typing")
    public void userTyping(@Payload MessageRequest request) {
        log.debug("User {} is typing in conversation {}", request.getSenderId(), request.getConversationId());
        // The broadcast is handled by the client subscription
    }
}
