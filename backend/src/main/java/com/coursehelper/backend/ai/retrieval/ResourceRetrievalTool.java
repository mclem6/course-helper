package com.coursehelper.backend.ai.retrieval;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.coursehelper.backend.ai.ingestion.EmbeddingService;

@Component
public class ResourceRetrievalTool {

    private final EmbeddingService embeddingService;
    private final DocumentChunkRepository documentChunkRepository;

    public ResourceRetrievalTool(EmbeddingService embeddingService,
                                  DocumentChunkRepository documentChunkRepository) {
        this.embeddingService = embeddingService;
        this.documentChunkRepository = documentChunkRepository;
    }

    public String search(String query, Long userId) {
        // embed the query, return string 
        String vectorString  = embeddingService.embed(query);

        // find top 5 most similar chunks
        try {
            List<DocumentChunk> chunks = documentChunkRepository
                    .findSimilarChunks(userId, vectorString, 5);


        // if nothing found
        if (chunks.isEmpty()) {
            return "No relevant study materials found for this query.";
        }

        // format chunks into a readable string for GPT-4o
        return chunks.stream()
                .map(chunk -> "From " + chunk.getFilename() + ":\n" + chunk.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));

        } catch (Exception e) {
            return "Error searching documents";
        }


    }

}