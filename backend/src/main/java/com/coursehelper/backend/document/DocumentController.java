package com.coursehelper.backend.document;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coursehelper.backend.ai.AgentService;
import com.coursehelper.backend.ai.ingestion.IngestionService;
import com.coursehelper.backend.ai.retrieval.DocumentChunkRepository;
import com.coursehelper.backend.auth.CustomUserPrincipal;
import com.coursehelper.backend.document.dto.DocumentResponseDto;
import com.coursehelper.backend.exceptions.ResourceNotFoundException;
import com.coursehelper.backend.document.DocumentRepository;


@RestController
@RequestMapping("/api/documents")
public class DocumentController {


    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final IngestionService ingestionService;

    public DocumentController(DocumentChunkRepository documentChunkRepository, DocumentRepository documentRepository, IngestionService ingestionService) {
        this.documentChunkRepository = documentChunkRepository;
        this.documentRepository = documentRepository;
        this.ingestionService = ingestionService;
        
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload( @RequestParam("file") MultipartFile file, Authentication auth) {

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();
        
        try {
            int chunks = ingestionService.ingest(file, userId);
            return ResponseEntity.ok(
                "Stored " + chunks + " chunks from " + file.getOriginalFilename()
            );
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to process file: " + e.getMessage());
        }
    }




    // list all documents for user
    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> getDocuments(Authentication auth) {
        CustomUserPrincipal user = (CustomUserPrincipal) auth.getPrincipal();
        List<Document> documents = documentRepository.findByUserId(user.getUserId());
        List<DocumentResponseDto> dtos = documents.stream()
            .map(d -> new DocumentResponseDto(d.getId(), d.getFilename(), d.getUploadedAt()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // download a document
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long id, Authentication auth) {
        CustomUserPrincipal user = (CustomUserPrincipal) auth.getPrincipal();
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        // make sure user owns this document
        if (!document.getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok()
            .header("Content-Disposition", 
                    "attachment; filename=\"" + document.getFilename() + "\"")
            .header("Content-Type", "application/pdf")
            .body(document.getFileData());
    }

    // delete a document
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Long id, Authentication auth) {
        CustomUserPrincipal user = (CustomUserPrincipal) auth.getPrincipal();
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        // make sure user owns this document
        if (!document.getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        // delete chunks
        documentChunkRepository.deleteByUserIdAndFilename(
            user.getUserId(), document.getFilename());

        // delete document record
        documentRepository.delete(document);

        return ResponseEntity.ok("Document deleted successfully");
    }



    
}
