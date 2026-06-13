package com.coursehelper.frontend.dto;

import java.time.LocalDateTime;

public class DocumentResponseDto {

    private Long id;
    private String filename;
    private LocalDateTime uploadedAt;

    public DocumentResponseDto() {}

    public Long getId() { return id; }
    public String getFilename() { return filename; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }

    public void setId(Long id) { this.id = id; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}