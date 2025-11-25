package me.ensah.ui.components;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

public class StyledTable<T> extends TableView<T> {

    public StyledTable() {
        super();
        applyDefaultStyles();
        setupColumnResizing();
    }

    private void applyDefaultStyles() {
        // Apply the whiteCard style class
        this.getStyleClass().add("whiteCard");
        this.getStyleClass().add("styled-table");

        // Set default styles
        this.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-control-inner-background: white;" +
                        "-fx-table-cell-border-color: #e5e7eb;" +
                        "-fx-text-fill: #374151;" +
                        "-fx-selection-bar: #fed7aa;" +
                        "-fx-selection-bar-non-focused: #f3f4f6;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;" +
                        "-fx-table-header-border-color: #e5e7eb;" +
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-cell-size: 50px;");

        // Remove focus indicators
        this.setFocusTraversable(false);
    }

    private void setupColumnResizing() {
        // Set column resize policy to distribute space evenly
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Listen for column changes to ensure proper distribution
        this.getColumns().addListener((javafx.collections.ListChangeListener.Change<? extends TableColumn<T, ?>> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    // When columns are added, distribute width evenly
                    distributeColumnWidths();
                }
            }
        });

        // Initial distribution when table is shown
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                distributeColumnWidths();
            }
        });
    }

    private void distributeColumnWidths() {
        if (!this.getColumns().isEmpty()) {
            double totalWidth = this.getWidth();
            if (totalWidth > 0) {
                double columnWidth = totalWidth / this.getColumns().size();
                for (TableColumn<T, ?> column : this.getColumns()) {
                    column.setPrefWidth(columnWidth);
                }
            }
        }
    }
}