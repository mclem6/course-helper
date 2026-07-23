package com.coursehelper.backend.ai.memory;

import java.util.List;

public interface ConversationMemory {

     /** Prior turns for this conversation, oldest first, already trimmed. */
    List<ChatTurn> load(Long userId, String conversationId);

    /** Append one exchange. */
    void append(Long userId, String conversationId, ChatTurn turn);

    void clear(Long userId, String conversationId);
    
}
