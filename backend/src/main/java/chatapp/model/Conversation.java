package chatapp.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_one_id")
    private Long userOne;

    @Column(name = "user_two_id")
    private Long userTwo;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    private String name;
    
    // Getters
    public Long getId() { return id; }
    public Long getUserOne() { return userOne; }
    public Long getUserTwo() { return userTwo; }
    public List<Message> getMessages() { return messages; }
    public String getName() { return name; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUserOne(Long userOne) { this.userOne = userOne; }
    public void setUserTwo(Long userTwo) { this.userTwo = userTwo; }
    public void setMessages(List<Message> messages) { this.messages = messages; }
    public void setName(String name) { this.name = name; }

    // Helper methods
    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setConversation(null);
    }
}