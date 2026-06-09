package com.coursehelper.backend.ai.ingestion;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


@Component 
public class TextChunker {

    private static final int CHUNK_SIZE    = 500;  // words per chunk
    private static final int CHUNK_OVERLAP = 50;   // words shared with next chunk

    public List<String> chunk(String text) {
        String[] words = text.split("\\s+");
        List<String> chunks = new ArrayList<>();

        int start = 0;
        while (start < words.length) {
            int end = Math.min(start + CHUNK_SIZE, words.length);

            // join words[start..end] back into a string
            String chunk = String.join(" ", 
                java.util.Arrays.copyOfRange(words, start, end));
            chunks.add(chunk);

            // advance by (CHUNK_SIZE - CHUNK_OVERLAP) so next chunk overlaps
            start += (CHUNK_SIZE - CHUNK_OVERLAP);
        }

        return chunks;
    }
    
}
