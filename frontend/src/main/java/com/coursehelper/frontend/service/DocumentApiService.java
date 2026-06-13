package com.coursehelper.frontend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.coursehelper.frontend.exceptions.ApiException;
import com.coursehelper.frontend.service.api.ApiClient;
import com.coursehelper.frontend.dto.DocumentResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;

public class DocumentApiService {

    private static DocumentApiService instance;
    private final ApiClient apiClient;

    public DocumentApiService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public static DocumentApiService getInstance() {
        if (instance == null) {
            instance = new DocumentApiService(new ApiClient());
        }
        return instance;
    }

    public List<DocumentResponseDto> getDocuments() {
        try {
            return apiClient.get("/documents", new TypeReference<List<DocumentResponseDto>>() {});
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Failed to load documents. Check connection.", 503) : e;
        }
    }

    public String upload(File file) {
        try {
            return apiClient.postFile("/documents/upload", file);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Failed to upload file. Check connection.", 503) : e;
        }
    }

    public void download(Long id, File saveLocation) {
        try {
            byte[] fileData = apiClient.getBytes("/documents/" + id);
            Files.write(saveLocation.toPath(), fileData);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Failed to download file. Check connection.", 503) : e;
        } catch (IOException e) {
            throw new ApiException("Failed to save file to disk.", 500);
        }
    }

    public void delete(Long id) {
        try {
            apiClient.delete("/documents/" + id);
        } catch (ApiException e) {
            throw e.getStatus() == 503
                ? new ApiException("Failed to delete document. Check connection.", 503) : e;
        }
    }
}
