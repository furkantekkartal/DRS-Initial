package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Util.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Controller class for the User Table view. Manages the display and loading of
 * user data into a TableView.
 * 
 * @author 12223508
 */
public class C_UserTable {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> userNameColumn;

    @FXML
    private TableColumn<User, String> userPasswordColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    /**
     * Initializes the controller. This method is automatically called after the
     * FXML file has been loaded.
     */
    @FXML
    private void initialize() {
        // Set up the cell value factories for each column
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Load user data into the table
        loadUsers();
    }

    /**
     * Loads user data from the database and populates the TableView.
     */
    private void loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT username, password, role FROM users")) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Consider adding proper error handling or logging here
        }

        userTable.setItems(users);
    }

    /**
     * Inner class representing a User entity.
     */
    public static class User {

        private final String username;
        private final String password;
        private final String role;

        /**
         * Constructs a new User object.
         *
         * @param username The user's username
         * @param password The user's password
         * @param role The user's role
         */
        public User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }

        /**
         * @return The user's username
         */
        public String getUsername() {
            return username;
        }

        /**
         * @return The user's password
         */
        public String getPassword() {
            return password;
        }

        /**
         * @return The user's role
         */
        public String getRole() {
            return role;
        }
    }
}
