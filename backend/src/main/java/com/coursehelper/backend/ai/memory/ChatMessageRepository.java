package com.coursehelper.backend.ai.memory;

import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // newest first + Limit, so we don't drag an entire year of chat into memory
    List<ChatMessage> findByUserIdAndConversationIdOrderByCreatedAtDescIdDesc(
        Long userId, String conversationId, Limit limit);

    @Modifying
    @Transactional
    void deleteByUserIdAndConversationId(Long userId, String conversationId);
}