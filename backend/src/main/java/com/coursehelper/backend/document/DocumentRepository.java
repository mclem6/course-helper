package com.coursehelper.backend.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUserId(Long userId);

    Optional<Document> findByUserIdAndFilename(Long userId, String filename);

    boolean existsByUserIdAndFilename(Long userId, String filename);

    void deleteByUserIdAndFilename(Long userId, String filename);
}
