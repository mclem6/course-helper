package com.coursehelper.frontend.service.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.coursehelper.frontend.UserSession;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;



public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient client;
    private final ObjectMapper mapper;
    

    public ApiClient(){
        client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
    }


    public <T,R> R post(String endpoint, T requestObj, Class<R> responseClass) throws IOException, InterruptedException{

        String json = mapper.writeValueAsString(requestObj);
        

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + endpoint))
        .header("Content-Type", "application/json");
        

        if (UserSession.getToken() != null){
            requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
        }

        HttpRequest request = requestBuilder
                            .POST(HttpRequest.BodyPublishers.ofString(json))
                            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();

        if (status >= 200 && status < 300){

            if (responseClass == String.class) {
                return responseClass.cast(response.body());
            }

            R responseObj = mapper.readValue(response.body(), responseClass);
            return responseObj;

        } else {

            String body = response.body();
            
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Request failed with status: " + status);
            }

            Map<String, Object> errorMap = mapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
            String message = (String)errorMap.getOrDefault("error", "Unknown error");

            throw new RuntimeException(message);
        }
        
    }

    public <T> T get(String endpoint, Class<T> clazz) throws IOException, InterruptedException {
    HttpResponse<String> response = executeGet(endpoint);
    String body = handleResponse(response);
    if (body == null) return null;
    return mapper.readValue(body, clazz);
}

    public <T> T get(String endpoint, TypeReference<T> typeRef) throws IOException, InterruptedException {
        HttpResponse<String> response = executeGet(endpoint);
        String body = handleResponse(response);
        if (body == null) return null;
        return mapper.readValue(body, typeRef);
    }

    // shared request building
    private HttpResponse<String> executeGet(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "application/json");

        if (UserSession.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
        }

        return client.send(requestBuilder.GET().build(), 
            HttpResponse.BodyHandlers.ofString());
    }

    // shared response handling
    private String handleResponse(HttpResponse<String> response) {
        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            if (response.body() == null || response.body().isBlank()) {
                return null; // 204 No Content
            }
            return response.body();
        } else {
            String body = response.body();
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Request failed with status: " + status);
            }
            try {
                Map<String, Object> errorMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
                String message = (String) errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            } catch (IOException e) {
                throw new RuntimeException("Request failed with status: " + status);
            }
        }
    }

    public String postFile(String endpoint, File file) throws IOException, InterruptedException {
        String boundary = "----Boundary" + System.currentTimeMillis();
        
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        String bodyStart = "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
            "Content-Type: application/octet-stream\r\n\r\n";
        String bodyEnd = "\r\n--" + boundary + "--\r\n";

        byte[] start = bodyStart.getBytes();
        byte[] end = bodyEnd.getBytes();
        byte[] body = new byte[start.length + fileBytes.length + end.length];
        System.arraycopy(start, 0, body, 0, start.length);
        System.arraycopy(fileBytes, 0, body, start.length, fileBytes.length);
        System.arraycopy(end, 0, body, start.length + fileBytes.length, end.length);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "multipart/form-data; boundary=" + boundary);

        if (UserSession.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
        }

        HttpRequest request = requestBuilder
            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            .build();

        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            return response.body();
        } else {
            Map<String, String> errorMap = mapper.readValue(response.body(), new TypeReference<Map<String, String>>() {});
            String message = errorMap.getOrDefault("message", "Upload failed");
            throw new RuntimeException(message);
        }

    }

    public byte[] getBytes(String endpoint) throws IOException, InterruptedException {
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + endpoint))
        .header("Content-Type", "application/json");

        if (UserSession.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
        }

        HttpRequest request = requestBuilder.GET().build();
        HttpResponse<byte[]> response = client.send(request,
            HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            byte[] body = response.body();
            
            if (body == null || body.length == 0) {
                return null; // covers 204 and empty body
            }
            return body;
        } else {
            throw new RuntimeException("Download failed with status: " + response.statusCode());
        }
    }

    public void delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "application/json");

        if (UserSession.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
        }

        HttpRequest request = requestBuilder.DELETE().build();
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String body = response.body();
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Delete failed with status: " + response.statusCode());
            }
            try {
                Map<String, Object> errorMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
                String message = (String) errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            } catch (IOException e) {
                throw new RuntimeException("Delete failed with status: " + response.statusCode());
            }
        }
    }

    public <T, R> R patch(String endpoint, T requestObj, Class<R> responseClass)
            throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "application/json")
            .method("PATCH", requestObj != null
                ? HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestObj))
                : HttpRequest.BodyPublishers.noBody());

        if (UserSession.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
        }

        HttpResponse<String> response = client.send(requestBuilder.build(),
            HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            if (responseClass == Void.class || response.body() == null || response.body().isBlank()) {
                return null;
            }
            return mapper.readValue(response.body(), responseClass);
        } else {
            String body = response.body();
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Request failed with status: " + status);
            }
            Map<String, Object> errorMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            String message = (String) errorMap.getOrDefault("message", "Unknown error");
            throw new RuntimeException(message);
        }
    }

    public String getRaw(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "application/json");

        if (UserSession.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
        }

        HttpRequest request = requestBuilder.GET().build();
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public <T, R> R put(String endpoint, T requestObj, Class<R> responseClass) 
        throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(requestObj);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + endpoint))
            .header("Content-Type", "application/json");

        if (UserSession.getToken() != null) {
            requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
        }

        HttpRequest request = requestBuilder
            .PUT(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            if (response.body() == null || response.body().isBlank()) {
                return null;
            }
            return mapper.readValue(response.body(), responseClass);
        } else {
            String body = response.body();
            if (body == null || body.isBlank()) {
                throw new RuntimeException("Request failed with status: " + status);
            }
            Map<String, Object> errorMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            String message = (String) errorMap.getOrDefault("message", "Unknown error");
            throw new RuntimeException(message);
        }
    }


    
}
