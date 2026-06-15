package com.coursehelper.backend.exceptions;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameExists(
            UsernameAlreadyExistsException e) {
        return ResponseEntity.status(400).body(errorBody(400, e.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
            InvalidCredentialsException e) {
        return ResponseEntity.status(401).body(errorBody(401, e.getMessage()));
    }

    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<Map<String, Object>> handleAIServiceException(
            AIServiceException e) {
        log.error("AI service error: {}", e.getMessage(), e);
        return ResponseEntity.status(503).body(errorBody(503, "AI service unavailable."));
    }

    @ExceptionHandler(DuplicateFileException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateFile(
            DuplicateFileException e) {
        return ResponseEntity.status(409).body(errorBody(409, e.getMessage()));
    }

    @ExceptionHandler(AssignmentException.class)
    public ResponseEntity<Map<String, Object>> handleAssignmentException(
            AssignmentException e) {
        return ResponseEntity.status(409).body(errorBody(409, e.getMessage()));
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<Map<String, Object>> handleFileProcessing(
            FileProcessingException e) {
        log.error("File processing error: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(errorBody(500, e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
            ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(errorBody(404, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(500).body(errorBody(500, "An unexpected error occurred."));
    }

    private Map<String, Object> errorBody(int status, String message) {
        return Map.of("status", status, "error", message);
    }
}