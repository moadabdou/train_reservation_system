package ma.ensah.config;

/**
 * Basic configuration holder. The API base URL can be overridden using
 * -Dapi.baseUrl or the environment variable API_BASE_URL.
 */
public final class Config {
    private static final String DEFAULT_BASE = "http://localhost:8080/api";

    public static String apiBaseUrl() {
        String sys = System.getProperty("api.baseUrl");
        if (sys != null && !sys.isBlank()) return sys;
        String env = System.getenv("API_BASE_URL");
        if (env != null && !env.isBlank()) return env;
        return DEFAULT_BASE;
    }
}
