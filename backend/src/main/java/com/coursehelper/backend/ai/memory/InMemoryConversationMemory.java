
package com.coursehelper.backend.ai.memory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
// @Profile("dev")   // or use @ConditionalOnProperty
public class InMemoryConversationMemory implements ConversationMemory {

    private static final int MAX_TURNS = 20;

    private final Map<String, Deque<ChatTurn>> store = new ConcurrentHashMap<>();

    private String key(Long userId, String conversationId) {
        return userId + ":" + conversationId;
    }

    @Override
    public List<ChatTurn> load(Long userId, String conversationId) {
        Deque<ChatTurn> turns = store.get(key(userId, conversationId));
        return turns == null ? List.of() : new ArrayList<>(turns);
    }

    @Override
    public void append(Long userId, String conversationId, ChatTurn turn) {
        store.compute(key(userId, conversationId), (k, existing) -> {
            Deque<ChatTurn> turns = existing == null ? new ArrayDeque<>() : existing;
            synchronized (turns) {
                turns.addLast(turn);
                while (turns.size() > MAX_TURNS) turns.removeFirst();
            }
            return turns;
        });
    }

    @Override
    public void clear(Long userId, String conversationId) {
        store.remove(key(userId, conversationId));
    }
}
    

