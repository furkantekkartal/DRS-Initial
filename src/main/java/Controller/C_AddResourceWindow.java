package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller class for the Add Resource Window. Handles the addition of new
 * resources to the DRC system.
 * 
 * @author 12223508
 */
public class C_AddResourceWindow {

    @FXML
    private TextField resourceNameField;

    @FXML
    private TextField resourceQuantityField;

    @FXML
    private TextArea resourceDescriptionArea;

    private C_Coordinator drcController;

    /**
     * Sets the DRC Controller reference.
     *
     * @param drcController The C_Coordinator instance to set
     */
    public void setDRCController(C_Coordinator drcController) {
        this.drcController = drcController;
    }

    /**
     * Handles the action of adding a new resource. Validates input, formats the
     * resource information, and adds it to the DRC.
     */
    @FXML
    private void addResource() {
        String resourceName = resourceNameField.getText();
        String resourceQuantity = resourceQuantityField.getText();
        String resourceDescription = resourceDescriptionArea.getText();

        if (!resourceName.isEmpty() && !resourceQuantity.isEmpty()) {
            String formattedResource = formatResourceInfo(resourceName, resourceQuantity, resourceDescription);
            drcController.addResourceNeeded(formattedResource);
            closeWindow();
        }
    }

    /**
     * Handles the action of canceling the resource addition.
     */
    @FXML
    private void cancel() {
        closeWindow();
    }

    /**
     * Formats the resource information with a timestamp.
     *
     * @param name The name of the resource
     * @param quantity The quantity of the resource
     * @param description The description of the resource
     * @return A formatted string containing the resource information
     */
    private String formatResourceInfo(String name, String quantity, String description) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String timestamp = now.format(formatter);
        return String.format("[%s] Resource Added: %s (Quantity: %s)\nDescription: %s",
                timestamp, name, quantity, description);
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        ((Stage) resourceNameField.getScene().getWindow()).close();
    }
}
