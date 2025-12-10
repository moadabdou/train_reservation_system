package me.ensah.ui.components.unused;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

/**
 * Reusable loading spinner component
 */
public class LoadingSpinner extends VBox {
    
    private final ProgressIndicator spinner;
    private final Label messageLabel;
    
    /**
     * Creates a loading spinner with default message
     */
    public LoadingSpinner() {
        this("Loading...");
    }
    
    /**
     * Creates a loading spinner with custom message
     */
    public LoadingSpinner(String message) {
        setAlignment(Pos.CENTER);
        setSpacing(16);
        applySpinnerStyle();
        
        spinner = new ProgressIndicator();
        spinner.setStyle("-fx-progress-color: #f97316;");
        
        messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px;");
        
        getChildren().addAll(spinner, messageLabel);
    }
    
    /**
     * Applies inline CSS styling to the spinner container
     */
    private void applySpinnerStyle() {
        String style = "-fx-alignment: center; -fx-padding: 40;";
        setStyle(style);
    }
    
    /**
     * Sets the loading message
     */
    public LoadingSpinner setMessage(String message) {
        messageLabel.setText(message);
        return this;
    }
    
    /**
     * Shows the message label
     */
    public LoadingSpinner showMessage(boolean show) {
        messageLabel.setVisible(show);
        messageLabel.setManaged(show);
        return this;
    }
    
    /**
     * Sets the spinner size
     */
    public LoadingSpinner setSize(double size) {
        spinner.setPrefSize(size, size);
        return this;
    }
    
    /**
     * Creates a small loading spinner
     */
    public static LoadingSpinner small() {
        LoadingSpinner spinner = new LoadingSpinner();
        spinner.setSize(30);
        return spinner;
    }
    
    /**
     * Creates a medium loading spinner
     */
    public static LoadingSpinner medium() {
        LoadingSpinner spinner = new LoadingSpinner();
        spinner.setSize(50);
        return spinner;
    }
    
    /**
     * Creates a large loading spinner
     */
    public static LoadingSpinner large() {
        LoadingSpinner spinner = new LoadingSpinner();
        spinner.setSize(80);
        return spinner;
    }
}
