package com.coursehelper.backend.ai.memory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /** Full history for a conversation, oldest first */
    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    /** All conversations for a user (for a "history" sidebar) */
    @Query("""
        SELECT DISTINCT c.conversationId
        FROM ChatMessage c
        WHERE c.userId = :userId
        ORDER BY c.conversationId
    """)
    List<String> findConversationIdsByUserId(Long userId);

    void deleteByConversationId(String conversationId);
}