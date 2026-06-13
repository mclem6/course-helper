package com.coursehelper.backend.ai.ingestion;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.coursehelper.backend.exceptions.AIServiceException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;



@Service
public class EmbeddingService {

    private final String apiKey;
    private final ObjectMapper mapper;
    private final HttpClient client;

    public EmbeddingService(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.mapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
    }

    public String embed(String text) {
        try {
            // build request body
            Map<String, Object> body = Map.of(
                "model", "text-embedding-3-small",
                "input", text
            );

            String json = mapper.writeValueAsString(body);

            // send HTTP request 
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/embeddings"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());

            // parse the embedding out of the response
            JsonNode root = mapper.readTree(response.body());
            JsonNode embedding = root
                .path("data")
                .get(0)
                .path("embedding");

            // convert JsonNode array → float[]
            float[] vector = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                vector[i] = (float) embedding.get(i).asDouble();
            }

            return Arrays.toString(vector).replace(", ", ",");

        } catch (IOException | InterruptedException e) {
            throw new AIServiceException("Failed to call embedding API", e);
        }
    }
}