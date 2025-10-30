package ma.ensah.net;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ma.ensah.config.Session;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper mapper;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule());
    }

    public <T> T get(String path, TypeReference<T> type) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .headers(defaultHeaders())
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 200 && res.statusCode() < 300) {
            return mapper.readValue(res.body(), type);
        }
        throw new IOException("HTTP " + res.statusCode() + " for GET " + path + ": " + res.body());
    }

    public <T> T post(String path, Object body, TypeReference<T> type) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(body);
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .headers(defaultHeaders());

        HttpRequest req = builder.build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 200 && res.statusCode() < 300) {
            if (type == null) return null; // caller doesn't need body
            return mapper.readValue(res.body(), type);
        }
        throw new IOException("HTTP " + res.statusCode() + " for POST " + path + ": " + res.body());
    }

    public void delete(String path) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .headers(defaultHeaders())
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() >= 200 && res.statusCode() < 300) {
            return;
        }
        throw new IOException("HTTP " + res.statusCode() + " for DELETE " + path + ": " + res.body());
    }

    private String[] defaultHeaders() {
        String token = Session.getToken();
        if (token != null && !token.isBlank()) {
            return new String[]{
                    "Accept", "application/json",
                    "Content-Type", "application/json",
                    "Authorization", "Bearer " + token
            };
        }
        return new String[]{
                "Accept", "application/json",
                "Content-Type", "application/json"
        };
    }
}
