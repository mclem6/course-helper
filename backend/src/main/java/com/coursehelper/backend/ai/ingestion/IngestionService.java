package com.coursehelper.backend.ai.ingestion;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.coursehelper.backend.ai.retrieval.DocumentChunk;
import com.coursehelper.backend.ai.retrieval.DocumentChunkRepository;
import com.coursehelper.backend.document.Document;
import com.coursehelper.backend.document.DocumentRepository;
import com.coursehelper.backend.exceptions.DuplicateFileException;

@Service
public class IngestionService {

    private final PdfParser pdfParser;
    private final TextChunker textChunker;
    private final EmbeddingService embeddingService;
    private final DocumentChunkRepository documentChunkRepository;
    private final DocumentRepository documentRepository;

    public IngestionService(PdfParser pdfParser,
                            TextChunker textChunker,
                            EmbeddingService embeddingService,
                            DocumentChunkRepository documentChunkRepository,
                            DocumentRepository documentRepository) {
        this.pdfParser = pdfParser;
        this.textChunker = textChunker;
        this.embeddingService = embeddingService;
        this.documentChunkRepository = documentChunkRepository;
        this.documentRepository = documentRepository;
    }

    public int ingest(MultipartFile file, Long userId) throws IOException {
        System.out.println("Starting ingestion for: " + file.getOriginalFilename());

         boolean exists = documentChunkRepository
        .existsByUserIdAndFilename(userId, file.getOriginalFilename());
    
        if (exists) {
            throw new DuplicateFileException(
                file.getOriginalFilename() + " has already been uploaded");
        }

        // 1. extract text from PDF
        String text = pdfParser.extractText(file);
        
        System.out.println("Extracted text length: " + text.length());


        // 2. split into overlapping chunks
        List<String> chunks = textChunker.chunk(text);

        System.out.println("Number of chunks: " + chunks.size());

        // 3. embed and save each chunk
        for (String chunkContent : chunks) {
            String embedding = embeddingService.embed(chunkContent);

             documentChunkRepository.insertChunk(
                userId,
                file.getOriginalFilename(),
                chunkContent,
                embedding,
                LocalDateTime.now()
            );
        }

        // 4. save document record with raw file
        Document document = new Document();
        document.setUserId(userId);
        document.setFilename(file.getOriginalFilename());
        document.setFileData(file.getBytes());
        document.setUploadedAt(LocalDateTime.now());
        documentRepository.save(document);

        // return how many chunks were stored
        return chunks.size();
    }
}