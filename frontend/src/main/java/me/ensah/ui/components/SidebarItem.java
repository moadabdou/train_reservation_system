package me.ensah.ui.components;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
// image handling removed for placeholder debug square

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class SidebarItem extends HBox {
    private Label iconLabel;
    private Rectangle placeholder;
    // image view not used when showing placeholder
    private HBox iconContainer; // Add reference to icon container for background effects
    private final Label textLabel;
    private boolean isActive = false;
    private boolean isCollapsed = false;

    // Styles
    private static final String DEFAULT_STYLE = "-fx-background-color: white; -fx-background-radius: 12px; " +
            "-fx-border-color: #f3f4f6; -fx-border-width: 2; -fx-border-radius: 12px; " +
            "-fx-cursor: hand;";

    private static final String ACTIVE_STYLE = "-fx-background-color: #fed7aa; -fx-background-radius: 12px; " +
            "-fx-border-color: #fdba74; -fx-border-width: 2; -fx-border-radius: 12px; " +
            "-fx-cursor: hand;";

    public SidebarItem() {
        this("/icons/dashboard_transparent.png", "Menu Item");
    }

    /**
     * Constructor for SVG/Node-based icons
     */
    public SidebarItem(Node iconNode, String text) {
        super();

        // Setup layout
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(14);
        this.setStyle(DEFAULT_STYLE);
        this.setPrefHeight(50); // Fixed height for consistency

        // Add SVG icon node directly
        if (iconNode != null) {
            this.getChildren().add(iconNode);
        }

        // Text label
        textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #374151;");

        this.getChildren().add(textLabel);

        // Set initial padding (expanded state)
        updatePadding();

        // Hover effects
        setupHoverEffects();
    }

    public SidebarItem(String icon, String text) {
        super();

        // Setup layout
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(14);
        this.setStyle(DEFAULT_STYLE);
        this.setPrefHeight(50); // Fixed height for consistency

        // Icon - Load as image if it's a path, otherwise use as text
        if (icon.endsWith(".png") || icon.endsWith(".jpg")) {
            // For debugging: show a solid black rounded square instead of the real icon
            try {
                // still validate resource exists so we know path is correct
                var resource = SidebarItem.class.getResource(icon);
                if (resource == null) {
                    throw new RuntimeException("Resource not found: " + icon);
                }

                // Create a black rounded square as placeholder
                placeholder = new Rectangle(30, 30);
                placeholder.setArcWidth(6);
                placeholder.setArcHeight(6);
                placeholder.setFill(Color.web("#f97316"));

                this.getChildren().add(placeholder);
            } catch (Exception e) {
                // Fallback to text if resource check fails
                System.err.println("Failed to locate icon resource: " + icon + " - " + e.getMessage());
                e.printStackTrace();
                iconLabel = new Label("!");
                iconLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #ef4444;");
                this.getChildren().add(iconLabel);
            }
        } else {
            // Use as text icon
            iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 22px;");
            this.getChildren().add(iconLabel);
        }

        // Text label
        textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #374151;");

        this.getChildren().add(textLabel);

        // Set initial padding (expanded state)
        updatePadding();

        // Hover effects
        setupHoverEffects();
    }

    private void setupHoverEffects() {
        this.setOnMouseEntered(e -> {
            if (!isActive) {
                this.setStyle(DEFAULT_STYLE.replace("white", "#f9fafb"));
                // Add background to icon when hovered
                if (iconContainer != null) {
                    iconContainer.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 6px;");
                }
            }
        });

        this.setOnMouseExited(e -> {
            if (!isActive) {
                this.setStyle(DEFAULT_STYLE);
                // Remove background from icon when not hovered
                if (iconContainer != null) {
                    iconContainer.setStyle("-fx-background-radius: 6px;");
                }
            }
        });
    }

    private void updatePadding() {
        if (isCollapsed) {
            // Centered padding for collapsed state
            this.setPadding(new javafx.geometry.Insets(12, 12, 12, 12));
            this.setAlignment(Pos.CENTER); // Center everything when collapsed
        } else {
            // Left-aligned padding for expanded state
            this.setPadding(new javafx.geometry.Insets(12, 20, 12, 20));
            this.setAlignment(Pos.CENTER_LEFT); // Back to left alignment
        }
    }

    public void setIcon(String icon) {
        if (iconLabel != null) {
            iconLabel.setText(icon);
        }
    }

    public void setText(String text) {
        textLabel.setText(text);
    }

    public String getIcon() {
        return iconLabel != null ? iconLabel.getText() : "";
    }

    public String getText() {
        return textLabel.getText();
    }

    public void setActive(boolean active) {
        this.isActive = active;
        this.setStyle(active ? ACTIVE_STYLE : DEFAULT_STYLE);

        // Update icon background for active state
        if (iconContainer != null) {
            if (active) {
                iconContainer.setStyle("-fx-background-color: #fdba74; -fx-background-radius: 6px;");
            } else {
                iconContainer.setStyle("-fx-background-radius: 6px;");
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setCollapsed(boolean collapsed) {
        this.isCollapsed = collapsed;

        if (collapsed) {
            // Animate text disappearance
            FadeTransition fade = new FadeTransition(Duration.millis(150), textLabel);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> {
                textLabel.setVisible(false);
                textLabel.setManaged(false); // Remove from layout
            });
            fade.play();

            // Adjust spacing and alignment
            this.setSpacing(0);
            this.setAlignment(Pos.CENTER); // Center icons when collapsed

        } else {
            // Show text with animation
            textLabel.setVisible(true);
            textLabel.setManaged(true); // Add back to layout
            FadeTransition fade = new FadeTransition(Duration.millis(150), textLabel);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();

            // Restore spacing and alignment
            this.setSpacing(14);
            this.setAlignment(Pos.CENTER_LEFT); // Back to left alignment
        }

        // Update padding for current state
        updatePadding();
    }
}