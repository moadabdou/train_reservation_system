package ma.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;
import ma.ensah.config.Config;
import ma.ensah.config.Session;
import ma.ensah.model.TokenResponse;
import ma.ensah.net.ApiClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final ApiClient api;

    public AuthService(ApiClient api) {
        this.api = api;
    }

    public static AuthService defaultInstance() {
        return new AuthService(new ApiClient(Config.apiBaseUrl()));
    }

    public String register(String name, String email, String password) throws IOException, InterruptedException {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);
        TokenResponse res = api.post("/auth/register", body, new TypeReference<TokenResponse>(){});
        if (res != null && res.getToken() != null) {
            Session.setToken(res.getToken());
            return res.getToken();
        }
        throw new IOException("No token returned from register");
    }

    public String login(String email, String password) throws IOException, InterruptedException {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        TokenResponse res = api.post("/auth/login", body, new TypeReference<TokenResponse>(){});
        if (res != null && res.getToken() != null) {
            Session.setToken(res.getToken());
            return res.getToken();
        }
        throw new IOException("No token returned from login");
    }
}
