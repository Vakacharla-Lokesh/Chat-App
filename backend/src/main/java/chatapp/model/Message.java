package chatapp.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "timestamp")
    private Instant timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    // Getters
    public Long getId() { return id; }
    public String getContent() { return content; }
    public Long getSenderId() { return senderId; }
    public Instant getTimestamp() { return timestamp; }
    public Conversation getConversation() { return conversation; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }
}

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
}