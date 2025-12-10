package me.ensah.ui.components.unused;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Reusable custom table component with built-in actions and styling
 */
public class CustomTable<T> extends VBox {
    
    private final TableView<T> tableView;
    private Label emptyMessageLabel;
    private String emptyMessage = "No data available";
    
    /**
     * Creates a custom table
     */
    public CustomTable() {
        this.tableView = new TableView<>();
        initialize();
    }
    
    /**
     * Creates a custom table with data
     */
    public CustomTable(ObservableList<T> items) {
        this.tableView = new TableView<>(items);
        initialize();
    }
    
    private void initialize() {
        applyTableStyle();
        
        // Empty state
        emptyMessageLabel = new Label(emptyMessage);
        emptyMessageLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 14px; -fx-padding: 40;");
        tableView.setPlaceholder(emptyMessageLabel);
        
        VBox.setVgrow(tableView, Priority.ALWAYS);
        getChildren().add(tableView);
    }
    
    /**
     * Applies inline CSS styling to the table
     */
    private void applyTableStyle() {
        String style = "-fx-background-color: white;" +
                      "-fx-border-color: #e5e7eb;" +
                      "-fx-border-radius: 8px;" +
                      "-fx-background-radius: 8px;";
        tableView.setStyle(style);
    }
    
    /**
     * Adds a text column to the table
     */
    public CustomTable<T> addColumn(String title, String propertyName) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        tableView.getColumns().add(column);
        return this;
    }
    
    /**
     * Adds a text column with custom value extractor
     */
    public CustomTable<T> addColumn(String title, Function<T, String> valueExtractor) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(cellData -> 
            new SimpleStringProperty(valueExtractor.apply(cellData.getValue()))
        );
        tableView.getColumns().add(column);
        return this;
    }
    
    /**
     * Adds a column with custom cell factory
     */
    public <S> CustomTable<T> addColumn(String title, String propertyName, 
                                        Callback<TableColumn<T, S>, TableCell<T, S>> cellFactory) {
        TableColumn<T, S> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(cellFactory);
        tableView.getColumns().add(column);
        return this;
    }
    
    /**
     * Adds an action column with buttons
     */
    public CustomTable<T> addActionColumn(String title, Consumer<T> onEdit, Consumer<T> onDelete) {
        TableColumn<T, Void> column = new TableColumn<>(title);
        
        Callback<TableColumn<T, Void>, TableCell<T, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(8);
            
            {
                editBtn.setStyle("-fx-padding: 4 12; -fx-font-size: 12px; " +
                               "-fx-background-color: transparent; -fx-text-fill: #374151; " +
                               "-fx-border-color: #d1d5db; -fx-border-width: 1.5; " +
                               "-fx-border-radius: 6; -fx-background-radius: 6; " +
                               "-fx-font-weight: 600; -fx-cursor: hand;");
                
                deleteBtn.setStyle("-fx-padding: 4 12; -fx-font-size: 12px; " +
                                 "-fx-background-color: #ef4444; -fx-text-fill: white; " +
                                 "-fx-font-weight: 600; -fx-background-radius: 6; " +
                                 "-fx-cursor: hand;");
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(editBtn, deleteBtn);
                
                editBtn.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    if (onEdit != null) onEdit.accept(item);
                });
                
                deleteBtn.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    if (onDelete != null) onDelete.accept(item);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        };
        
        column.setCellFactory(cellFactory);
        tableView.getColumns().add(column);
        return this;
    }
    
    /**
     * Adds a single action button column
     */
    public CustomTable<T> addActionColumn(String title, String buttonText, 
                                         Consumer<T> onAction, String buttonStyle) {
        TableColumn<T, Void> column = new TableColumn<>(title);
        
        Callback<TableColumn<T, Void>, TableCell<T, Void>> cellFactory = param -> new TableCell<>() {
            private final Button actionBtn = new Button(buttonText);
            
            {
                // Apply style based on buttonStyle parameter
                String style = "-fx-padding: 4 12; -fx-font-size: 12px; -fx-font-weight: 600; " +
                             "-fx-background-radius: 6; -fx-cursor: hand;";
                
                if ("btn-primary".equals(buttonStyle)) {
                    style += "-fx-background-color: #f97316; -fx-text-fill: white;";
                } else if ("btn-danger".equals(buttonStyle)) {
                    style += "-fx-background-color: #ef4444; -fx-text-fill: white;";
                } else if ("btn-secondary".equals(buttonStyle)) {
                    style += "-fx-background-color: transparent; -fx-text-fill: #374151; " +
                           "-fx-border-color: #d1d5db; -fx-border-width: 1.5; -fx-border-radius: 6;";
                }
                
                actionBtn.setStyle(style);
                
                actionBtn.setOnAction(event -> {
                    T item = getTableView().getItems().get(getIndex());
                    if (onAction != null) onAction.accept(item);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBtn);
                }
            }
        };
        
        column.setCellFactory(cellFactory);
        tableView.getColumns().add(column);
        return this;
    }
    
    /**
     * Sets the items in the table
     */
    public CustomTable<T> setItems(ObservableList<T> items) {
        tableView.setItems(items);
        return this;
    }
    
    /**
     * Gets the items from the table
     */
    public ObservableList<T> getItems() {
        return tableView.getItems();
    }
    
    /**
     * Gets the underlying TableView
     */
    public TableView<T> getTableView() {
        return tableView;
    }
    
    /**
     * Sets the empty message
     */
    public CustomTable<T> setEmptyMessage(String message) {
        this.emptyMessage = message;
        emptyMessageLabel.setText(message);
        return this;
    }
    
    /**
     * Sets selection mode
     */
    public CustomTable<T> setSelectionMode(SelectionMode mode) {
        tableView.getSelectionModel().setSelectionMode(mode);
        return this;
    }
    
    /**
     * Gets selected item
     */
    public T getSelectedItem() {
        return tableView.getSelectionModel().getSelectedItem();
    }
    
    /**
     * Gets selected items
     */
    public ObservableList<T> getSelectedItems() {
        return tableView.getSelectionModel().getSelectedItems();
    }
    
    /**
     * Sets a callback for row selection
     */
    public CustomTable<T> onRowSelect(Consumer<T> callback) {
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null && callback != null) {
                    callback.accept(newVal);
                }
            }
        );
        return this;
    }
    
    /**
     * Refreshes the table
     */
    public void refresh() {
        tableView.refresh();
    }
    
    /**
     * Clears the table
     */
    public void clear() {
        tableView.getItems().clear();
    }
}
