package com.coursehelper.backend.ai.memory;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.coursehelper.backend.ai.LLMClient;
import com.coursehelper.backend.ai.memory.ConversationMemory;

@Component
@Primary
public class SummarizingConversationMemory implements ConversationMemory {

    private static final int KEEP_VERBATIM = 6;

    private final ConversationMemory delegate;   
    private final LLMClient llmClient;

    public SummarizingConversationMemory(JpaConversationMemory delegate, LLMClient llmClient) {  // concrete type, not the interface
        this.delegate = delegate;
        this.llmClient = llmClient;
    }

    @Override
    public List<ChatTurn> load(Long userId, String conversationId) {
        List<ChatTurn> turns = delegate.load(userId, conversationId);
        if (turns.size() <= KEEP_VERBATIM) return turns;

        List<ChatTurn> older  = turns.subList(0, turns.size() - KEEP_VERBATIM);
        List<ChatTurn> recent = turns.subList(turns.size() - KEEP_VERBATIM, turns.size());

        String summary = llmClient.summarize(older);   // cache this keyed by last summarized id

        List<ChatTurn> result = new ArrayList<>();
        result.add(new ChatTurn("user", "Summary of our earlier conversation: " + summary));
        result.add(new ChatTurn("assistant", "Understood — I'll keep that context in mind."));
        result.addAll(recent);
        return result;
    }
    // append/clear delegate straight through

    @Override
    public void append(Long userId, String conversationId, ChatTurn turn) {
        delegate.append(userId, conversationId, turn);
    }

    @Override
    public void clear(Long userId, String conversationId) {
        delegate.clear(userId, conversationId);
    }
}