package me.ensah.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * PageHeaderController - Controller for the PageHeader FXML component
 * Allows setting title and subtitle programmatically
 */
public class PageHeaderController extends VBox {

    // @FXML
    // private Rectangle placeholderIcon;
    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    public void initialize() {
    }

    /**
     * Set the title of the page header
     */
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }

    /**
     * Set the subtitle of the page header
     */
    public void setSubtitle(String subtitle) {
        if (subtitleLabel != null) {
            subtitleLabel.setText(subtitle);
        }
    }

    /**
     * Get the title label
     */
    public Label getTitleLabel() {
        return titleLabel;
    }

    /**
     * Get the subtitle label
     */
    public Label getSubtitleLabel() {
        return subtitleLabel;
    }
}
