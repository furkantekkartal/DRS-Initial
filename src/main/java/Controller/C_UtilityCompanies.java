package Controller;

import ENUM.Department;
import ENUM.ResponseStatus;
import Model.Report;
import Model.UtilityCompanies;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

/**
 * Controller class for managing Utility Companies operations in the disaster
 * management system.
 *
 * @author 12223508
 */
public class C_UtilityCompanies {

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
    private TableColumn<Report, String> statusColumn;
    @FXML
    private TableColumn<Report, String> priorityColumn;
    @FXML
    private TableColumn<Report, String> utilityStatusColumn;
    @FXML
    private ComboBox<ResponseStatus> statusComboBox;
    @FXML
    private ComboBox<Department> subDepartmentComboBox;
    @FXML
    private TextArea reportDetailsArea;
    @FXML
    private TextArea communicationLogArea;
    @FXML
    private TextArea resourcesNeededArea;
    @FXML
    private TextField newLogEntryField;
    @FXML
    private Label userLabel;

    private String currentUser;
    private ObservableList<Report> activeReports = FXCollections.observableArrayList();
    private Report selectedReport;
    private UtilityCompanies utilityCompanies;
    private Department currentSubDepartment;

    @FXML
    public void initialize() {
        utilityCompanies = new UtilityCompanies();

        setupTableColumns();
        setupComboBoxes();
        setupEventListeners();
        loadActiveReports();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("disasterType"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        latitudeColumn.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longitudeColumn.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("responseStatus"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priorityLevel"));
        utilityStatusColumn.setCellValueFactory(this::getUtilityStatus);

        idColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.05));
        typeColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.1));
        latitudeColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.1));
        longitudeColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.1));
        locationColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.15));
        dateTimeColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.15));
        statusColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.1));
        priorityColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.1));
        utilityStatusColumn.prefWidthProperty().bind(reportTable.widthProperty().multiply(0.15));

        reportTable.setItems(activeReports);
    }

    private void setupComboBoxes() {
        statusComboBox.getItems().addAll(ResponseStatus.values());
        subDepartmentComboBox.getItems().addAll(UtilityCompanies.getUtilitySubDepartments());

        subDepartmentComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentSubDepartment = newVal;
                utilityStatusColumn.setText(currentSubDepartment.getDisplayName() + " Status");
                reportTable.refresh();
            }
        });
    }

    private void setupEventListeners() {
        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedReport = newSelection;
                updateReportDetails(newSelection);
                updateCommunicationLog(newSelection);
                updateResourcesNeeded(newSelection);
            }
        });
    }

    private void loadActiveReports() {
        activeReports.clear();
        List<Report> allReports = utilityCompanies.getActiveReports();

        for (Report report : allReports) {
            ResponseStatus utilityStatus = report.getDepartmentStatus(Department.UTILITY_COMPANIES);
            if (utilityStatus != ResponseStatus.NOT_RESPONSIBLE) {
                activeReports.add(report);
            }
        }
    }

    @FXML
    private void handleSubmitStatus() {
        if (selectedReport == null) {
            showAlert("Error", "Please select a report first.");
            return;
        }

        ResponseStatus selectedStatus = statusComboBox.getValue();
        Department selectedSubDept = subDepartmentComboBox.getValue();

        if (selectedStatus == null) {
            showAlert("Error", "Please select a status.");
            return;
        }

        if (selectedSubDept == null) {
            showAlert("Error", "Please select a sub-department.");
            return;
        }

        utilityCompanies.updateStatus(selectedReport, selectedStatus);
        utilityCompanies.updateSubDepartmentStatus(selectedReport, selectedSubDept, selectedStatus);
        showAlert("Success", "Status updated successfully for " + selectedSubDept.getDisplayName() + ".");
        loadActiveReports();
        reportTable.refresh();
    }

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

        String logEntry = String.format("[%s] %s: %s", timestamp, currentSubDepartment.getDisplayName(), newEntry);

        utilityCompanies.addCommunicationLogEntry(selectedReport, logEntry);
        updateCommunicationLog(selectedReport);

        newLogEntryField.clear();
    }

    private void updateReportDetails(Report report) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(report.getId()).append("\n");
        details.append("Type: ").append(report.getDisasterType()).append("\n");
        details.append("Location: ").append(report.getLocation()).append("\n");
        details.append("Date/Time: ").append(report.getDateTime()).append("\n");
        details.append("Reporter: ").append(report.getReporterName()).append("\n");
        details.append("Contact: ").append(report.getContactInfo()).append("\n");
        details.append("Status: ").append(report.getResponseStatus()).append("\n");
        details.append("Priority: ").append(report.getPriorityLevel()).append("\n");

        if (currentSubDepartment != null) {
            details.append(currentSubDepartment.getDisplayName())
                    .append(" Status: ")
                    .append(report.getDepartmentStatus(currentSubDepartment).getDisplayName())
                    .append("\n");
        }

        reportDetailsArea.setText(details.toString());
    }

    private void updateCommunicationLog(Report report) {
        communicationLogArea.setText(report.getCommunicationLog());
    }

    private void updateResourcesNeeded(Report report) {
        resourcesNeededArea.setText(report.getResourcesNeeded());
    }

    @FXML
    private void handleRefreshReports() {
        loadActiveReports();
        reportTable.refresh();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
        userLabel.setText("Logged in as: " + username);
    }

    public void setSubDepartment(String subDepartment) {
        try {
            String enumConstant = subDepartment.toUpperCase().replace("_", "_");
            currentSubDepartment = Department.valueOf(enumConstant);

            subDepartmentComboBox.getItems().clear();
            subDepartmentComboBox.getItems().add(currentSubDepartment);
            subDepartmentComboBox.getSelectionModel().select(0);
            subDepartmentComboBox.setDisable(true);

            utilityStatusColumn.setText(currentSubDepartment.getDisplayName() + " Status");

            System.out.println("Sub-department set successfully: " + currentSubDepartment.getDisplayName());

            loadActiveReports();
            reportTable.refresh();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid sub-department: " + subDepartment);
        }
    }

    private SimpleStringProperty getUtilityStatus(TableColumn.CellDataFeatures<Report, String> cellData) {
        if (currentSubDepartment != null) {
            return new SimpleStringProperty(cellData.getValue().getDepartmentStatus(currentSubDepartment).getDisplayName());
        } else {
            return new SimpleStringProperty(cellData.getValue().getDepartmentStatus(Department.UTILITY_COMPANIES).getDisplayName());
        }
    }
}
