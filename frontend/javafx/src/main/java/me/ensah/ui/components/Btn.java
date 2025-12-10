package me.ensah.ui.components;

import javafx.scene.control.Button;
import me.ensah.ui.helpers.Color;

/// Reusable custom button component with different styles and types
public class Btn extends Button {
    private String type = "PRIMARY";
    private String size = "MEDIUM";
    private boolean loading = false;

    // No-argument constructor for FXML
    public Btn() {
        super();
        // Don't apply style here - wait for FXML to set properties
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyButtonStyle();
            }
        });
    }

    public Btn(String text, String type, String size) {
        super(text);
        this.type = type;
        this.size = size;
        applyButtonStyle();
    }

    // Apply button styles
    private void applyButtonStyle() {
        String baseStyle = buildStyle();
        setStyle(baseStyle);

        // Setup hover effects once
        setOnMouseEntered(e -> {
            if ("PRIMARY".equals(type)) {
                setStyle(buildStyle().replace(Color.PRIMARY, Color.PRIMARY_HOVER)
                        .replace("0.3), 8", "0.4), 10"));
            } else if ("SECONDARY".equals(type)) {
                setStyle(buildStyle().replace("white", "#fef2f2")
                        .replace("#fecaca", "#f87171")
                        .replace("0.1), 6", "0.15), 8"));
            }
        });

        setOnMouseExited(e -> setStyle(buildStyle()));
    }

    // Build the style string
    private String buildStyle() {
        StringBuilder s = new StringBuilder();

        // Base
        s.append("-fx-font-family: 'Segoe UI', Arial, sans-serif;");
        s.append("-fx-background-radius: 6px;");
        s.append("-fx-cursor: hand;");
        s.append("-fx-font-weight: 600;");

        // Type
        if ("PRIMARY".equals(type)) {
            s.append("-fx-background-color: ").append(Color.PRIMARY).append(";");
            s.append("-fx-text-fill: white;");
            s.append("-fx-effect: dropshadow(gaussian, rgba(249, 115, 22, 0.3), 8, 0, 0, 2);");
        } else if ("SECONDARY".equals(type)) {
            s.append("-fx-background-color: white;");
            s.append("-fx-text-fill: #dc2626;");
            s.append("-fx-border-color: #fecaca;");
            s.append("-fx-border-width: 2px;");
            s.append("-fx-border-radius: 6px;");
            s.append("-fx-effect: dropshadow(gaussian, rgba(220, 38, 38, 0.1), 6, 0, 0, 2);");
        }

        // Size
        if ("SMALL".equals(size)) {
            s.append("-fx-padding: 4px 12px; -fx-font-size: 12px;");
        } else if ("MEDIUM".equals(size)) {
            s.append("-fx-padding: 6px 16px; -fx-font-size: 13px;");
        } else if ("LARGE".equals(size)) {
            s.append("-fx-padding: 10px 24px; -fx-font-size: 15px;");
        }

        return s.toString();
    }

    // setters:
    public void setType(String type) {
        this.type = type;
        applyButtonStyle();
    }

    public void setSize(String size) {
        this.size = size;
        applyButtonStyle();
    }

    // getters
    public String getType() {
        return type;
    }

    public String getSize() {
        return size;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        setDisable(loading);

        if (loading) {
            setText("Loading...");
        }

    }

    public boolean isLoading() {
        return loading;
    }

    /// Sets the button to occupy full width of its container
    public void setFullWidth(boolean fullWidth) {
        if (fullWidth) {
            setMaxWidth(Double.MAX_VALUE);
        }
    }
}