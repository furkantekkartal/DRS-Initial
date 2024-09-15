package Controller;

import Model.Report;
import Model.Meteorology;
import Model.Geoscience;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller class for the Meteorology module of the disaster management
 * system. Handles UI interactions and database operations related to weather
 * data and reports.
 *
 * @author 12223508
 */
public class C_Meteorology {

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
    private TextField temperatureField;
    @FXML
    private TextField humidityField;
    @FXML
    private TextField windSpeedField;
    @FXML
    private TextField windDirectionField;
    @FXML
    private ComboBox<String> conditionsComboBox;

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
        setupConditionsComboBox();
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
        infoLabel.setText("Select a record to see latest weather information");
        userLabel.setText("Logged in as: " + currentUser);
    }

    /**
     * Sets up the conditions combo box with predefined weather conditions.
     */
    private void setupConditionsComboBox() {
        conditionsComboBox.getItems().addAll("Sunny", "Cloudy", "Rainy", "Snowy", "Windy", "Partly Cloudy", "Overcast");
    }

    /**
     * Sets up the listener for table row selection.
     */
    private void setupTableSelectionListener() {
        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReport = newSelection;
                updateReportDetails(newSelection);
                fetchAndDisplayWeatherData(newSelection);
                updateCommunicationLog(newSelection);
            }
        });
    }

    /**
     * Loads active reports from the database.
     */
    private void loadActiveReports() {
        activeReports.clear();
        activeReports.addAll(Meteorology.getActiveReports());
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
     * Fetches and displays weather data for the selected report.
     *
     * @param report The selected report
     */
    private void fetchAndDisplayWeatherData(Report report) {
        Meteorology weather = Meteorology.getWeather(report.getLatitude(), report.getLongitude());

        // Initially set fields with report data
        latitudeField.setText(String.format("%.4f", report.getLatitude()));
        longitudeField.setText(String.format("%.4f", report.getLongitude()));
        locationField.setText(report.getLocation());
        clearWeatherFields();

        if (weather != null) {
            locationField.setText(weather.getLocation());
            temperatureField.setText(String.format("%.1f", weather.getTemperature()));
            humidityField.setText(String.format("%.1f", weather.getHumidity()));
            windSpeedField.setText(String.format("%.1f", weather.getWindSpeed()));
            windDirectionField.setText(weather.getWindDirection());
            conditionsComboBox.setValue(weather.getCondition());
            infoLabel.setText("Weather data found for " + weather.getLocation());
        } else {
            infoLabel.setText("No weather data found for coordinates: "
                    + String.format("%.4f", report.getLatitude()) + ", "
                    + String.format("%.4f", report.getLongitude()));
        }
    }

    /**
     * Clears all weather-related fields in the UI.
     */
    private void clearWeatherFields() {
        temperatureField.clear();
        humidityField.clear();
        windSpeedField.clear();
        windDirectionField.clear();
        conditionsComboBox.getSelectionModel().clearSelection();
    }

    /**
     * Handles the action of saving weather data.
     */
    @FXML
    private void handleSave() {
        if (selectedReport == null) {
            showAlert("No Selection", "Please select a report to update weather data.");
            return;
        }

        try {
            double latitude = Double.parseDouble(latitudeField.getText());
            double longitude = Double.parseDouble(longitudeField.getText());
            String location = locationField.getText();
            double temperature = Double.parseDouble(temperatureField.getText());
            double humidity = Double.parseDouble(humidityField.getText());
            double windSpeed = Double.parseDouble(windSpeedField.getText());
            String windDirection = windDirectionField.getText();
            String conditions = conditionsComboBox.getValue();

            Meteorology weather = new Meteorology(latitude, longitude, location, temperature, humidity, conditions, windSpeed, windDirection);
            System.out.println("Saving weather data: " + weather);
            Meteorology.setWeather(weather);

            showAlert("Success", "Weather data saved successfully.");
            fetchAndDisplayWeatherData(selectedReport); // Refresh the displayed data
        } catch (NumberFormatException e) {
            System.out.println("Error parsing input: " + e.getMessage());
            showAlert("Invalid Input", "Please enter valid numeric values for all fields.");
        }
    }

    /**
     * Handles the action of resetting all input fields.
     */
    @FXML
    private void handleReset() {
        locationField.clear();
        latitudeField.clear();
        longitudeField.clear();
        clearWeatherFields();
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

        String logEntry = String.format("[%s] Meteorology: %s", timestamp, newEntry);

        Meteorology.addCommunicationLogEntry(selectedReport, logEntry);
        updateCommunicationLog(selectedReport);

        newLogEntryField.clear();
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
     * Sets the current user and updates the user label.
     *
     * @param username The username of the current user
     */
    public void setCurrentUser(String username) {
        this.currentUser = username;
        userLabel.setText("Logged in as: " + username);
    }
}
