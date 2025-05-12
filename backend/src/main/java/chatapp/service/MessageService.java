package chatapp.service;

import chatapp.model.Conversation;
import chatapp.model.Message;
import chatapp.repository.ConversationRepository;
import chatapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;

    @Transactional
    public Message saveMessage(String conversationId, Message message) {
        Conversation conversation = conversationRepository.findById(Long.parseLong(conversationId))
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
            
        message.setConversation(conversation);
        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessages(String conversationId) {
        return messageRepository.findByConversationIdOrderByTimestampAsc(
            Long.parseLong(conversationId)
        );
    }
}