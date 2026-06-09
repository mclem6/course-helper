package com.coursehelper.backend.document.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DocumentResponseDto {
    private Long id;
    private String filename;
    private LocalDateTime uploadedAt;

    public DocumentResponseDto(Long id, String filename, LocalDateTime uploadedAt) {
        this.id = id;
        this.filename = filename;
        this.uploadedAt = uploadedAt;
    }

}
