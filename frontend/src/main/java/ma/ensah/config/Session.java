package ma.ensah.config;

/**
 * In-memory session holder for the JavaFX app.
 * Stores the JWT token returned by the backend after login/register.
 */
public final class Session {
    private static volatile String token;

    private Session() {}

    public static void setToken(String t) { token = t; }
    public static String getToken() { return token; }
    public static boolean isAuthenticated() { return token != null && !token.isBlank(); }
    public static void clear() { token = null; }
}
