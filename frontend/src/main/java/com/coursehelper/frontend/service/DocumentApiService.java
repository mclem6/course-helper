package com.coursehelper.frontend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.coursehelper.frontend.service.api.ApiClient;
import com.coursehelper.frontend.dto.DocumentResponseDto;
import com.coursehelper.frontend.exceptions.ApiException;
import com.fasterxml.jackson.core.type.TypeReference;

public class DocumentApiService {

    private static DocumentApiService instance;
    private final ApiClient apiClient;

    public static final String SOURCE_TYPE = "class";

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
            return apiClient.get("/documents",
                new TypeReference<List<DocumentResponseDto>>() {});
        } catch (IOException e) {
            throw new ApiException("Failed to load documents. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public String upload(File file) {
        try {
            return apiClient.postFile("/documents/upload", file);
        } catch (IOException e) {
            throw new ApiException("Failed to upload file. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public void download(Long id, File saveLocation) {
        try {
            byte[] fileData = apiClient.getBytes("/documents/" + id);
            try {
                Files.write(saveLocation.toPath(), fileData);
            } catch (IOException e) {
                throw new ApiException("Failed to save file to disk.", 500);
            }
        } catch (IOException e) {
            throw new ApiException("Failed to download file. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    public void delete(Long id) {
        try {
            apiClient.delete("/documents/" + id);
        } catch (IOException e) {
            throw new ApiException("Failed to delete document. Check connection.", 503);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (RuntimeException e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }
}