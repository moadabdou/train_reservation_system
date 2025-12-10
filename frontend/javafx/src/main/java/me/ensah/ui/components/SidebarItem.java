package me.ensah.ui.components;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class SidebarItem extends HBox {
    private Label iconLabel;
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
        this("/icons/dashboard.svg", "Menu Item");
    }

    public SidebarItem(String icon, String text) {
        super();

        // Setup layout
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(14);
        this.setStyle(DEFAULT_STYLE);
        this.setPrefHeight(50); // Fixed height for consistency

        // Text label
        textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 600; -fx-text-fill: #374151;");

        // Load Icon
        updateIcon(icon);

        // Add text label if not already added by updateIcon (it shouldn't be)
        if (!this.getChildren().contains(textLabel)) {
            this.getChildren().add(textLabel);
        }

        // Set initial padding (expanded state)
        updatePadding();

        // Hover effects
        setupHoverEffects();
    }

    public void setIcon(String icon) {
        updateIcon(icon);
    }

    private void updateIcon(String icon) {
        // Remove existing icon (it's the first child if present, before textLabel)
        if (!this.getChildren().isEmpty() && this.getChildren().contains(textLabel)) {
            int textIndex = this.getChildren().indexOf(textLabel);
            if (textIndex > 0) {
                this.getChildren().remove(0, textIndex);
            }
        } else if (!this.getChildren().isEmpty() && textLabel == null) {
            this.getChildren().clear();
        } else if (!this.getChildren().isEmpty() && !this.getChildren().contains(textLabel)) {
            this.getChildren().clear();
        }

        // Load new icon
        if (icon.endsWith(".svg") || icon.endsWith(".png") || icon.endsWith(".jpg")) {
            try {
                String iconPath = icon.startsWith("/") ? icon : "/" + icon;
                java.net.URL resource = getClass().getResource(iconPath);

                if (resource == null) {
                    String relativePath = iconPath.substring(1);
                    resource = Thread.currentThread().getContextClassLoader().getResource(relativePath);
                }

                if (resource == null) {
                    resource = SidebarItem.class.getModule().getClassLoader().getResource(iconPath.substring(1));
                }

                if (resource == null) {
                    throw new RuntimeException("Resource not found: " + icon);
                }

                if (icon.endsWith(".svg")) {
                    javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
                    webView.setPrefSize(24, 24);
                    webView.setMaxSize(24, 24);
                    webView.setMinSize(24, 24);
                    webView.setPageFill(javafx.scene.paint.Color.TRANSPARENT);
                    webView.setMouseTransparent(true);
                    webView.setDisable(true); // Ensure it doesn't capture any events
                    webView.getEngine().load(resource.toExternalForm());
                    this.getChildren().add(0, webView);
                    iconLabel = null;
                } else {
                    Image image = new Image(resource.toExternalForm());
                    if (image.isError()) {
                        throw new RuntimeException("Failed to load image data: " + image.getException());
                    }
                    ImageView iconView = new ImageView(image);
                    iconView.setFitWidth(22);
                    iconView.setFitHeight(22);
                    iconView.setPreserveRatio(true);
                    this.getChildren().add(0, iconView);
                    iconLabel = null;
                }
            } catch (Exception e) {
                System.err.println("Failed to load image: " + icon + " - " + e.getMessage());
                iconLabel = new Label("!");
                iconLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #ef4444;");
                this.getChildren().add(0, iconLabel);
            }
        } else {
            iconLabel = new Label(icon);
            iconLabel.setStyle("-fx-font-size: 22px;");
            this.getChildren().add(0, iconLabel);
        }
    }

    private void setupHoverEffects() {
        this.setOnMouseEntered(e -> {
            if (!isActive) {
                this.setStyle(DEFAULT_STYLE.replace("white", "#f9fafb"));
            }
        });

        this.setOnMouseExited(e -> {
            if (!isActive) {
                this.setStyle(DEFAULT_STYLE);
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