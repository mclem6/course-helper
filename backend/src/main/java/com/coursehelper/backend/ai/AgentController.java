package com.coursehelper.backend.ai;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coursehelper.backend.ai.dto.ChatRequestDto;
import com.coursehelper.backend.ai.ingestion.IngestionService;
import com.coursehelper.backend.ai.retrieval.DocumentChunkRepository;
import com.coursehelper.backend.auth.CustomUserPrincipal;
import com.coursehelper.backend.document.DocumentRepository;


@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentService agent;
    private final IngestionService ingestionService;
    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;

    public AgentController(AgentService agent, IngestionService ingestionService, DocumentChunkRepository documentChunkRepository, DocumentRepository documentRepository) {
        this.agent = agent;
        this.ingestionService = ingestionService;
        this.documentChunkRepository = documentChunkRepository;
        this.documentRepository = documentRepository;
        
    }


    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequestDto request, Authentication auth){

        CustomUserPrincipal user = 
            (CustomUserPrincipal) auth.getPrincipal();

        Long userId = user.getUserId();
        String username = user.getUsername();

        return ResponseEntity.ok(
            agent.answerQuery(request.getMessage(), userId, username)
        );
    }




}

