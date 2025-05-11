package main.java.chatapp.repository;

import com.ikaansh.chatapp.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserOneOrUserTwo(Long userOne, Long userTwo);
}