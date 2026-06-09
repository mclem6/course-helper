package com.coursehelper.backend.ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.coursehelper.backend.exceptions.AIServiceException;

import tools.jackson.databind.ObjectMapper;

@Component
public class LLMClient {


    private ObjectMapper mapper;
    private String apiKey;
    private final HttpClient client; 

    public LLMClient(@Value("${openai.api.key}") String apiKey){
        this.apiKey = apiKey;
        this.mapper = new ObjectMapper();
        this.client = HttpClient.newHttpClient();
    }


    public String call(String systemMessage, String userMessage) {

        try {

            Map<String, Object> prompt = Map.of(
            "model", "gpt-4o-mini", 
            "messages", List.of(
                    Map.of("role", "system", "content", systemMessage),
                    Map.of("role", "user", "content", userMessage)
                )

            );


            String json = mapper.writeValueAsString(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                            .header("Authorization", "Bearer " + apiKey)
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json))
                            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.body());

            return response.body();
            
        } catch (IOException | InterruptedException e) {
            throw new AIServiceException("Failed to call AI Model", e);
        }

    }

    public Map<String, Object> callWithTools(
        List<Map<String, Object>> messages,
        List<Map<String, Object>> tools) {

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");
            body.put("messages", messages);
            body.put("tools", tools);
            body.put("tool_choice", "auto"); 

            String json = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

            // pase reponse to map
            Map<String, Object> responseMap = mapper.readValue(response.body(), 
                new tools.jackson.core.type.TypeReference<Map<String, Object>>() {});

            // check for API error before returning
            if (responseMap.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
                throw new AIServiceException("OpenAI API error: " + error.get("message"));
            }

            return responseMap;

            
        } catch (IOException | InterruptedException e) {
            throw new AIServiceException("Failed to call AI Model", e);
        }
    }
    
}
