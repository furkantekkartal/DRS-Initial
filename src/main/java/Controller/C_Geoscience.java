package Controller;

import Model.Report;
import Model.Geoscience;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller class for the Geoscience module of the disaster management system.
 * Handles UI interactions and database operations related to geoscience data.
 *
 * @author 12223508
 */
public class C_Geoscience {

    // FXML injected fields
    @FXML
    private TableView<Report> reportTable;
    @FXML
    private TableColumn<Report, Integer> idColumn;
    @FXML
    private TableColumn<Report, String> typeColumn;
    @FXML
    private TableColumn<Report, String> locationColumn;
    @FXML
    private TableColumn<Report, Double> latitudeColumn;
    @FXML
    private TableColumn<Report, Double> longitudeColumn;
    @FXML
    private TableColumn<Report, String> dateTimeColumn;

    @FXML
    private TextField locationField;
    @FXML
    private TextField latitudeField;
    @FXML
    private TextField longitudeField;
    @FXML
    private TextField magnitudeField;
    @FXML
    private TextField depthField;
    @FXML
    private CheckBox aftershocksCheckBox;
    @FXML
    private TextArea reportDetailsArea;
    @FXML
    private TextArea communicationLogArea;
    @FXML
    private TextField newLogEntryField;
    @FXML
    private Label infoLabel;
    @FXML
    private Label userLabel;

    // Class fields
    private String currentUser;
    private ObservableList<Report> activeReports = FXCollections.observableArrayList();
    private Geoscience gis;
    private Report selectedReport;

    /**
     * Initializes the controller. This method is automatically called after the
     * FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        gis = new Geoscience(null);  // Pass null or appropriate DisasterType
        setupTableColumns();
        setupTableData();
        setupAfterShocksCheckBox();
        loadActiveReports();
        setupTableSelectionListener();
    }

    /**
     * Sets up the table columns and their cell value factories.
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("disasterType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));

        idColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.1));
        typeColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.15));
        latitudeColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.15));
        longitudeColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.15));
        locationColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.25));
        dateTimeColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.2));
    }

    /**
     * Sets up the table data and initial UI state.
     */
    private void setupTableData() {
        reportTable.setItems(activeReports);
        infoLabel.setText("Select a record to see latest geoscience information");
        userLabel.setText("Logged in as: " + currentUser);
    }

    /**
     * Sets up the aftershocks combo box with predefined options.
     */
    private void setupAfterShocksCheckBox() {
        aftershocksCheckBox.setText("Aftershocks Expected");
    }

    /**
     * Sets up the listener for table row selection.
     */
    private void setupTableSelectionListener() {
        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReport = newSelection;
                updateReportDetails(newSelection);
                fetchAndDisplayGeoscienceData(newSelection);
                updateCommunicationLog(newSelection);
            }
        });
    }

    /**
     * Loads active reports from the database.
     */
    private void loadActiveReports() {
        activeReports.clear();
        activeReports.addAll(gis.getActiveReports());
    }

    /**
     * Updates the report details area with the selected report's information.
     *
     * @param report The selected report
     */
    private void updateReportDetails(Report report) {
        StringBuilder details = new StringBuilder();
        details.append("Disaster Type: ").append(report.getDisasterType()).append("\n");
        details.append("Location: ").append(report.getLocation()).append("\n");
        details.append("Date/Time: ").append(report.getDateTime()).append("\n");
        details.append("Reporter: ").append(report.getReporterName()).append("\n");
        details.append("Contact: ").append(report.getContactInfo()).append("\n");
        details.append("Latitude: ").append(report.getLatitude()).append("\n");
        details.append("Longitude: ").append(report.getLongitude()).append("\n");
        details.append("Response Status: ").append(report.getResponseStatus()).append("\n");

        reportDetailsArea.setText(details.toString());

        locationField.setText(report.getLocation());
        latitudeField.setText(String.valueOf(report.getLatitude()));
        longitudeField.setText(String.valueOf(report.getLongitude()));
    }

    /**
     * Fetches and displays geoscience data for the selected report.
     *
     * @param report The selected report
     */
    private void fetchAndDisplayGeoscienceData(Report report) {
        Geoscience.GISData gisData = gis.getData(report.getLatitude(), report.getLongitude());

        // Initially set fields with report data
        latitudeField.setText(String.format("%.4f", report.getLatitude()));
        longitudeField.setText(String.format("%.4f", report.getLongitude()));
        locationField.setText(report.getLocation());
        clearGeoscienceFields();

        if (gisData != null) {
            locationField.setText(gisData.getLocation());
            magnitudeField.setText(String.format("%.1f", gisData.getMagnitude()));
            depthField.setText(String.format("%.1f", gisData.getDepth()));
            aftershocksCheckBox.setSelected(gisData.isAftershocksExpected());
            infoLabel.setText("Geoscience data found for " + gisData.getLocation());
        } else {
            infoLabel.setText("No geoscience data found for coordinates: "
                    + String.format("%.4f", report.getLatitude()) + ", "
                    + String.format("%.4f", report.getLongitude()));
        }
    }

    /**
     * Handles the action of getting coordinates for the selected report.
     */
    @FXML
    private void handleGetCoordinates() {
        if (selectedReport == null) {
            showAlert("Error", "Please select a report first.");
            return;
        }

        String location = selectedReport.getLocation();
        double[] coordinates = gis.getCoordinates(location);
        if (coordinates != null && coordinates.length == 2) {
            double latitude = coordinates[0];
            double longitude = coordinates[1];

            latitudeField.setText(String.format("%.4f", latitude));
            longitudeField.setText(String.format("%.4f", longitude));

            selectedReport.setLatitude(latitude);
            selectedReport.setLongitude(longitude);

            gis.updateCoordinates(selectedReport.getId(), latitude, longitude);
            fetchAndDisplayGeoscienceData(selectedReport);

            showAlert("Success", "Coordinates updated successfully.");
        } else {
            showAlert("Not Found", "Coordinates for the given location were not found.");
            clearGeoscienceFields();
        }
    }

    /**
     * Clears the geoscience data fields.
     */
    private void clearGeoscienceFields() {
        magnitudeField.clear();
        depthField.clear();
        aftershocksCheckBox.setSelected(false);
    }

    /**
     * Handles the action of saving geoscience data.
     */
    @FXML
    private void handleSave() {
        if (selectedReport == null) {
            showAlert("No Selection", "Please select a report to update geoscience data.");
            return;
        }

        try {
            // Get location data from the selected report
            String location = selectedReport.getLocation();
            double latitude = Double.parseDouble(latitudeField.getText());
            double longitude = Double.parseDouble(longitudeField.getText());

            // Get magnitude and depth from input fields
            double magnitude = Double.parseDouble(magnitudeField.getText());
            double depth = Double.parseDouble(depthField.getText());
            boolean aftershocksExpected = aftershocksCheckBox.isSelected();

            // Create GISData object
            Geoscience.GISData gisData = new Geoscience.GISData(location, latitude, longitude, magnitude, depth, aftershocksExpected);

            // Log the data being saved
            System.out.println("Attempting to save GIS data: " + gisData);

            // Save geoscience data
            gis.setData(gisData);

            // Update report coordinates in the database
            gis.updateCoordinates(selectedReport.getId(), latitude, longitude);

            // Update the selected report object
            selectedReport.setLatitude(latitude);
            selectedReport.setLongitude(longitude);

            // Refresh the displayed data
            fetchAndDisplayGeoscienceData(selectedReport);

            // Update the report details in the UI
            updateReportDetails(selectedReport);

            showAlert("Success", "Geoscience data and report coordinates saved successfully for " + location);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + e.getMessage());
            showAlert("Invalid Input", "Please enter valid numeric values for latitude, longitude, magnitude, and depth.");
        } catch (Exception e) {
            System.out.println("Error saving geoscience data: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving the geoscience data. Please try again.");
        }
    }

    /**
     * Handles the action of resetting geoscience data fields.
     */
    @FXML
    private void handleReset() {
        locationField.clear();
        latitudeField.clear();
        longitudeField.clear();
        clearGeoscienceFields();
    }

    /**
     * Handles the action of refreshing the reports table.
     */
    @FXML
    private void handleRefreshReports() {
        loadActiveReports();
    }

    /**
     * Updates the communication log area with the selected report's log.
     *
     * @param report The selected report
     */
    private void updateCommunicationLog(Report report) {
        communicationLogArea.setText(report.getCommunicationLog());
    }

    /**
     * Handles the action of adding a new log entry.
     */
    @FXML
    private void handleAddLogEntry() {
        if (selectedReport == null) {
            showAlert("Error", "Please select a report first.");
            return;
        }

        String newEntry = newLogEntryField.getText().trim();
        if (newEntry.isEmpty()) {
            showAlert("Error", "Please enter a log entry.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        String logEntry = String.format("[%s] Geoscience: %s", timestamp, newEntry);

        gis.addCommunicationLogEntry(selectedReport, logEntry);
        updateCommunicationLog(selectedReport);

        newLogEntryField.clear();
    }

    /**
     * Displays an alert dialog with the given title and content.
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
     * Sets the current user and updates the user label.
     *
     * @param username The username of the current user
     */
    public void setCurrentUser(String username) {
        this.currentUser = username;
        userLabel.setText("Logged in as: " + username);
    }
}
