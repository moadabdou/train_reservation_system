package me.ensah.ui.components.unused;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Reusable card component for displaying content
 */
public class Card extends VBox {
    
    private final VBox headerContainer;
    private final VBox contentContainer;
    private final HBox footerContainer;
    private Label titleLabel;
    private Label subtitleLabel;
    
    /**
     * Creates a basic card
     */
    public Card() {
        setPadding(new Insets(20));
        setSpacing(16);
        applyCardStyle();
        
        // Header
        headerContainer = new VBox(8);
        
        // Content
        contentContainer = new VBox(12);
        VBox.setVgrow(contentContainer, Priority.ALWAYS);
        
        // Footer
        footerContainer = new HBox(12);
        footerContainer.setAlignment(Pos.CENTER_RIGHT);
        
        getChildren().add(contentContainer);
    }
    
    /**
     * Applies inline CSS styling to the card
     */
    private void applyCardStyle() {
        String style = "-fx-background-color: white;" +
                      "-fx-background-radius: 12px;" +
                      "-fx-border-color: #e5e7eb;" +
                      "-fx-border-radius: 12px;" +
                      "-fx-border-width: 1px;" +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);";
        setStyle(style);
    }
    
    /**
     * Sets the card title
     */
    public Card setTitle(String title) {
        if (titleLabel == null) {
            titleLabel = new Label(title);
            titleLabel.setStyle("-fx-text-fill: #111827; -fx-font-size: 16px; -fx-font-weight: bold;");
            headerContainer.getChildren().add(0, titleLabel);
            
            if (!getChildren().contains(headerContainer)) {
                getChildren().add(0, headerContainer);
            }
        } else {
            titleLabel.setText(title);
        }
        return this;
    }
    
    /**
     * Sets the card subtitle
     */
    public Card setSubtitle(String subtitle) {
        if (subtitleLabel == null) {
            subtitleLabel = new Label(subtitle);
            subtitleLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");
            headerContainer.getChildren().add(subtitleLabel);
            
            if (!getChildren().contains(headerContainer)) {
                getChildren().add(0, headerContainer);
            }
        } else {
            subtitleLabel.setText(subtitle);
        }
        return this;
    }
    
    /**
     * Adds content to the card
     */
    public Card addContent(Node... nodes) {
        contentContainer.getChildren().addAll(nodes);
        return this;
    }
    
    /**
     * Sets the content (replaces existing)
     */
    public Card setContent(Node... nodes) {
        contentContainer.getChildren().clear();
        contentContainer.getChildren().addAll(nodes);
        return this;
    }
    
    /**
     * Adds a footer button
     */
    public Card addFooterButton(Button button) {
        if (!getChildren().contains(footerContainer)) {
            getChildren().add(footerContainer);
        }
        footerContainer.getChildren().add(button);
        return this;
    }
    
    /**
     * Adds footer content
     */
    public Card addFooter(Node... nodes) {
        if (!getChildren().contains(footerContainer)) {
            getChildren().add(footerContainer);
        }
        footerContainer.getChildren().addAll(nodes);
        return this;
    }
    
    /**
     * Sets card elevation (shadow effect)
     */
    public Card setElevation(int level) {
        switch (level) {
            case 1:
                setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 4, 0, 0, 1);");
                break;
            case 2:
                setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
                break;
            case 3:
                setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 4);");
                break;
            default:
                setStyle("");
        }
        return this;
    }
    
    /**
     * Sets card padding
     */
    public Card setCardPadding(double value) {
        setPadding(new Insets(value));
        return this;
    }
    
    /**
     * Sets card padding
     */
    public Card setCardPadding(double top, double right, double bottom, double left) {
        setPadding(new Insets(top, right, bottom, left));
        return this;
    }
    
    /**
     * Makes the card clickable
     */
    public Card setClickable(Runnable action) {
        setOnMouseClicked(e -> action.run());
        setStyle(getStyle() + "-fx-cursor: hand;");
        
        // Add hover effect
        setOnMouseEntered(e -> {
            setStyle(getStyle() + 
                    "-fx-border-color: #f97316;" +
                    "-fx-effect: dropshadow(gaussian, rgba(249, 115, 22, 0.12), 12, 0, 0, 4);");
        });
        
        setOnMouseExited(e -> applyCardStyle());
        
        return this;
    }
    
    /**
     * Adds a custom style class
     */
    public Card addCustomStyle(String additionalStyle) {
        setStyle(getStyle() + additionalStyle);
        return this;
    }
    
    /**
     * Gets the content container for direct manipulation
     */
    public VBox getContentContainer() {
        return contentContainer;
    }
    
    /**
     * Gets the header container
     */
    public VBox getHeaderContainer() {
        return headerContainer;
    }
    
    /**
     * Gets the footer container
     */
    public HBox getFooterContainer() {
        return footerContainer;
    }
    
    /**
     * Creates a simple card with title and content
     */
    public static Card simple(String title, Node... content) {
        return new Card()
                .setTitle(title)
                .addContent(content);
    }
    
    /**
     * Creates a card with title, subtitle, and content
     */
    public static Card withSubtitle(String title, String subtitle, Node... content) {
        return new Card()
                .setTitle(title)
                .setSubtitle(subtitle)
                .addContent(content);
    }
}
