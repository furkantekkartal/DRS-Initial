package Controller;

import ENUM.*;
import Model.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Map;
import java.util.EnumMap;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Region;

/**
 * Controller class for the Coordinator view. Manages the interaction between
 * the UI and the Coordinator model.
 *
 * @author 12223508
 */
public class C_Coordinator {

    // FXML Annotations and UI Components
    @FXML
    private TableView<Report> reportTableView;
    @FXML
    private TableColumn<Report, Integer> idColumn;
    @FXML
    private TableColumn<Report, String> disasterTypeColumn;
    @FXML
    private TableColumn<Report, String> locationColumn;
    @FXML
    private TableColumn<Report, String> dateTimeColumn;
    @FXML
    private TableColumn<Report, String> reporterNameColumn;
    @FXML
    private TableColumn<Report, String> contactInfoColumn;
    @FXML
    private TableColumn<Report, String> responseStatusColumn;
    @FXML
    private TableColumn<Report, Double> latitudeColumn;
    @FXML
    private TableColumn<Report, Double> longitudeColumn;
    @FXML
    private TableColumn<Report, String> priorityLevelColumn;

    @FXML
    private TextArea detailsTextArea;
    @FXML
    private ComboBox<String> responseStatusComboBox;
    @FXML
    private TextArea resourcesNeededArea;
    @FXML
    private TextArea communicationLogArea;
    @FXML
    private ComboBox<String> priorityLevelComboBox;

    @FXML
    private CheckBox fireDeptCheckBox;
    @FXML
    private CheckBox healthDeptCheckBox;
    @FXML
    private CheckBox lawEnforcementCheckBox;
    @FXML
    private CheckBox utilityCompanyCheckBox;

    @FXML
    private TableView<Report> disasterStatusTableView;
    @FXML
    private TableColumn<Report, Integer> statusIdColumn;
    @FXML
    private TableColumn<Report, String> statusTypeColumn;
    @FXML
    private TableColumn<Report, String> statusDateColumn;
    @FXML
    private TableColumn<Report, String> statusLocationColumn;
    @FXML
    private TableColumn<Report, String> fireDepartmentStatusColumn;
    @FXML
    private TableColumn<Report, String> healthDepartmentStatusColumn;
    @FXML
    private TableColumn<Report, String> lawEnforcementStatusColumn;
    @FXML
    private TableColumn<Report, String> utilityElectricityStatusColumn;
    @FXML
    private TableColumn<Report, String> utilityWaterStatusColumn;
    @FXML
    private TableColumn<Report, String> utilityGasStatusColumn;
    @FXML
    private TableColumn<Report, String> utilityTelecomStatusColumn;

    @FXML
    private TextField newLogEntryField;

    @FXML
    private Button getCoordinates;
    @FXML
    private Button getWeather;
    @FXML
    private Label userLabel;

    @FXML
    private TextField weatherImpactField;
    @FXML
    private TextField calculatedPriorityField;

    // Class variables
    private String currentUser;
    private Coordinator model;
    private ObservableList<Report> reports = FXCollections.observableArrayList();
    private ObservableList<Report> disasterStatusReports = FXCollections.observableArrayList();

    /**
     * Initializes the controller. This method is automatically called after the
     * FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        model = new Coordinator();
        setupTableColumns();
        setupComboBoxes();
        setupEventListeners();
        loadDataFromModel();

        userLabel.setText("Logged in as: " + currentUser);
    }

    /**
     * Sets the current user and updates the UI.
     *
     * @param username The username of the current user
     */
    public void setCurrentUser(String username) {
        this.currentUser = username;
        userLabel.setText("Logged in as: " + username);
    }

    /**
     * Sets up the table columns for both report tables.
     */
    private void setupTableColumns() {
        // Setup main report table
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        disasterTypeColumn.setCellValueFactory(new PropertyValueFactory<>("disasterType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        latitudeColumn.setCellValueFactory(cellData -> cellData.getValue().latitudeProperty().asObject());
        longitudeColumn.setCellValueFactory(cellData -> cellData.getValue().longitudeProperty().asObject());
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        reporterNameColumn.setCellValueFactory(new PropertyValueFactory<>("reporterName"));
        contactInfoColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
        responseStatusColumn.setCellValueFactory(new PropertyValueFactory<>("responseStatus"));
        priorityLevelColumn.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));

        idColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.04));
        disasterTypeColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.08));
        locationColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.16));
        latitudeColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.1));
        longitudeColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.1));
        dateTimeColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.12));
        reporterNameColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.1));
        contactInfoColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.1));
        responseStatusColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.09));
        priorityLevelColumn.prefWidthProperty().bind(reportTableView.widthProperty().multiply(0.09));

        reportTableView.setItems(reports);

        // Setup disaster status table
        setupDisasterStatusTable();
    }

    /**
     * Sets up the disaster status table columns.
     */
    private void setupDisasterStatusTable() {
        statusIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        statusTypeColumn.setCellValueFactory(new PropertyValueFactory<>("disasterType"));
        statusDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        statusLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        fireDepartmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("fireDeptStatus"));
        healthDepartmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("healthDeptStatus"));
        lawEnforcementStatusColumn.setCellValueFactory(new PropertyValueFactory<>("lawEnforcementStatus"));

        utilityElectricityStatusColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDepartmentStatus(Department.UTILITY_ELECTRICITY).getDisplayName()));
        utilityWaterStatusColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDepartmentStatus(Department.UTILITY_WATER).getDisplayName()));
        utilityGasStatusColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDepartmentStatus(Department.UTILITY_GAS).getDisplayName()));
        utilityTelecomStatusColumn.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getDepartmentStatus(Department.UTILITY_TELECOMMUNICATIONS).getDisplayName()));

        disasterStatusTableView.setItems(disasterStatusReports);
    }

    /**
     * Sets up the combo boxes with their respective items.
     */
    private void setupComboBoxes() {
        responseStatusComboBox.getItems().addAll("Pending", "In Progress", "Resolved");
        priorityLevelComboBox.getItems().addAll("Low", "Medium", "High", "Critical");
    }

    /**
     * Sets up event listeners for various UI components.
     */
    private void setupEventListeners() {
        reportTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showReportDetails(newSelection);
            }
        });

        reportTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && reportTableView.getSelectionModel().getSelectedItem() != null) {
                Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
                openEditReportWindow(selectedReport);
            }
        });

        getCoordinates.setOnAction(event -> getCoordinatesForReport());
        getWeather.setOnAction(event -> showWeatherInfo());
    }

    /**
     * Loads data from the model and updates the UI.
     */
    private void loadDataFromModel() {
        model.loadReportsFromDatabase();
        model.loadDisasterStatusReports();
        updateUI();
    }

    /**
     * Updates the UI with the latest data from the model.
     */
    private void updateUI() {
        reports.setAll(model.getReports());
        disasterStatusReports.setAll(model.getDisasterStatusReports());
        reportTableView.refresh();
        disasterStatusTableView.refresh();
    }

    /**
     * Refreshes the reports from the model.
     */
    @FXML
    public void refreshReports() {
        loadDataFromModel();
        showAlert("Report", "Report page has been updated succesfully.");
    }

    /**
     * Refreshes the disaster status reports from the model.
     */
    @FXML
    private void refreshDisasterStatus() {
        model.loadDisasterStatusReports();
        updateUI();
    }

    /**
     * Displays the details of the selected report in the UI.
     *
     * @param report The selected report to display
     */
    private void showReportDetails(Report report) {
        StringBuilder details = new StringBuilder();
        details.append("Disaster Type: ").append(report.getDisasterType()).append("\n");
        details.append("Location: ").append(report.getLocation()).append("\n");
        details.append("Date/Time: ").append(report.getDateTime()).append("\n");
        details.append("Reporter: ").append(report.getReporterName()).append("\n");
        details.append("Contact: ").append(report.getContactInfo()).append("\n\n");

        String communicationLog = report.getCommunicationLog();
        if (communicationLog != null) {
            communicationLogArea.setText(communicationLog);
        } else {
            communicationLogArea.clear();
        }

        switch (report.getDisasterType()) {
            case "Wildfire":
                details.append("Fire Intensity: ").append(report.getFireIntensity()).append("\n");
                details.append("Affected Area Size: ").append(report.getAffectedAreaSize()).append("\n");
                details.append("Nearby Infrastructure: ").append(report.getNearbyInfrastructure()).append("\n");
                break;
            case "Hurricane":
                details.append("Wind Speed: ").append(report.getWindSpeed()).append("\n");
                details.append("Flood Risk: ").append(report.getFloodRisk()).append("\n");
                details.append("Evacuation Status: ").append(report.getEvacuationStatus()).append("\n");
                break;
            case "Earthquake":
                details.append("Magnitude: ").append(report.getMagnitude()).append("\n");
                details.append("Depth: ").append(report.getDepth()).append("\n");
                details.append("Aftershocks Expected: ").append(report.getAftershocksExpected()).append("\n");
                break;
            case "Flood":
                details.append("Water Level: ").append(report.getWaterLevel()).append("\n");
                details.append("Flood Evacuation Status: ").append(report.getFloodEvacuationStatus()).append("\n");
                details.append("Infrastructure Damage: ").append(report.getInfrastructureDamage()).append("\n");
                break;
            case "Landslide":
                details.append("Slope Stability: ").append(report.getSlopeStability()).append("\n");
                details.append("Blocked Roads: ").append(report.getBlockedRoads()).append("\n");
                details.append("Casualties/Injuries: ").append(report.getCasualtiesInjuries()).append("\n");
                break;
            default:
                details.append("Disaster Description: ").append(report.getDisasterDescription()).append("\n");
                details.append("Estimated Impact: ").append(report.getEstimatedImpact()).append("\n");
                break;
        }

        detailsTextArea.setText(details.toString());
        responseStatusComboBox.setValue(report.getResponseStatus());
        resourcesNeededArea.setText(report.getResourcesNeeded());
        communicationLogArea.setText(report.getCommunicationLog());
        priorityLevelComboBox.setValue(report.getPriorityLevel());

        if (report.getLatitude() != 0 && report.getLongitude() != 0) {
            detailsTextArea.appendText("\nLatitude: " + report.getLatitude());
            detailsTextArea.appendText("\nLongitude: " + report.getLongitude());
        }

        updateDepartmentCheckboxes(report);
        calculatedPriorityField.setText("");
        weatherImpactField.setText("");

//        // Weather Impact Analysis
//        Meteorology.WeatherImpact weatherImpact = model.analyzeWeatherImpact(report);
//        if (weatherImpact != null) {
//            weatherImpactField.setText(weatherImpact.getRiskLevel());
//        } else {
//            weatherImpactField.setText("N/A");
//        }
//        // Calculate Priority
//        String calculatedPriority = model.calculatePriority(report);
//        calculatedPriorityField.setText(calculatedPriority);
    }

    /**
     * Updates the department checkboxes based on the selected report.
     *
     * @param report The selected report
     */
    private void updateDepartmentCheckboxes(Report report) {
        fireDeptCheckBox.setSelected(report.getDepartmentStatus(Department.FIRE_DEPARTMENT) != ResponseStatus.NOT_RESPONSIBLE);
        healthDeptCheckBox.setSelected(report.getDepartmentStatus(Department.HEALTH_DEPARTMENT) != ResponseStatus.NOT_RESPONSIBLE);
        lawEnforcementCheckBox.setSelected(report.getDepartmentStatus(Department.LAW_ENFORCEMENT) != ResponseStatus.NOT_RESPONSIBLE);
        utilityCompanyCheckBox.setSelected(report.getDepartmentStatus(Department.UTILITY_COMPANIES) != ResponseStatus.NOT_RESPONSIBLE);
    }

    /**
     * Opens the edit report window for the selected report.
     *
     * @param report The report to be edited
     */
    private void openEditReportWindow(Report report) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/furkan/coit20258_assignment2/edit_report_window.fxml"));
            Parent root = loader.load();

            C_EditReportWindow controller = loader.getController();
            controller.initData(report, this);

            Stage stage = new Stage();
            stage.setTitle("Edit Report");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Edit Report window: " + e.getMessage());
        }
    }

    /**
     * Handles the assignment of departments to the selected report.
     */
    @FXML
    private void handleAssignDepartments() {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("Error", "No report selected. Please select a report to assign departments.");
            return;
        }

        Map<Department, ResponseStatus> assignments = createAssignmentsFromCheckboxes();
        try {
            model.updateDepartmentAssignments(selectedReport, assignments);
            updateUI();
            showAssignmentAlert(assignments);
        } catch (NullPointerException e) {
            showAlert("Error", "Failed to update department assignments. Please ensure all departments are properly set.");
            e.printStackTrace();
        }
    }

    /**
     * Creates a map of department assignments based on checkbox states.
     *
     * @return A map of department assignments
     */
    private Map<Department, ResponseStatus> createAssignmentsFromCheckboxes() {
        Map<Department, ResponseStatus> assignments = new EnumMap<>(Department.class);
        assignments.put(Department.FIRE_DEPARTMENT, fireDeptCheckBox.isSelected() ? ResponseStatus.NOT_RESPONDED_YET : ResponseStatus.NOT_RESPONSIBLE);
        assignments.put(Department.HEALTH_DEPARTMENT, healthDeptCheckBox.isSelected() ? ResponseStatus.NOT_RESPONDED_YET : ResponseStatus.NOT_RESPONSIBLE);
        assignments.put(Department.LAW_ENFORCEMENT, lawEnforcementCheckBox.isSelected() ? ResponseStatus.NOT_RESPONDED_YET : ResponseStatus.NOT_RESPONSIBLE);
        assignments.put(Department.UTILITY_COMPANIES, utilityCompanyCheckBox.isSelected() ? ResponseStatus.NOT_RESPONDED_YET : ResponseStatus.NOT_RESPONSIBLE);
        assignments.put(Department.METEOROLOGY, ResponseStatus.NOT_RESPONSIBLE);
        assignments.put(Department.GEOSCIENCE, ResponseStatus.NOT_RESPONSIBLE);
        return assignments;
    }

    /**
     * Shows an alert with the assigned departments.
     *
     * @param assignments The map of department assignments
     */
    private void showAssignmentAlert(Map<Department, ResponseStatus> assignments) {
        StringBuilder assignedDepartments = new StringBuilder("Assigned Departments:\n");
        boolean anyAssigned = false;
        for (Map.Entry<Department, ResponseStatus> entry : assignments.entrySet()) {
            if (entry.getValue() == ResponseStatus.NOT_RESPONDED_YET) {
                assignedDepartments.append("- ").append(entry.getKey().toString()).append("\n");
                anyAssigned = true;
            }
        }

        if (anyAssigned) {
            showAlert("Departments Assigned", assignedDepartments.toString());
        } else {
            showAlert("No Assignments", "No departments were assigned.");
        }
    }

    /**
     * Handles adding a new log entry to the selected report.
     */
    @FXML
    private void handleAddLogEntry() {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("Error", "No report selected. Please select a report to add a log entry.");
            return;
        }

        String newEntry = newLogEntryField.getText().trim();
        if (newEntry.isEmpty()) {
            showAlert("Error", "Please enter a log entry.");
            return;
        }

        addCommunicationLog(newEntry);
        newLogEntryField.clear();
    }

    /**
     * Adds a communication log entry to the selected report.
     *
     * @param log The log entry to add
     */
    public void addCommunicationLog(String log) {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("Error", "No report selected. Please select a report to add a log.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);
        String logEntry = String.format("[%s] Coordinator: %s", timestamp, log);

        model.addCommunicationLog(selectedReport, logEntry);
        updateCommunicationLog(selectedReport);
        updateUI();
    }

    private void updateCommunicationLog(Report report) {
        communicationLogArea.setText(report.getCommunicationLog());
    }

    /**
     * Opens the add resource window.
     */
    @FXML
    private void openAddResourceWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/furkan/coit20258_assignment2/add_resource_window.fxml"));
            Parent root = loader.load();

            C_AddResourceWindow controller = loader.getController();
            controller.setDRCController(this);

            Stage stage = new Stage();
            stage.setTitle("Add Resource Needed");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open Add Resource window: " + e.getMessage());
        }
    }

    /**
     * Adds a resource needed to the selected report.
     *
     * @param resource The resource to add
     */
    public void addResourceNeeded(String resource) {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("Error", "No report selected. Please select a report to add a resource.");
            return;
        }

        model.addResourceNeeded(selectedReport, resource);
        resourcesNeededArea.setText(selectedReport.getResourcesNeeded());
        updateUI();
    }

    /**
     * Updates the department status in the UI.
     *
     * @param departmentKey The key of the department
     * @param updateArea The text area to update
     */
    private void updateDepartmentStatus(String departmentKey, TextArea updateArea) {
        String updates = model.getDepartmentUpdate(departmentKey);
        updateArea.setText(updates);
    }

    /**
     * Gets coordinates for the selected report.
     */
    private void getCoordinatesForReport() {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("Error", "No report selected. Please select a report to get coordinates.");
            return;
        }

        String location = selectedReport.getLocation();
        if (location == null || location.isEmpty()) {
            showAlert("Error", "The selected report does not have a location specified.");
            return;
        }

        double[] coordinates = model.getCoordinates(location);
        if (coordinates != null && coordinates.length == 2) {
            double latitude = coordinates[0];
            double longitude = coordinates[1];

            model.updateCoordinates(selectedReport, latitude, longitude);
            updateUI();

            String updatedDetails = detailsTextArea.getText() + "\n\nUpdated Coordinates:\nLatitude: " + latitude + "\nLongitude: " + longitude;
            detailsTextArea.setText(updatedDetails);

            showAlert("Success", "Coordinates updated successfully for " + location);
        } else {
            showAlert("Not Found", "Coordinates for the location '" + location + "' were not found.");
        }
    }

    /**
     * Shows weather information for the selected report.
     */
    private void showWeatherInfo() {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("No Selection", "Please select a report to view weather information.");
            return;
        }

        double latitude = selectedReport.getLatitude();
        double longitude = selectedReport.getLongitude();

        if (latitude == 0 && longitude == 0) {
            showAlert("Missing Information", "Latitude and longitude are not available for this report. Please get coordinates first.");
            return;
        }

        Meteorology weather = model.getWeather(latitude, longitude);
        if (weather == null) {
            showAlert("No Weather Data", "Weather information for this location is not available in our system.");
            return;
        }

        showAlert("Weather Information", weather.toString());
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
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    private void handleWeatherImpactButton() {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport != null) {
            Meteorology.WeatherImpact weatherImpact = model.analyzeWeatherImpact(selectedReport);
            if (weatherImpact != null) {
                weatherImpactField.setText(weatherImpact.getRiskLevel());
                showAlert("Weather Impact Analysis", weatherImpact.toString());
            } else {
                showAlert("Weather Impact Analysis", "Unable to analyze weather impact for this report.");
            }
        } else {
            showAlert("Error", "No report selected. Please select a report to show weather impact.");
        }
    }

    @FXML
    private void handlePrioritySuggestionButton() {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport != null) {
            String result = model.calculatePriority(selectedReport);
            String[] parts = result.split("\\|", 2); // Split only on the first "|"
            calculatedPriorityField.setText(parts[0]);
            showAlert("Priority Calculation Process", parts[1]);
        } else {
            showAlert("Error", "No report selected. Please select a report to show calculated priority.");
        }
    }

    @FXML
    public void updateButton() {
        Report selectedReport = reportTableView.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("Error", "No report selected. Please select a report to update.");
            return;
        }

        selectedReport.setResponseStatus(responseStatusComboBox.getValue());
        selectedReport.setResourcesNeeded(resourcesNeededArea.getText());
        selectedReport.setCommunicationLog(communicationLogArea.getText());
        selectedReport.setPriorityLevel(priorityLevelComboBox.getValue());

        showAlert("Update", "Report updated succesfully.");
        System.out.println("Report updated succesfully.");

        model.updateReport(selectedReport);
        updateUI();
    }
}
