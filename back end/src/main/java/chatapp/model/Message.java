package main.java.chatapp.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    private Long senderId;
    private String content;
    private Instant timestamp = Instant.now();
    private boolean isRead = false;
    // Getters & setters
}