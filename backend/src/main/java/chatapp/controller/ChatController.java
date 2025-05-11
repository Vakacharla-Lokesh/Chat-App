package main.java.chatapp.controller;

import main.java.chatapp.model.Conversation;
import main.java.chatapp.model.Message;
import main.java.chatapp.model.User;
import main.java.chatapp.repository.ConversationRepository;
import main.java.chatapp.repository.MessageRepository;
import main.java.chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
public class ChatController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;

    // Handle WebSocket connection/join
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public Message addUser(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", message.getSenderId());
        return message;
    }
    
    // Public chat room
    @MessageMapping("/chat.public")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message) {
        message.setTimestamp(Instant.now());
        // We don't save public messages to DB
        return message;
    }
    
    // Private conversation
    @MessageMapping("/chat/{conversationId}")
    public void sendPrivateMessage(
            @DestinationVariable Long conversationId, 
            @Payload Message message,
            Principal principal) {
        
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Get sender user
        User sender = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        // Set sender and conversation
        message.setConversation(conversation);
        message.setSenderId(sender.getId());
        message.setTimestamp(Instant.now());
        
        // Save to database
        Message savedMessage = messageRepository.save(message);
        
        // Determine recipient
        Long recipientId = conversation.getUserOne().equals(sender.getId()) 
                ? conversation.getUserTwo() 
                : conversation.getUserOne();
                
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));
        
        // Send to sender
        messagingTemplate.convertAndSendToUser(
                sender.getUsername(),
                "/queue/messages", 
                savedMessage);
                
        // Send to recipient
        messagingTemplate.convertAndSendToUser(
                recipient.getUsername(),
                "/queue/messages", 
                savedMessage);
    }
}