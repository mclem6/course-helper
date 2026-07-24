package com.coursehelper.backend.ai.memory;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JpaConversationMemory implements ConversationMemory {

    /** Turns of context replayed to the model. Tune against your token budget. */
    private static final int MAX_TURNS = 20;

    private final ChatMessageRepository repository;

    public JpaConversationMemory(ChatMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatTurn> load(Long userId, String conversationId) {
        List<ChatMessage> newestFirst =
            repository.findByUserIdAndConversationIdOrderByCreatedAtDescIdDesc(
                userId, conversationId, Limit.of(MAX_TURNS));

        List<ChatTurn> turns = new ArrayList<>(newestFirst.size());
        for (int i = newestFirst.size() - 1; i >= 0; i--) {   // flip to oldest-first
            ChatMessage m = newestFirst.get(i);
            turns.add(new ChatTurn(m.getRole(), m.getContent()));
        }
        // Never open the window on a dangling assistant message
        if (!turns.isEmpty() && "assistant".equals(turns.get(0).role())) {
            turns.remove(0);
        }
        return turns;
    }

    @Override
    @Transactional
    public void append(Long userId, String conversationId, ChatTurn turn) {
        repository.save(new ChatMessage(userId, conversationId, turn.role(), turn.content()));
    }

    @Override
    @Transactional
    public void clear(Long userId, String conversationId) {
        repository.deleteByUserIdAndConversationId(userId, conversationId);
    }
}