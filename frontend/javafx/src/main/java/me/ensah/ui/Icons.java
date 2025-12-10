package me.ensah.ui;

/**
 * Professional SVG-based icons for the TrainLink application.
 * All icons are stored as SVG files in the resources/icons directory.
 */
public class Icons {

    private static final String ICON_PATH = "/icons/";

    // Navigation Icons (SVG format)
    public static final String DASHBOARD = ICON_PATH + "dashboard.svg";
    public static final String USERS = ICON_PATH + "users.svg";
    public static final String TRAINS = ICON_PATH + "trains.svg";
    public static final String SCHEDULES = ICON_PATH + "schedules.svg";
    public static final String BOOKINGS = ICON_PATH + "bookings.svg";
    public static final String LOGOUT = ICON_PATH + "logout.svg";

    // Header Icons
    public static final String TRAIN_LOGO = "T"; // TrainLink logo - Letter T
    public static final String MENU_TOGGLE = "☰"; // Menu - Hamburger icon

    /**
     * Get size recommendation for icons
     */
    public enum IconSize {
        SMALL(14),
        MEDIUM(16),
        LARGE(20),
        XLARGE(24),
        XXLARGE(32),
        HEADER(48);

        private final int size;

        IconSize(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }

        public String getStyle() {
            return "-fx-font-size: " + size + "px;";
        }
    }

    /**
     * Load SVG icon from resources
     * 
     * @param iconPath Path to the SVG file (e.g., "/icons/dashboard.svg")
     * @return File URL for loading in JavaFX
     */
    public static String loadSvgIcon(String iconPath) {
        return Icons.class.getResource(iconPath).toExternalForm();
    }
}
