package Controller;

import ENUM.DisasterType;
import Model.Geoscience;
import Model.Report;
import Util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Controller class for the Edit Report Window. Handles the editing and updating
 * of disaster reports.
 * 
 * @author 12223508
 */
public class C_EditReportWindow {

    // FXML annotated fields
    @FXML
    private TableView<Report> editTable;
    @FXML
    private TableColumn<Report, String> colDisasterType, colLocation, colDateTime, colReporterName, colContactInfo,
            colFireIntensity, colAffectedAreaSize, colNearbyInfrastructure, colWindSpeed, colEvacuationStatus,
            colMagnitude, colDepth, colWaterLevel, colFloodEvacuationStatus, colInfrastructureDamage,
            colSlopeStability, colBlockedRoads, colCasualtiesInjuries, colDisasterDescription, colEstimatedImpact,
            colResponseStatus, colAssignedDepartment, colResourcesNeeded, colCommunicationLog, colPriorityLevel;
    @FXML
    private TableColumn<Report, Double> colLatitude, colLongitude;
    @FXML
    private TableColumn<Report, Boolean> colFloodRisk, colAftershocksExpected;
    @FXML
    private TextField locationField, latitudeField, longitudeField;

    // Other fields
    private Geoscience gisService;
    private Report report;
    private C_Coordinator mainController;

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        editTable.setEditable(true);

        setupStringColumns();
        setupNumericColumns();
        setupBooleanColumns();

        gisService = new Geoscience(DisasterType.WILDFIRE); // Default disaster type
    }

    /**
     * Sets up the String type columns in the table.
     */
    private void setupStringColumns() {
        setupColumn(colDisasterType, "disasterType");
        setupColumn(colLocation, "location");
        setupColumn(colDateTime, "dateTime");
        setupColumn(colReporterName, "reporterName");
        setupColumn(colContactInfo, "contactInfo");
        setupColumn(colFireIntensity, "fireIntensity");
        setupColumn(colAffectedAreaSize, "affectedAreaSize");
        setupColumn(colNearbyInfrastructure, "nearbyInfrastructure");
        setupColumn(colWindSpeed, "windSpeed");
        setupColumn(colEvacuationStatus, "evacuationStatus");
        setupColumn(colMagnitude, "magnitude");
        setupColumn(colDepth, "depth");
        setupColumn(colWaterLevel, "waterLevel");
        setupColumn(colFloodEvacuationStatus, "floodEvacuationStatus");
        setupColumn(colInfrastructureDamage, "infrastructureDamage");
        setupColumn(colSlopeStability, "slopeStability");
        setupColumn(colBlockedRoads, "blockedRoads");
        setupColumn(colCasualtiesInjuries, "casualtiesInjuries");
        setupColumn(colDisasterDescription, "disasterDescription");
        setupColumn(colEstimatedImpact, "estimatedImpact");
        setupColumn(colResponseStatus, "responseStatus");
        setupColumn(colAssignedDepartment, "assignedDepartment");
        setupColumn(colResourcesNeeded, "resourcesNeeded");
        setupColumn(colCommunicationLog, "communicationLog");
        setupColumn(colPriorityLevel, "priorityLevel");
    }

    /**
     * Sets up the numeric (Double) type columns in the table.
     */
    private void setupNumericColumns() {
        setupNumericColumn(colLatitude, "latitude");
        setupNumericColumn(colLongitude, "longitude");
    }

    /**
     * Sets up the Boolean type columns in the table.
     */
    private void setupBooleanColumns() {
        setupBooleanColumn(colFloodRisk, "floodRisk");
        setupBooleanColumn(colAftershocksExpected, "aftershocksExpected");
    }

    /**
     * Sets up a String type column in the table.
     *
     * @param column The TableColumn to set up
     * @param propertyName The name of the property in the Report class
     */
    private void setupColumn(TableColumn<Report, String> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new EditableCell<>(col));
        column.setOnEditCommit(event -> {
            Report report = event.getRowValue();
            String newValue = event.getNewValue();
            try {
                report.getClass().getMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), String.class).invoke(report, newValue);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update " + propertyName + ": " + e.getMessage());
            }
        });
    }

    /**
     * Sets up a numeric (Double) type column in the table.
     *
     * @param column The TableColumn to set up
     * @param propertyName The name of the property in the Report class
     */
    private void setupNumericColumn(TableColumn<Report, Double> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(col -> new EditableNumericCell<>(col));
        column.setOnEditCommit(event -> {
            Report report = event.getRowValue();
            double newValue = event.getNewValue();
            try {
                report.getClass().getMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), double.class).invoke(report, newValue);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update " + propertyName + ": " + e.getMessage());
            }
        });
    }

    /**
     * Sets up a Boolean type column in the table.
     *
     * @param column The TableColumn to set up
     * @param propertyName The name of the property in the Report class
     */
    private void setupBooleanColumn(TableColumn<Report, Boolean> column, String propertyName) {
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setCellFactory(CheckBoxTableCell.forTableColumn(column));
        column.setOnEditCommit(event -> {
            Report report = event.getRowValue();
            Boolean newValue = event.getNewValue();
            try {
                report.getClass().getMethod("set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1), Boolean.class).invoke(report, newValue);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update " + propertyName + ": " + e.getMessage());
            }
        });
    }

    /**
     * Initializes the data for the edit window.
     *
     * @param report The Report object to edit
     * @param mainController The main controller
     */
    public void initData(Report report, C_Coordinator mainController) {
        this.report = report;
        this.mainController = mainController;
        ObservableList<Report> reportList = FXCollections.observableArrayList(report);
        editTable.setItems(reportList);
    }

    /**
     * Handles the action when the Save Changes button is clicked.
     */
    @FXML
    private void onSaveChangesClicked() {
        Report updatedReport = editTable.getItems().get(0);
        updateReportInDatabase(updatedReport);
        mainController.refreshReports();
        //editTable.getScene().getWindow().hide();
    }

    /**
     * Updates the report in the database.
     *
     * @param updatedReport The updated Report object
     */
    private void updateReportInDatabase(Report updatedReport) {
        String sql = "UPDATE reports SET disaster_type = ?, location = ?, latitude = ?, longitude = ?, date_time = ?, "
                + "reporter_name = ?, contact_info = ?, fire_intensity = ?, affected_area_size = ?, "
                + "nearby_infrastructure = ?, wind_speed = ?, flood_risk = ?, evacuation_status = ?, "
                + "magnitude = ?, depth = ?, aftershocks_expected = ?, water_level = ?, "
                + "flood_evacuation_status = ?, infrastructure_damage = ?, slope_stability = ?, "
                + "blocked_roads = ?, casualties_injuries = ?, disaster_description = ?, "
                + "estimated_impact = ?, response_status = ?, assigned_department = ?, "
                + "resources_needed = ?, communication_log = ?, priority_level = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set all the parameters for the prepared statement
            pstmt.setString(1, updatedReport.getDisasterType());
            pstmt.setString(2, updatedReport.getLocation());
            pstmt.setDouble(3, updatedReport.getLatitude());
            pstmt.setDouble(4, updatedReport.getLongitude());
            pstmt.setString(5, updatedReport.getDateTime());
            pstmt.setString(6, updatedReport.getReporterName());
            pstmt.setString(7, updatedReport.getContactInfo());
            pstmt.setString(8, updatedReport.getFireIntensity());
            pstmt.setString(9, updatedReport.getAffectedAreaSize());
            pstmt.setString(10, updatedReport.getNearbyInfrastructure());
            pstmt.setString(11, updatedReport.getWindSpeed());
            pstmt.setBoolean(12, updatedReport.getFloodRisk());
            pstmt.setString(13, updatedReport.getEvacuationStatus());
            pstmt.setString(14, updatedReport.getMagnitude());
            pstmt.setString(15, updatedReport.getDepth());
            pstmt.setBoolean(16, updatedReport.getAftershocksExpected());
            pstmt.setString(17, updatedReport.getWaterLevel());
            pstmt.setString(18, updatedReport.getFloodEvacuationStatus());
            pstmt.setString(19, updatedReport.getInfrastructureDamage());
            pstmt.setString(20, updatedReport.getSlopeStability());
            pstmt.setString(21, updatedReport.getBlockedRoads());
            pstmt.setString(22, updatedReport.getCasualtiesInjuries());
            pstmt.setString(23, updatedReport.getDisasterDescription());
            pstmt.setString(24, updatedReport.getEstimatedImpact());
            pstmt.setString(25, updatedReport.getResponseStatus());
            pstmt.setString(26, updatedReport.getAssignedDepartment());
            pstmt.setString(27, updatedReport.getResourcesNeeded());
            pstmt.setString(28, updatedReport.getCommunicationLog());
            pstmt.setString(29, updatedReport.getPriorityLevel());
            pstmt.setInt(30, updatedReport.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Report updated successfully");
                //showAlert("Success", "Report updated successfully");
            } else {
                System.out.println("No report was updated");
                showAlert("Error", "Failed to update report");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    /**
     * Handles the action when the Cancel button is clicked.
     */
    @FXML
    private void onCancelClicked() {
        editTable.getScene().getWindow().hide();
    }

    /**
     * Shows an alert dialog with the given title and content.
     *
     * @param title The title of the alert
     * @param content The content of the alert
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Custom TableCell for editable String fields.
     *
     * @param <T> The type of the TableView generic type
     */
    private class EditableCell<T> extends TableCell<T, String> {

        private TextField textField;

        public EditableCell(TableColumn<T, String> column) {
            textField = new TextField();
            textField.setOnAction(e -> commitEdit(textField.getText()));
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    commitEdit(textField.getText());
                }
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();
            setText(null);
            setGraphic(textField);
            textField.setText(getString());
            textField.selectAll();
            textField.requestFocus();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getString());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    setText(null);
                    setGraphic(textField);
                    textField.setText(getString());
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

    /**
     * Custom TableCell for editable numeric (Double) fields.
     *
     * @param <T> The type of the TableView generic type
     */
    private class EditableNumericCell<T> extends TableCell<T, Double> {

        private TextField textField;

        public EditableNumericCell(TableColumn<T, Double> column) {
            textField = new TextField();
            textField.setOnAction(e -> commitEdit(Double.parseDouble(textField.getText())));
            textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    try {
                        commitEdit(Double.parseDouble(textField.getText()));
                    } catch (NumberFormatException ex) {
                        cancelEdit();
                    }
                }
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();
            setText(null);
            setGraphic(textField);
            textField.setText(getDoubleString());
            textField.selectAll();
            textField.requestFocus();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getDoubleString());
            setGraphic(null);
        }

        @Override
        public void updateItem(Double item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    setText(null);
                    setGraphic(textField);
                    textField.setText(getDoubleString());
                } else {
                    setText(getDoubleString());
                    setGraphic(null);
                }
            }
        }

        private String getDoubleString() {
            return getItem() == null ? "" : String.format("%.6f", getItem());
        }
    }
}
