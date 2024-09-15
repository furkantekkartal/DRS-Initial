package Controller;

import Util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller class for reporting disasters. This class handles the UI and logic
 * for submitting disaster reports.
 *
 * @author 12223508
 */
public class C_ReportDisaster {

    // FXML annotated fields
    @FXML
    public VBox mainContainer;
    @FXML
    public ComboBox<String> disasterTypeComboBox;
    @FXML
    public TextField locationField;
    @FXML
    public DatePicker datePicker;
    @FXML
    public TextField reporterNameField;
    @FXML
    public TextField contactInfoField;
    @FXML
    public VBox specificFieldsContainer;

    // Wildfire specific fields
    public ComboBox<String> intensityComboBox;
    public TextField areaField;
    public TextField infrastructureField;

    // Hurricane specific fields
    public TextField windSpeedField;
    public CheckBox floodRiskCheckBox;
    public ComboBox<String> evacuationStatusComboBox;

    // Earthquake specific fields
    public TextField magnitudeField;
    public TextField depthField;
    public CheckBox aftershocksCheckBox;

    // Flood specific fields
    public ComboBox<String> waterLevelComboBox;
    public ComboBox<String> floodEvacuationStatusComboBox;
    public TextField infrastructureDamageField;

    // Landslide specific fields
    public ComboBox<String> slopeStabilityComboBox;
    public TextField blockedRoadsField;
    public TextField casualtiesField;

    // Other disaster specific fields
    public TextArea disasterDescriptionArea;
    public ComboBox<String> impactComboBox;

    public Stage stage;

    /**
     * Initializes the controller. This method is automatically called after the
     * FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        disasterTypeComboBox.getItems().addAll("Wildfire", "Hurricane", "Earthquake", "Flood", "Landslide", "Other");

        contactInfoField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (contactInfoField.isFocused()) {
                try {
                    String filtered = newValue.replaceAll("[^\\d]", "");
                    if (filtered.length() > 10) {
                        filtered = filtered.substring(0, 10);
                    }

                    String formatted;
                    if (filtered.length() == 10) {
                        formatted = String.format("%s %s %s",
                                filtered.substring(0, 4),
                                filtered.substring(4, 7),
                                filtered.substring(7, 10));
                    } else {
                        formatted = filtered;
                    }

                    if (!formatted.equals(newValue)) {
                        Platform.runLater(() -> {
                            contactInfoField.setText(formatted);
                            contactInfoField.positionCaret(formatted.length());
                        });
                    }
                } catch (IllegalArgumentException e) {
                    // Ignore the exception and don't update the text field
                }
            }
        });
    }

    /**
     * Handles the selection of a disaster type. This method is called when the
     * user selects a disaster type from the combo box.
     */
    @FXML
    protected void handleDisasterTypeSelection() {
        String selectedDisaster = disasterTypeComboBox.getValue();
        specificFieldsContainer.getChildren().clear();

        switch (selectedDisaster) {
            case "Wildfire":
                addWildfireFields();
                break;
            case "Hurricane":
                addHurricaneFields();
                break;
            case "Earthquake":
                addEarthquakeFields();
                break;
            case "Flood":
                addFloodFields();
                break;
            case "Landslide":
                addLandslideFields();
                break;
            case "Other":
                addOtherFields();
                break;
        }

        resizeWindow();
    }

    /**
     * Resizes the window to fit the content.
     */
    private void resizeWindow() {
        if (stage != null) {
            stage.sizeToScene();
            stage.centerOnScreen();
        }
    }

    /**
     * Adds Wildfire specific fields to the UI.
     */
    private void addWildfireFields() {
        intensityComboBox = new ComboBox<>();
        intensityComboBox.getItems().addAll("Low", "Medium", "High");
        intensityComboBox.setPromptText("Select Fire Intensity");
        intensityComboBox.getStyleClass().add("mandatory");

        areaField = new TextField();
        areaField.setPromptText("Affected Area Size (optional)");

        infrastructureField = new TextField();
        infrastructureField.setPromptText("Nearby Infrastructure at Risk (optional)");

        specificFieldsContainer.getChildren().addAll(
                new Label("Fire Intensity:"),
                intensityComboBox,
                new Label("Affected Area Size:"),
                areaField,
                new Label("Nearby Infrastructure at Risk:"),
                infrastructureField
        );
    }

    /**
     * Adds Hurricane specific fields to the UI.
     */
    private void addHurricaneFields() {
        windSpeedField = new TextField();
        windSpeedField.setPromptText("Wind Speed (optional)");

        floodRiskCheckBox = new CheckBox("Flood Risk");

        evacuationStatusComboBox = new ComboBox<>();
        evacuationStatusComboBox.getItems().addAll("None", "Partial", "Full");
        evacuationStatusComboBox.setPromptText("Select Evacuation Status");
        evacuationStatusComboBox.getStyleClass().add("mandatory");

        specificFieldsContainer.getChildren().addAll(
                new Label("Wind Speed:"),
                windSpeedField,
                floodRiskCheckBox,
                new Label("Evacuation Status:"),
                evacuationStatusComboBox
        );
    }

    /**
     * Adds Earthquake specific fields to the UI.
     */
    private void addEarthquakeFields() {
        magnitudeField = new TextField();
        magnitudeField.setPromptText("Magnitude (optional)");

        depthField = new TextField();
        depthField.setPromptText("Depth (optional)");

        aftershocksCheckBox = new CheckBox("Aftershocks Expected");

        specificFieldsContainer.getChildren().addAll(
                new Label("Magnitude:"),
                magnitudeField,
                new Label("Depth:"),
                depthField,
                aftershocksCheckBox
        );
    }

    /**
     * Adds Flood specific fields to the UI.
     */
    private void addFloodFields() {
        waterLevelComboBox = new ComboBox<>();
        waterLevelComboBox.getItems().addAll("Low", "Medium", "High");
        waterLevelComboBox.setPromptText("Select Water Level");
        waterLevelComboBox.getStyleClass().add("mandatory");

        floodEvacuationStatusComboBox = new ComboBox<>();
        floodEvacuationStatusComboBox.getItems().addAll("None", "Partial", "Full");
        floodEvacuationStatusComboBox.setPromptText("Select Evacuation Status");
        floodEvacuationStatusComboBox.getStyleClass().add("mandatory");

        infrastructureDamageField = new TextField();
        infrastructureDamageField.setPromptText("Infrastructure Damage (optional)");

        specificFieldsContainer.getChildren().addAll(
                new Label("Water Level:"),
                waterLevelComboBox,
                new Label("Evacuation Status:"),
                floodEvacuationStatusComboBox,
                new Label("Infrastructure Damage:"),
                infrastructureDamageField
        );
    }

    /**
     * Adds Landslide specific fields to the UI.
     */
    private void addLandslideFields() {
        slopeStabilityComboBox = new ComboBox<>();
        slopeStabilityComboBox.getItems().addAll("Stable", "Unstable");
        slopeStabilityComboBox.setPromptText("Select Slope Stability");
        slopeStabilityComboBox.getStyleClass().add("mandatory");

        blockedRoadsField = new TextField();
        blockedRoadsField.setPromptText("Blocked Roads (optional)");

        casualtiesField = new TextField();
        casualtiesField.setPromptText("Casualties/Injuries (optional)");

        specificFieldsContainer.getChildren().addAll(
                new Label("Slope Stability:"),
                slopeStabilityComboBox,
                new Label("Blocked Roads:"),
                blockedRoadsField,
                new Label("Casualties/Injuries:"),
                casualtiesField
        );
    }

    /**
     * Adds fields for Other disaster types to the UI.
     */
    private void addOtherFields() {
        disasterDescriptionArea = new TextArea();
        disasterDescriptionArea.setPromptText("Disaster Description");
        disasterDescriptionArea.getStyleClass().add("mandatory");

        impactComboBox = new ComboBox<>();
        impactComboBox.getItems().addAll("Low", "Medium", "High");
        impactComboBox.setPromptText("Estimated Impact");

        specificFieldsContainer.getChildren().addAll(
                new Label("Disaster Description:"),
                disasterDescriptionArea,
                new Label("Estimated Impact:"),
                impactComboBox
        );
    }

    /**
     * Handles the submission of a disaster report. This method is called when
     * the user clicks the submit button.
     */
    @FXML
    private void handleSubmitReport() {
        LocalDate date = datePicker.getValue();

        if (!validateMandatoryFields()) {
            showAlert("Error", "Please fill all mandatory fields correctly.");
            return;
        }

        Map<String, Object> columnValueMap = new HashMap<>();
        System.out.println("\nReport is creating...");

        // Populate the columnValueMap with common fields
        columnValueMap.put("disaster_type", disasterTypeComboBox.getValue());
        System.out.println("   " + disasterTypeComboBox.getValue());

        columnValueMap.put("location", locationField.getText());
        System.out.println("   " + locationField.getText());

        columnValueMap.put("date_time", java.sql.Date.valueOf(date));

        columnValueMap.put("reporter_name", reporterNameField.getText());
        System.out.println("   " + reporterNameField.getText());

        columnValueMap.put("priority_level", "Low");
        columnValueMap.put("response_status", "Pending");

        columnValueMap.put("communication_log", "");

        // Remove spaces from contact info before storing
        String contactInfo = contactInfoField.getText().replaceAll("\\s+", "");
        columnValueMap.put("contact_info", contactInfo);
        System.out.println("   " + contactInfo);
        System.out.println("   ...");

        // Add disaster-specific fields to the columnValueMap
        addDisasterSpecificFields(columnValueMap);

        // Insert the report into the database
        insertReportIntoDatabase(columnValueMap);
    }

    /**
     * Adds disaster-specific fields to the columnValueMap based on the selected
     * disaster type.
     *
     * @param columnValueMap The map to populate with disaster-specific field
     * values.
     */
    private void addDisasterSpecificFields(Map<String, Object> columnValueMap) {
        switch (disasterTypeComboBox.getValue()) {
            case "Wildfire":
                columnValueMap.put("fire_intensity", intensityComboBox.getValue());
                columnValueMap.put("affected_area_size", areaField.getText());
                columnValueMap.put("nearby_infrastructure", infrastructureField.getText());
                break;
            case "Hurricane":
                columnValueMap.put("wind_speed", windSpeedField.getText());
                columnValueMap.put("flood_risk", floodRiskCheckBox.isSelected());
                columnValueMap.put("evacuation_status", evacuationStatusComboBox.getValue());
                break;
            case "Earthquake":
                columnValueMap.put("magnitude", magnitudeField.getText());
                columnValueMap.put("depth", depthField.getText());
                columnValueMap.put("aftershocks_expected", aftershocksCheckBox.isSelected());
                break;
            case "Flood":
                columnValueMap.put("water_level", waterLevelComboBox.getValue());
                columnValueMap.put("flood_evacuation_status", floodEvacuationStatusComboBox.getValue());
                columnValueMap.put("infrastructure_damage", infrastructureDamageField.getText());
                break;
            case "Landslide":
                columnValueMap.put("slope_stability", slopeStabilityComboBox.getValue());
                columnValueMap.put("blocked_roads", blockedRoadsField.getText());
                columnValueMap.put("casualties_injuries", casualtiesField.getText());
                break;
            case "Other":
                columnValueMap.put("disaster_description", disasterDescriptionArea.getText());
                columnValueMap.put("estimated_impact", impactComboBox.getValue());
                break;
        }
    }

    /**
     * Inserts the report into the database.
     *
     * @param columnValueMap The map containing the column names and values to
     * be inserted.
     */
    private void insertReportIntoDatabase(Map<String, Object> columnValueMap) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String columns = String.join(", ", columnValueMap.keySet());
            String placeholders = String.join(", ", Collections.nCopies(columnValueMap.size(), "?"));
            String sql = "INSERT INTO reports (" + columns + ") VALUES (" + placeholders + ")";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                int paramIndex = 1;
                for (Object value : columnValueMap.values()) {
                    pstmt.setObject(paramIndex++, value);
                }

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Success", "Report submitted successfully!");
                    System.out.println("Report submitted.\n");
                    clearFields();
                } else {
                    showAlert("Error", "Failed to submit report. Please try again.");
                    System.out.println("Failed to submit report.\n");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(C_ReportDisaster.class.getName()).log(Level.SEVERE, null, ex);
            showAlert("Error", "An error occurred while submitting the report: " + ex.getMessage());
        }
    }

    /**
     * Validates that all mandatory fields have been filled and the contact info
     * is correct.
     *
     * @return true if all mandatory fields are filled and contact info is
     * valid, false otherwise.
     */
    public boolean validateMandatoryFields() {
        if (disasterTypeComboBox.getValue() == null
                || locationField.getText().isEmpty()
                || datePicker.getValue() == null
                || reporterNameField.getText().isEmpty()
                || contactInfoField.getText().isEmpty()) {
            return false;
        }

        // Validate contact information
        String contactInfo = contactInfoField.getText().replaceAll("\\s+", "");
        if (contactInfo.length() != 10 || !contactInfo.matches("\\d{10}")) {
            showAlert("Error", "Please enter a valid 10-digit phone number.");
            return false;
        }

        return true;
    }

    /**
     * Shows an alert dialog with the given title and content.
     *
     * @param title The title of the alert dialog.
     * @param content The content message of the alert dialog.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        handleDisasterTypeSelection();
        locationField.clear();
        datePicker.setValue(null);
        reporterNameField.clear();
        contactInfoField.clear();
        // Note: Other specific fields are cleared when handleDisasterTypeSelection() is called
    }

    /**
     * Sets the stage for this controller.
     *
     * @param stage The stage to be set.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public boolean validateContactInfo(String contactInfo) {
        return contactInfo.replaceAll("\\s+", "").matches("\\d{10}");
    }

    // This method is for testing purposes only
    public boolean validateMandatoryFieldsTest(String disasterType, String location, LocalDate date, String reporterName, String contactInfo) {
        return disasterType != null && !disasterType.isEmpty()
                && location != null && !location.isEmpty()
                && date != null
                && reporterName != null && !reporterName.isEmpty()
                && validateContactInfo(contactInfo);
    }

    public void addDisasterSpecificFields(Map<String, Object> columnValueMap, String disasterType, String... fields) {
        switch (disasterType) {
            case "Wildfire":
                columnValueMap.put("fire_intensity", fields[0]);
                columnValueMap.put("affected_area_size", fields[1]);
                columnValueMap.put("nearby_infrastructure", fields[2]);
                break;
            case "Hurricane":
                columnValueMap.put("wind_speed", fields[0]);
                columnValueMap.put("flood_risk", Boolean.parseBoolean(fields[1]));
                columnValueMap.put("evacuation_status", fields[2]);
                break;
            case "Earthquake":
                columnValueMap.put("magnitude", fields[0]);
                columnValueMap.put("depth", fields[1]);
                columnValueMap.put("aftershocks_expected", Boolean.parseBoolean(fields[2]));
                break;
            case "Flood":
                columnValueMap.put("water_level", fields[0]);
                columnValueMap.put("flood_evacuation_status", fields[1]);
                columnValueMap.put("infrastructure_damage", fields[2]);
                break;
            case "Landslide":
                columnValueMap.put("slope_stability", fields[0]);
                columnValueMap.put("blocked_roads", fields[1]);
                columnValueMap.put("casualties_injuries", fields[2]);
                break;
            case "Other":
                columnValueMap.put("disaster_description", fields[0]);
                columnValueMap.put("estimated_impact", fields[1]);
                break;
            default:
                // Handle unknown disaster type or throw an exception
                throw new IllegalArgumentException("Unknown disaster type: " + disasterType);
        }
    }
}
