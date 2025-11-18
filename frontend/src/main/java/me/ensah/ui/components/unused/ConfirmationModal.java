package me.ensah.ui.components.unused;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Reusable confirmation modal for user confirmations
 */
public class ConfirmationModal {
    
    public enum ModalType {
        INFO(Alert.AlertType.INFORMATION),
        WARNING(Alert.AlertType.WARNING),
        ERROR(Alert.AlertType.ERROR),
        CONFIRMATION(Alert.AlertType.CONFIRMATION);
        
        private final Alert.AlertType alertType;
        
        ModalType(Alert.AlertType alertType) {
            this.alertType = alertType;
        }
        
        public Alert.AlertType getAlertType() {
            return alertType;
        }
    }
    
    private final Alert alert;
    private String title;
    private String headerText;
    private String contentText;
    private ModalType modalType;
    
    /**
     * Creates a confirmation modal with default type
     */
    public ConfirmationModal() {
        this(ModalType.CONFIRMATION);
    }
    
    /**
     * Creates a modal with specified type
     */
    public ConfirmationModal(ModalType type) {
        this.modalType = type;
        this.alert = new Alert(type.getAlertType());
        this.alert.initModality(Modality.APPLICATION_MODAL);
    }
    
    /**
     * Sets the title of the modal
     */
    public ConfirmationModal title(String title) {
        this.title = title;
        this.alert.setTitle(title);
        return this;
    }
    
    /**
     * Sets the header text of the modal
     */
    public ConfirmationModal header(String headerText) {
        this.headerText = headerText;
        this.alert.setHeaderText(headerText);
        return this;
    }
    
    /**
     * Sets the content text of the modal
     */
    public ConfirmationModal content(String contentText) {
        this.contentText = contentText;
        this.alert.setContentText(contentText);
        return this;
    }
    
    /**
     * Sets the modal type
     */
    public ConfirmationModal type(ModalType type) {
        this.modalType = type;
        this.alert.setAlertType(type.getAlertType());
        return this;
    }
    
    /**
     * Sets the owner window
     */
    public ConfirmationModal owner(Window owner) {
        this.alert.initOwner(owner);
        return this;
    }
    
    /**
     * Sets custom button types
     */
    public ConfirmationModal buttons(ButtonType... buttonTypes) {
        this.alert.getButtonTypes().setAll(buttonTypes);
        return this;
    }
    
    /**
     * Shows the modal and waits for user response
     */
    public Optional<ButtonType> showAndWait() {
        return alert.showAndWait();
    }
    
    /**
     * Shows the modal and executes callback on confirmation
     */
    public void showAndWait(Runnable onConfirm) {
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
            onConfirm.run();
        }
    }
    
    /**
     * Shows the modal and executes callbacks based on user response
     */
    public void showAndWait(Runnable onConfirm, Runnable onCancel) {
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                onConfirm.run();
            } else {
                if (onCancel != null) onCancel.run();
            }
        }
    }
    
    /**
     * Shows the modal and executes callback with the button result
     */
    public void showAndWait(Consumer<ButtonType> callback) {
        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(callback);
    }
    
    // ============= Static Factory Methods =============
    
    /**
     * Creates and shows a simple confirmation dialog
     */
    public static boolean confirm(String title, String content) {
        ConfirmationModal modal = new ConfirmationModal(ModalType.CONFIRMATION)
                .title(title)
                .content(content);
        
        Optional<ButtonType> result = modal.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Creates and shows a confirmation dialog with custom message
     */
    public static boolean confirm(String title, String header, String content) {
        ConfirmationModal modal = new ConfirmationModal(ModalType.CONFIRMATION)
                .title(title)
                .header(header)
                .content(content);
        
        Optional<ButtonType> result = modal.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Shows an information dialog
     */
    public static void info(String title, String content) {
        new ConfirmationModal(ModalType.INFO)
                .title(title)
                .header(null)
                .content(content)
                .buttons(ButtonType.OK)
                .showAndWait();
    }
    
    /**
     * Shows a warning dialog
     */
    public static void warning(String title, String content) {
        new ConfirmationModal(ModalType.WARNING)
                .title(title)
                .header(null)
                .content(content)
                .buttons(ButtonType.OK)
                .showAndWait();
    }
    
    /**
     * Shows an error dialog
     */
    public static void error(String title, String content) {
        new ConfirmationModal(ModalType.ERROR)
                .title(title)
                .header(null)
                .content(content)
                .buttons(ButtonType.OK)
                .showAndWait();
    }
    
    /**
     * Shows a delete confirmation dialog
     */
    public static boolean confirmDelete(String itemName) {
        return confirm(
                "Confirm Delete",
                "Delete " + itemName + "?",
                "This action cannot be undone. Are you sure you want to delete this " + itemName + "?"
        );
    }
    
    /**
     * Shows a cancel confirmation dialog
     */
    public static boolean confirmCancel(String actionName) {
        return confirm(
                "Confirm Cancel",
                "Cancel " + actionName + "?",
                "Any unsaved changes will be lost. Are you sure you want to cancel?"
        );
    }
}
