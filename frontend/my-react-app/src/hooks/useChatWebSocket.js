import { useEffect, useRef, useCallback } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export const useChatWebSocket = (conversationId, onMessageReceived) => {
  const clientRef = useRef(null);
  const isConnectedRef = useRef(false);
  const subscriptionRef = useRef(null);
  const onMessageReceivedRef = useRef(onMessageReceived);

  // Keep callback ref updated
  useEffect(() => {
    onMessageReceivedRef.current = onMessageReceived;
  }, [onMessageReceived]);

  const connect = useCallback(() => {
    if (clientRef.current) {
      console.log('WebSocket already exists, skipping connect');
      return;
    }

    console.log('🔌 Connecting to WebSocket...');
    const socket = new SockJS('http://localhost:8080/chat');
    const stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => {
        console.log('STOMP:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('✅ Connected to WebSocket');
        isConnectedRef.current = true;
      },
      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame);
        isConnectedRef.current = false;
      },
      onWebSocketClose: () => {
        console.log('🔌 WebSocket connection closed');
        isConnectedRef.current = false;
      }
    });

    stompClient.activate();
    clientRef.current = stompClient;
  }, []);

  // Subscribe to conversation when conversationId changes
  useEffect(() => {
    if (!conversationId || !clientRef.current) {
      console.log('Skip subscription: conversationId or client not ready');
      return;
    }

    // Unsubscribe previous conversation
    if (subscriptionRef.current) {
      console.log('Unsubscribing from previous conversation');
      subscriptionRef.current.unsubscribe();
      subscriptionRef.current = null;
    }

    // Wait for connection with timeout
    let attempts = 0;
    const maxAttempts = 50; // 5 seconds max
    
    const subscribeWhenReady = () => {
      attempts++;
      
      if (isConnectedRef.current && clientRef.current && clientRef.current.connected) {
        console.log(`📡 Subscribing to /topic/conversations/${conversationId}`);
        try {
          subscriptionRef.current = clientRef.current.subscribe(
            `/topic/conversations/${conversationId}`,
            (message) => {
              try {
                const receivedMessage = JSON.parse(message.body);
                console.log('📨 WebSocket received message:', receivedMessage);
                if (onMessageReceivedRef.current) {
                  onMessageReceivedRef.current(receivedMessage);
                }
              } catch (error) {
                console.error('Error parsing message:', error);
              }
            }
          );
          console.log('✅ Successfully subscribed to conversation');
        } catch (error) {
          console.error('❌ Error subscribing:', error);
        }
      } else if (attempts < maxAttempts) {
        console.log(`Waiting for connection... (attempt ${attempts}/${maxAttempts})`);
        setTimeout(subscribeWhenReady, 100);
      } else {
        console.error('❌ Failed to connect after maximum attempts');
      }
    };

    // Start subscription attempt after a small delay to let connection establish
    const timeoutId = setTimeout(subscribeWhenReady, 200);

    return () => {
      clearTimeout(timeoutId);
      if (subscriptionRef.current) {
        subscriptionRef.current.unsubscribe();
        subscriptionRef.current = null;
      }
    };
  }, [conversationId]);

  const disconnect = useCallback(() => {
    if (subscriptionRef.current) {
      subscriptionRef.current.unsubscribe();
      subscriptionRef.current = null;
    }
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      isConnectedRef.current = false;
      console.log('🔌 Disconnected from WebSocket');
    }
  }, []);

  const sendMessage = useCallback((messageData) => {
    if (clientRef.current && isConnectedRef.current) {
      console.log('📤 Sending message via WebSocket:', messageData);
      clientRef.current.publish({
        destination: '/app/chat.sendMessage',
        body: JSON.stringify(messageData)
      });
    } else {
      console.error('❌ WebSocket not connected, cannot send message');
    }
  }, []);

  // Connect on mount, disconnect on unmount
  useEffect(() => {
    connect();
    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  return {
    sendMessage,
    disconnect
  };
};
