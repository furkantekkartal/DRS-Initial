package Controller;

import ENUM.*;
import Util.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Controller class for the login functionality of the Disaster Response System.
 *
 * @author 12223508
 */
public class C_Login {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<UserRole> roleComboBox;

    /**
     * Initializes the controller. This method is automatically called after the
     * FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll(
                Arrays.stream(UserRole.values())
                        .filter(role -> !role.isSubUtility() && !role.equals(UserRole.Admin))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Handles the login process when the login button is clicked.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        UserRole selectedRole = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String storedRole = rs.getString("role");

                System.out.println("Stored role: " + storedRole);
                System.out.println("Selected role: " + selectedRole);

                if (!password.equals(storedPassword)) {
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect password.");
                    return;
                }

                if (storedRole.equalsIgnoreCase("admin")) {
                    // Admin can access any role
                    openDepartmentInterface(selectedRole, true, username);
                } else if (storedRole.equalsIgnoreCase(selectedRole.toString())) {
                    // User can access their assigned role
                    openDepartmentInterface(selectedRole, false, username);
                } else if (storedRole.startsWith("Utility_")) {
                    // Utility sub-department users are directed to UtilityCompanies interface
                    openDepartmentInterface(UserRole.UtilityCompanies, false, username, storedRole);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Access Denied", "You don't have access to the selected department.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Username does not exist.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error connecting to the database: " + e.getMessage());
        }
    }

    /**
     * Opens the appropriate department interface based on the user's role.
     *
     * @param role The role of the user
     * @param isAdmin Whether the user is an admin
     * @param username The username of the user
     */
    private void openDepartmentInterface(UserRole role, boolean isAdmin, String username) {
        openDepartmentInterface(role, isAdmin, username, null);
    }

    /**
     * Opens the appropriate department interface based on the user's role and
     * sub-department.
     *
     * @param role The role of the user
     * @param isAdmin Whether the user is an admin
     * @param username The username of the user
     * @param subDepartment The sub-department of the user (for utility
     * companies)
     */
    private void openDepartmentInterface(UserRole role, boolean isAdmin, String username, String subDepartment) {
        String fxmlFile;
        String title;

        switch (role) {
            case Coordinator:
                fxmlFile = "/furkan/coit20258_assignment2/Coordinator.fxml";
                title = "Disaster Response Center";
                break;
            case FireDepartment:
                fxmlFile = "/furkan/coit20258_assignment2/Fire.fxml";
                title = "Fire Department";
                break;
            case HealthDepartment:
                fxmlFile = "/furkan/coit20258_assignment2/Health.fxml";
                title = "Health Department";
                break;
            case LawEnforcement:
                fxmlFile = "/furkan/coit20258_assignment2/LawEnforcement.fxml";
                title = "Law Enforcement";
                break;
            case Meteorology:
                fxmlFile = "/furkan/coit20258_assignment2/Meteorology.fxml";
                title = "Meteorology Department";
                break;
            case Geoscience:
                fxmlFile = "/furkan/coit20258_assignment2/Geoscience.fxml";
                title = "Geoscience Department";
                break;
            case UtilityCompanies:
                fxmlFile = "/furkan/coit20258_assignment2/UtilityCompanies.fxml";
                title = "Utility Companies";
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid department role");
                return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof C_Coordinator) {
                ((C_Coordinator) controller).setCurrentUser(username);
            } else if (controller instanceof C_Fire) {
                ((C_Fire) controller).setCurrentUser(username);
            } else if (controller instanceof C_Health) {
                ((C_Health) controller).setCurrentUser(username);
            } else if (controller instanceof C_LawEnforcement) {
                ((C_LawEnforcement) controller).setCurrentUser(username);
            } else if (controller instanceof C_Meteorology) {
                ((C_Meteorology) controller).setCurrentUser(username);
//            } else if (controller instanceof C_Geoscience) {
//                ((C_Geoscience) controller).setCurrentUser(username);
            } else if (controller instanceof C_UtilityCompanies) {
                C_UtilityCompanies utilityController = (C_UtilityCompanies) controller;
                utilityController.setCurrentUser(username);
                if (subDepartment != null) {
                    System.out.println("Attempting to set sub-department: " + subDepartment);
                    utilityController.setSubDepartment(subDepartment);
                }
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            // Close the login window
            //((Stage) usernameField.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading department interface: " + e.getMessage());
        }
    }

    /**
     * Handles the action to report a disaster.
     */
    @FXML
    private void handleReportDisaster() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/furkan/coit20258_assignment2/report_disaster.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            C_ReportDisaster controller = loader.getController();
            controller.setStage(stage);
            stage.setTitle("Report a Disaster");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Report Disaster interface: " + e.getMessage());
        }
    }

    /**
     * Handles the database setup process.
     */
    @FXML
    private void handleDatabaseSetup() {
        try {
            DatabaseSetup.setupDatabase();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Database setup completed successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to set up database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the loading of sample data into the database.
     */
    @FXML
    private void handleLoadSampleData() {
        try {
            CSVDataImporter.importAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Sample data loaded successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the display of the user table.
     */
    @FXML
    private void handleShowUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/furkan/coit20258_assignment2/user_table.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("User List");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert dialog with the given type, title, and content.
     *
     * @param alertType The type of the alert
     * @param title The title of the alert
     * @param content The content of the alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean validateLogin(String username, String password, UserRole selectedRole) {
        // Check for null or empty inputs
        if (username == null || password == null || selectedRole == null
                || username.isEmpty() || password.isEmpty()) {
            return false;
        }

        // Simulated user data for testing purposes
        if (username.equals("admin") && password.equals("admin") && selectedRole == UserRole.Admin) {
            return true;
        }
        if (username.equals("john.smith") && password.equals("pass") && selectedRole == UserRole.Coordinator) {
            return true;
        }
        if (username.equals("james.martin") && password.equals("pass") && selectedRole == UserRole.UtilityCompanies) {
            return true;
        }

        // If no match found, return false
        return false;
    }
}
