package me.ensah.net;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import me.ensah.config.Session;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
    private final String baseUrl;//in our case it is the backend server address
    private final HttpClient http;// to send http requests
    private final ObjectMapper mapper; // to convert java objects to json and vice versa

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;// remove trailing slash if any
        this.http = HttpClient.newBuilder()// create http client we dont have to create it every time we make a request
                .connectTimeout(Duration.ofSeconds(5))// set connection timeout which is the maximum time to wait for a connection to be established
                .build();
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)// ignore unknown properties in json responses
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
        // STEP 1: Convert Java object to JSON string
        // Example: {email: "user@test.com", password: "pass123"} → '{"email":"user@test.com","password":"pass123"}'
        String json = mapper.writeValueAsString(body);
        
        // STEP 2: Build HTTP POST request
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))  // Full URL: http://localhost:8080/api/v1/auth/login
                .timeout(Duration.ofSeconds(10))   // Max wait time: 10 seconds
                .POST(HttpRequest.BodyPublishers.ofString(json))  // Attach JSON body
                .headers(defaultHeaders());  // Add headers (Content-Type, Authorization if token exists)

        // STEP 3: Send request to backend server
        HttpRequest req = builder.build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        
        // STEP 4: Check if request was successful (status code 200-299)
        if (res.statusCode() >= 200 && res.statusCode() < 300) {
            if (type == null) return null; // caller doesn't need response body
            
            // STEP 5: Convert JSON response back to Java object
            // Example: '{"token":"eyJhbGc..."}' → TokenResponse object
            return mapper.readValue(res.body(), type);
        }
        
        // STEP 6: If request failed, throw exception with error details
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

//  apiClient  class is a simple HTTP client for making requests to a RESTful API. It supports GET, POST, and DELETE methods, handles JSON serialization/deserialization using Jackson, and includes authorization headers if a token is set in the Session.
// HTTP client is like a bridge that allows your application to communicate with external web services or APIs over the internet.
//in our case it helps the frontend to communicate with the backend server. useing HTTP requests.
// from scratch :
//http client is created using java.net.http.HttpClient
//http is a built-in library in java that provides a way to send HTTP requests and receive HTTP responses from a resource identified by a URI.

//for a better understanding of the code please from scratch read about HTTP protocol , RESTful APIs and JSON format.
//http protocol : https://developer.mozilla.org/en-US/docs/Web/HTTP/Overview
//RESTful APIs : https://restfulapi.net/
//JSON format : https://www.json.org/json-en.html


//http protocol is the foundation of data communication on the web. It defines how messages are formatted and transmitted, and how web servers and browsers should respond to various commands.
//RESTful APIs are a way to provide interoperability between computer systems on the internet. They use HTTP requests to perform CRUD (Create, Read, Update, Delete) operations on resources, which are typically represented in JSON format.
//JSON (JavaScript Object Notation) is a lightweight data interchange format that is easy for humans to read and write, and easy for machines to parse and generate. It is commonly used in web applications to transmit data between a server and a client.


// to be more detailed :
//http protocol is a request-response protocol that operates on a client-server model. The client sends an HTTP request to the server, which processes the request and sends back an HTTP response. The request and response messages consist of a start line, headers, and an optional body.
// we see in the seach bar of the navigator when we visit a website : https://www.example.com/index.html
//https is the protocol
//www.example.com is the domain name (server address)
///index.html is the resource path (specific resource on the server)
/// here https is a secure version of http that uses encryption to protect data transmitted between the client and server.
/// resful APIs use standard HTTP methods to perform operations on resources. The most common methods are GET (retrieve a resource), POST (create a new resource), PUT (update an existing resource), and DELETE (remove a resource).
// Resources are identified by URIs (Uniform Resource Identifiers), which are similar to URLs (Uniform Resource Locators) but can also include non-web resources.
// JSON is a text-based format that represents data as key-value pairs. It supports various data types, including strings, numbers, booleans, arrays, and objects. JSON is often used in RESTful APIs to represent resources and exchange

// HttpClient is a class that represents an HTTP client that can send requests and receive responses.
// HttpRequest is a class that represents an HTTP request that can be sent by an HttpClient
// HttpResponse is a class that represents an HTTP response that can be received by an HttpClient