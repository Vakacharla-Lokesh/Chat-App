package main.java.chatapp.model;

import jakarta.persistence.*;

@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userOne;
    private Long userTwo;
    // Getters & setters
}