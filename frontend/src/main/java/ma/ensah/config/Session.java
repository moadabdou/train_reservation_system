package ma.ensah.config;

import ma.ensah.model.Schedule;

/**
 * In-memory session holder for the JavaFX app.
 * Stores the JWT token returned by the backend after login/register.
 * Also stores transient UI context like the selected schedule during booking.
 */
public final class Session {
    private static volatile String token;
    private static volatile Schedule selectedSchedule;

    private Session() {}

    public static void setToken(String t) { token = t; }
    public static String getToken() { return token; }
    public static boolean isAuthenticated() { return token != null && !token.isBlank(); }
    public static void clear() { token = null; selectedSchedule = null; }

    public static void setSelectedSchedule(Schedule s) { selectedSchedule = s; }
    public static Schedule getSelectedSchedule() { return selectedSchedule; }
}
