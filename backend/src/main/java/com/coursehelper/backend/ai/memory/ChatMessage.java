package com.coursehelper.backend.ai.memory;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat_messages", indexes = {
    @Index(name = "idx_chat_conv", columnList = "conversationId, createdAt")
})
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 64)
    private String conversationId;

    @Column(nullable = false, length = 16)
    private String role;              

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected ChatMessage() {}

    public ChatMessage(Long userId,
                       String conversationId,
                       String role,
                       String content) {
        this.userId         = userId;
        this.conversationId = conversationId;
        this.role           = role;
        this.content        = content;
        this.createdAt      = LocalDateTime.now();
    }

    public Long   getId()             { return id; }
    public Long   getUserId()         { return userId; }
    public String getConversationId() { return conversationId; }
    public String getRole()           { return role; }
    public String getContent()        { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}