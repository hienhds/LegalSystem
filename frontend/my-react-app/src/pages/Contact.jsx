import React, { useState, useEffect, useCallback, useRef } from "react";
import Layout from "../components/Layout";
import { apiFetch } from "../utils/api";
import useUserProfile from "../hooks/useUserProfile";
import { useChatWebSocket } from "../hooks/useChatWebSocket";
import "./Contact.css";

export default function Contact() {
  const { user, loading: userLoading } = useUserProfile();
  const [conversations, setConversations] = useState([]);
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messages, setMessages] = useState([]);
  const [messageInput, setMessageInput] = useState("");
  const [loading, setLoading] = useState(true);
  const messagesEndRef = useRef(null);

  // Auto scroll to bottom when new message arrives
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  // Scroll to bottom when messages change
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // WebSocket for real-time messages
  const handleNewMessage = useCallback((newMessage) => {
    setMessages((prevMessages) => [...prevMessages, newMessage]);
    // Update conversation list with new last message
    setConversations(prevConvs => 
      prevConvs.map(conv => 
        conv.conversationId === newMessage.conversationId
          ? { ...conv, lastMessage: newMessage }
          : conv
      )
    );
  }, []);

  useChatWebSocket(
    selectedConversation?.conversationId,
    handleNewMessage
  );

  // Load conversations when component mounts
  useEffect(() => {
    if (!userLoading && user) {
      // Get the actual user ID - could be userId (for citizens) or lawyerId (for lawyers)
      const actualUserId = user.userId || user.lawyerId;
      
      if (actualUserId) {
        loadConversations();
      } else {
        setLoading(false);
      }
    } else if (!userLoading && !user) {
      setLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userLoading, user]);

  // Load messages when conversation is selected
  useEffect(() => {
    if (selectedConversation) {
      loadMessages(selectedConversation.conversationId);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedConversation?.conversationId]);

  const loadConversations = async () => {
    try {
      // Get the actual user ID - could be userId or lawyerId
      const actualUserId = user?.userId || user?.lawyerId;
      
      if (!actualUserId) {
        setLoading(false);
        return;
      }
      
      const response = await apiFetch(
        `http://localhost:8080/api/chat/conversations?userId=${actualUserId}`
      );
      const data = await response.json();
      
      if (data.success) {
        // Map the flat structure to nested otherUser object
        const mappedConversations = (data.data || []).map(conv => ({
          ...conv,
          otherUser: {
            userId: conv.otherUserId,
            lawyerId: conv.otherUserId, // Could be lawyer or user
            fullName: conv.otherUserName,
            avatarUrl: conv.otherUserAvatar,
            userType: conv.otherUserType
          }
        }));
        
        setConversations(mappedConversations);
        // Auto-select first conversation
        if (mappedConversations.length > 0) {
          setSelectedConversation(mappedConversations[0]);
        }
      }
    } catch (error) {
      console.error("Error loading conversations:", error);
    } finally {
      setLoading(false);
    }
  };

  const loadMessages = async (conversationId) => {
    try {
      const response = await apiFetch(
        `http://localhost:8080/api/chat/conversations/${conversationId}/messages?page=0&size=50`
      );
      const data = await response.json();
      if (data.success) {
        const newMessages = data.data.content || [];
        // Reverse to show oldest first
        setMessages(newMessages.reverse());
        
        // Mark as read
        markAsRead(conversationId);
      }
    } catch (error) {
      console.error("Error loading messages:", error);
    }
  };

  const markAsRead = async (conversationId) => {
    try {
      const actualUserId = user?.userId || user?.lawyerId;
      if (!actualUserId) return;
      
      await apiFetch(
        `http://localhost:8080/api/chat/conversations/${conversationId}/read?userId=${actualUserId}`,
        { method: 'PUT' }
      );
    } catch (error) {
      console.error("Error marking as read:", error);
    }
  };

  const sendMessage = async () => {
    if (!messageInput.trim() || !selectedConversation) return;

    // Get the actual user ID - could be userId or lawyerId
    const actualUserId = user.userId || user.lawyerId;
    
    const messageData = {
      conversationId: selectedConversation.conversationId,
      senderId: actualUserId,
      senderType: user.lawyerId ? "LAWYER" : "CITIZEN",
      content: messageInput,
      messageType: "TEXT"
    };

    // Clear input immediately for better UX
    const currentMessage = messageInput;
    setMessageInput("");

    try {
      // Send via REST API only - backend will broadcast via WebSocket
      const response = await apiFetch(
        'http://localhost:8080/api/chat/messages',
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(messageData)
        }
      );
      
      const data = await response.json();
      if (data.success) {
        // Don't add message here - it will come via WebSocket broadcast
        // This prevents duplicate messages
        // Reload conversations to update last message
        loadConversations();
      } else {
        // Restore input if failed
        setMessageInput(currentMessage);
      }
    } catch (error) {
      console.error("Error sending message:", error);
      // Restore input if failed
      setMessageInput(currentMessage);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  if (loading) {
    return (
      <Layout showFooter={false}>
        <div className="flex items-center justify-center h-screen">
          <p className="text-slate-500">Đang tải...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout showFooter={false} fullWidth={true}>
      <div className="chat-layout-container flex h-full overflow-hidden">
        {/* Sidebar */}
        <aside className="w-96 flex-shrink-0 border-r border-border-light dark:border-border-dark flex flex-col bg-surface-light dark:bg-surface-dark h-full">
          <div className="p-4 border-b border-border-light dark:border-border-dark">
            <h1 className="text-2xl font-bold text-text-primary-light dark:text-text-primary-dark">Trò chuyện</h1>
            <div className="relative mt-4">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-text-secondary-light dark:text-text-secondary-dark">search</span>
              <input className="w-full bg-background-light dark:bg-background-dark border-border-light dark:border-border-dark rounded-full py-2 pl-10 pr-4 focus:ring-primary focus:border-primary" placeholder="Tìm kiếm trong LegalConnect" type="text" />
            </div>
          </div>
          <nav className="flex-1 overflow-y-auto">
            {conversations.length === 0 ? (
              <div className="p-4 text-center text-slate-500">
                Chưa có cuộc trò chuyện nào
              </div>
            ) : (
              <ul>
                {conversations.map(conv => (
                  <li 
                    key={conv.conversationId}
                    className={`flex items-center p-3 space-x-3 cursor-pointer ${
                      selectedConversation?.conversationId === conv.conversationId 
                        ? 'bg-primary/10 dark:bg-primary/20' 
                        : 'hover:bg-hover-light dark:hover:bg-hover-dark transition-colors rounded-lg mx-1'
                    }`}
                    onClick={() => setSelectedConversation(conv)}
                  >
                    <div className="relative flex-shrink-0">
                      <img 
                        alt={`Ảnh đại diện ${conv.otherUser?.fullName || conv.otherUser?.name || 'User'}`} 
                        className="w-14 h-14 rounded-full object-cover bg-gray-200" 
                        src={conv.otherUser?.avatarUrl ? `http://localhost:8080${conv.otherUser.avatarUrl}` : 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="150" height="150"%3E%3Crect fill="%23ddd" width="150" height="150"/%3E%3Ctext fill="%23999" x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-size="48"%3E?%3C/text%3E%3C/svg%3E'} 
                        onError={(e) => { e.target.style.display = 'none'; }}
                      />
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex justify-between items-center">
                        <p className="font-semibold truncate text-text-primary-light dark:text-text-primary-dark">
                          {conv.otherUser?.fullName || conv.otherUser?.name || 'Unknown User'}
                        </p>
                        <p className="text-xs text-text-secondary-light dark:text-text-secondary-dark flex-shrink-0">
                          {conv.lastMessage ? new Date(conv.lastMessage.timestamp).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) : ''}
                        </p>
                      </div>
                      <div className="flex justify-between items-start mt-1">
                        <p className="text-sm text-text-secondary-light dark:text-text-secondary-dark truncate">
                          {conv.lastMessage?.content || 'Chưa có tin nhắn'}
                        </p>
                        {conv.unreadCount > 0 && (
                          <span className="flex-shrink-0 text-xs bg-primary text-white rounded-full px-2 py-0.5">
                            {conv.unreadCount}
                          </span>
                        )}
                      </div>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </nav>
        </aside>
        {/* Main chat section */}
        <section className="flex-1 flex flex-col bg-background-light dark:bg-background-dark h-full overflow-hidden">
          {selectedConversation ? (
            <>
              <header className="flex-shrink-0 flex items-center justify-between p-4 border-b border-border-light dark:border-border-dark bg-background-light dark:bg-background-dark">
                <div className="flex items-center space-x-3">
                  <img 
                    alt={`Ảnh đại diện ${selectedConversation.otherUser?.fullName || selectedConversation.otherUser?.name || 'User'}`} 
                    className="w-12 h-12 rounded-full object-cover bg-gray-200" 
                    src={selectedConversation.otherUser?.avatarUrl ? `http://localhost:8080${selectedConversation.otherUser.avatarUrl}` : 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="150" height="150"%3E%3Crect fill="%23ddd" width="150" height="150"/%3E%3Ctext fill="%23999" x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-size="48"%3E?%3C/text%3E%3C/svg%3E'} 
                    onError={(e) => { e.target.style.display = 'none'; }}
                  />
                  <div>
                    <h2 className="font-bold text-lg text-text-primary-light dark:text-text-primary-dark">
                      {selectedConversation.otherUser?.fullName || selectedConversation.otherUser?.name || 'Unknown User'}
                    </h2>
                    <p className="text-sm text-green-500">Đang hoạt động</p>
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <button className="p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-text-secondary-light dark:text-text-secondary-dark transition-colors">
                    <span className="material-symbols-outlined">call</span>
                  </button>
                  <button className="p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-text-secondary-light dark:text-text-secondary-dark transition-colors">
                    <span className="material-symbols-outlined">videocam</span>
                  </button>
                  <button className="p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-text-secondary-light dark:text-text-secondary-dark transition-colors">
                    <span className="material-symbols-outlined">info</span>
                  </button>
                </div>
              </header>
              <div className="flex-1 p-6 overflow-y-auto space-y-4" style={{ maxHeight: 'calc(100vh - 180px)' }}>
                {messages.length === 0 ? (
                  <div className="flex items-center justify-center h-full text-slate-500">
                    Chưa có tin nhắn nào
                  </div>
                ) : (
                  messages.map((msg) => {
                    const currentUserId = user?.userId || user?.lawyerId;
                    const isFromMe = msg.senderId === currentUserId;
                    return (
                      <div key={msg.messageId} className={`flex items-start gap-3 ${isFromMe ? 'justify-end' : ''}`}>
                        {!isFromMe && (
                          <img 
                            alt={selectedConversation.otherUser?.fullName || selectedConversation.otherUser?.name || 'User'} 
                            className="w-8 h-8 rounded-full flex-shrink-0 bg-gray-200" 
                            src={selectedConversation.otherUser?.avatarUrl ? `http://localhost:8080${selectedConversation.otherUser.avatarUrl}` : 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="150" height="150"%3E%3Crect fill="%23ddd" width="150" height="150"/%3E%3Ctext fill="%23999" x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-size="48"%3E?%3C/text%3E%3C/svg%3E'} 
                            onError={(e) => { e.target.style.display = 'none'; }}
                          />
                        )}
                        <div className={`flex flex-col items-${isFromMe ? 'end' : 'start'} gap-1`}>
                          <div className={`${
                            isFromMe 
                              ? 'bg-primary text-white rounded-xl rounded-tr-sm' 
                              : 'bg-surface-light dark:bg-surface-dark rounded-xl rounded-tl-sm'
                          } p-3 max-w-md`}>
                            <p>{msg.content}</p>
                            {msg.fileUrl && (
                              <div className="mt-2 flex items-center gap-3 p-3 rounded-lg bg-background-light dark:bg-background-dark border border-border-light dark:border-border-dark">
                                <span className="material-symbols-outlined text-red-500">description</span>
                                <div className="flex-1">
                                  <p className="font-medium text-sm">{msg.fileName || 'File'}</p>
                                  <p className="text-xs text-text-secondary-light dark:text-text-secondary-dark">Tải xuống</p>
                                </div>
                                <a 
                                  href={msg.fileUrl} 
                                  target="_blank" 
                                  rel="noopener noreferrer"
                                  className="p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-text-secondary-light dark:text-text-secondary-dark transition-colors"
                                >
                                  <span className="material-symbols-outlined" style={{ fontSize: 20 }}>download</span>
                                </a>
                              </div>
                            )}
                          </div>
                          <span className="text-xs text-text-secondary-light dark:text-text-secondary-dark">
                            {new Date(msg.timestamp).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}
                          </span>
                          {isFromMe && msg.status === "READ" && (
                            <span className="material-symbols-outlined text-primary" style={{ fontSize: 16 }}>done_all</span>
                          )}
                        </div>
                        {isFromMe && (
                          <img 
                            alt="Ảnh đại diện của bạn" 
                            className="w-8 h-8 rounded-full flex-shrink-0 bg-gray-200" 
                            src={user?.avatarUrl ? `http://localhost:8080${user.avatarUrl}` : 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="150" height="150"%3E%3Crect fill="%23ddd" width="150" height="150"/%3E%3Ctext fill="%23999" x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-size="48"%3E?%3C/text%3E%3C/svg%3E'} 
                            onError={(e) => { e.target.style.display = 'none'; }}
                          />
                        )}
                      </div>
                    );
                  })
                )}
                {/* Auto-scroll anchor */}
                <div ref={messagesEndRef} />
              </div>
              <footer className="flex-shrink-0 p-4 border-t border-border-light dark:border-border-dark bg-surface-light dark:bg-surface-dark">
                <div className="flex items-center space-x-2">
                  <button className="p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-primary transition-colors">
                    <span className="material-symbols-outlined">add_circle</span>
                  </button>
                  <button className="p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-primary transition-colors">
                    <span className="material-symbols-outlined">image</span>
                  </button>
                  <button className="p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-primary transition-colors">
                    <span className="material-symbols-outlined">attach_file</span>
                  </button>
                  <div className="flex-1 relative">
                    <input 
                      className="w-full bg-surface-light dark:bg-surface-dark border-transparent rounded-full py-2.5 px-4 pr-12 focus:ring-primary focus:border-primary" 
                      placeholder="Nhập tin nhắn..." 
                      type="text"
                      value={messageInput}
                      onChange={(e) => setMessageInput(e.target.value)}
                      onKeyDown={handleKeyPress}
                    />
                    <button className="absolute right-2 top-1/2 -translate-y-1/2 p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-text-secondary-light dark:text-text-secondary-dark transition-colors">
                      <span className="material-symbols-outlined">sentiment_satisfied</span>
                    </button>
                  </div>
                  <button 
                    className="p-2 rounded-full hover:bg-hover-light dark:hover:bg-hover-dark text-primary transition-colors"
                    onClick={sendMessage}
                  >
                    <span className="material-symbols-outlined">send</span>
                  </button>
                </div>
              </footer>
            </>
          ) : (
            <div className="flex items-center justify-center h-full text-slate-500">
              Chọn một cuộc trò chuyện để bắt đầu
            </div>
          )}
        </section>
      </div>
    </Layout>
  );
}
