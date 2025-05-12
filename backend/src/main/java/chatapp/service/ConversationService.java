package chatapp.service;

import chatapp.model.Conversation;
import chatapp.model.User;
import chatapp.repository.ConversationRepository;
import chatapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Conversation createConversation(Long userOneId, Long userTwoId) {
        User userOne = userRepository.findById(userOneId)
            .orElseThrow(() -> new RuntimeException("User One not found"));
        User userTwo = userRepository.findById(userTwoId)
            .orElseThrow(() -> new RuntimeException("User Two not found"));

        Conversation conversation = new Conversation();
        conversation.setUserOne(userOne.getId());
        conversation.setUserTwo(userTwo.getId());
        
        return conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findByUserOneOrUserTwo(userId, userId);
    }
}