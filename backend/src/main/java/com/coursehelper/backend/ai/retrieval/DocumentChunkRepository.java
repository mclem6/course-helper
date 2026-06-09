package com.coursehelper.backend.ai.retrieval;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long>{

    @Query(value = """
    SELECT id, user_id, filename, content, created_at, embedding::text as embedding 
    FROM document_chunks 
    WHERE user_id = :userId
    ORDER BY embedding <=> CAST(:embedding AS vector)
    LIMIT :topK
    """, nativeQuery = true)
    List<DocumentChunk> findSimilarChunks(
        @Param("userId") Long userId,
        @Param("embedding") String embedding,
        @Param("topK") int topK
    );

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO document_chunks (user_id, filename, content, embedding, created_at) " +
                "VALUES (:userId, :filename, :content, CAST(:embedding AS vector), :createdAt)",
        nativeQuery = true)
    void insertChunk(@Param("userId") Long userId,
                    @Param("filename") String filename,
                    @Param("content") String content,
                    @Param("embedding") String embedding,
                    @Param("createdAt") java.time.LocalDateTime createdAt);

    boolean existsByUserIdAndFilename(Long userId, String filename);

    void deleteByUserIdAndFilename(Long userId, String filename);
}
