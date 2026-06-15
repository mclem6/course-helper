package com.coursehelper.frontend.service.api;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.coursehelper.frontend.UserSession;
import com.coursehelper.frontend.exceptions.ApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public ApiClient() {
        client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public <T, R> R post(String endpoint, T requestObj, Class<R> responseClass) {
        try {
            String json = mapper.writeValueAsString(requestObj);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");

            if (UserSession.getToken() != null) {
                requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
            }

            HttpResponse<String> response = client.send(
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(json)).build(),
                HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                if (responseClass == String.class) return responseClass.cast(response.body());
                return mapper.readValue(response.body(), responseClass);
            }
            throw parseError(response.body(), status);

        } catch (ApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (IOException e) {
            throw new ApiException("Connection error.", 503);
        }
    }

    public <T> T get(String endpoint, Class<T> clazz) {
        String body = handleResponse(executeGet(endpoint));
        if (body == null) return null;
        try {
            return mapper.readValue(body, clazz);
        } catch (IOException e) {
            throw new ApiException("Failed to parse response.", 500);
        }
    }

    public <T> T get(String endpoint, TypeReference<T> typeRef) {
        String body = handleResponse(executeGet(endpoint));
        if (body == null) return null;
        try {
            return mapper.readValue(body, typeRef);
        } catch (IOException e) {
            throw new ApiException("Failed to parse response.", 500);
        }
    }

    public String postFile(String endpoint, File file) {
        try {
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

            HttpResponse<String> response = client.send(
                requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(body)).build(),
                HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status >= 200 && status < 300) return response.body();
            throw parseError(response.body(), status);

        } catch (ApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (IOException e) {
            throw new ApiException("Connection error.", 503);
        }
    }

    public byte[] getBytes(String endpoint) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");

            if (UserSession.getToken() != null) {
                requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
            }

            HttpResponse<byte[]> response = client.send(
                requestBuilder.GET().build(),
                HttpResponse.BodyHandlers.ofByteArray());

            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                byte[] body = response.body();
                return (body == null || body.length == 0) ? null : body;
            }
            throw new ApiException("Download failed with status: " + status, status);

        } catch (ApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (IOException e) {
            throw new ApiException("Connection error.", 503);
        }
    }

    public void delete(String endpoint) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");

            if (UserSession.getToken() != null) {
                requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
            }

            HttpResponse<String> response = client.send(
                requestBuilder.DELETE().build(),
                HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status < 200 || status >= 300) throw parseError(response.body(), status);

        } catch (ApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (IOException e) {
            throw new ApiException("Connection error.", 503);
        }
    }

    public <T, R> R patch(String endpoint, T requestObj, Class<R> responseClass) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .method("PATCH", requestObj != null
                    ? HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestObj))
                    : HttpRequest.BodyPublishers.noBody());

            if (UserSession.getToken() != null) {
                requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
            }

            HttpResponse<String> response = client.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                if (responseClass == Void.class || response.body() == null || response.body().isBlank()) return null;
                return mapper.readValue(response.body(), responseClass);
            }
            throw parseError(response.body(), status);

        } catch (ApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (IOException e) {
            throw new ApiException("Connection error.", 503);
        }
    }

    public <T, R> R put(String endpoint, T requestObj, Class<R> responseClass) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");

            if (UserSession.getToken() != null) {
                requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
            }

            HttpResponse<String> response = client.send(
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestObj))).build(),
                HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status >= 200 && status < 300) {
                if (response.body() == null || response.body().isBlank()) return null;
                if (responseClass == String.class) return responseClass.cast(response.body());
                return mapper.readValue(response.body(), responseClass);
            }
            throw parseError(response.body(), status);

        } catch (ApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (IOException e) {
            throw new ApiException("Connection error.", 503);
        }
    }

    public String getRaw(String endpoint) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");

            if (UserSession.getToken() != null) {
                requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
            }

            return client.send(requestBuilder.GET().build(),
                HttpResponse.BodyHandlers.ofString()).body();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (IOException e) {
            throw new ApiException("Connection error.", 503);
        }
    }

    private HttpResponse<String> executeGet(String endpoint) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json");

            if (UserSession.getToken() != null) {
                requestBuilder.header("Authorization", "Bearer " + UserSession.getToken());
            }

            return client.send(requestBuilder.GET().build(),
                HttpResponse.BodyHandlers.ofString());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ApiException("Request interrupted.", 503);
        } catch (IOException e) {
            throw new ApiException("Connection error.", 503);
        }
    }

    private String handleResponse(HttpResponse<String> response) {
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
            String body = response.body();
            return (body == null || body.isBlank()) ? null : body;
        }
        throw parseError(response.body(), status);
    }

    private ApiException parseError(String body, int status) {
        if (body == null || body.isBlank()) {
            return new ApiException("Request failed with status: " + status, status);
        }
        try {
            Map<String, Object> errorMap = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            Object msg = errorMap.getOrDefault("error", errorMap.get("message"));
            return new ApiException(msg != null ? msg.toString() : "Unknown error", status);
        } catch (IOException e) {
            return new ApiException("Request failed with status: " + status, status);
        }
    }
}
