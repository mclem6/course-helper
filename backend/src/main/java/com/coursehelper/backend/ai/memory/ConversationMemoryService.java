package com.coursehelper.backend.ai.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversationMemoryService {

    private final ChatMessageRepository repo;
    private final int maxMessages;  

    public ConversationMemoryService(
            ChatMessageRepository repo,
            @Value("${agent.memory.max-messages:20}") int maxMessages) {
        this.repo = repo;
        this.maxMessages = maxMessages;
    }

    public List<Map<String, Object>> loadHistory(String conversationId) {
        List<ChatMessage> all = repo
                .findByConversationIdOrderByCreatedAtAsc(conversationId);

        if (all.isEmpty()) return List.of();

        // take last N
        List<ChatMessage> window = all.size() <= maxMessages
                ? all
                : all.subList(all.size() - maxMessages, all.size());

        // start with user message, skip any assistant messages at the start
        int start = 0;
        while (start < window.size()
                && !"user".equals(window.get(start).getRole())) {
            start++;
        }

        List<Map<String, Object>> history = new ArrayList<>();
        for (int i = start; i < window.size(); i++) {
            ChatMessage m = window.get(i);
            history.add(Map.of(
                "role",    m.getRole(),
                "content", m.getContent()
            ));
        }
        return history;
    }

    // Persist one message
    public void save(String conversationId, Long userId,
                     String role, String content) {
        repo.save(new ChatMessage(userId, conversationId, role, content));
    }

    //Persist a full user→assistant turn in one shot.
    @Transactional
    public void saveTurn(String conversationId, Long userId,
                         String userText, String assistantText) {
        save(conversationId, userId, "user",      userText);
        save(conversationId, userId, "assistant",  assistantText);
    }

    @Transactional
    public void clearConversation(String conversationId) {
        repo.deleteByConversationId(conversationId);
    }
}