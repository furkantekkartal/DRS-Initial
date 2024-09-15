package Util;

import java.sql.*;

/**
 * This class is responsible for setting up the database for the disaster
 * response system. It creates the necessary database and tables if they don't
 * exist.
 * 
 * @author 12223508
 */
public class DatabaseSetup {

    /**
     * Main method to run the database setup.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        setupDatabase();
    }

    /**
     * Sets up the database by creating necessary tables and structures.
     */
    public static void setupDatabase() {
        Connection conn = null;
        Statement stmt = null;

        try {
            // Step 1: Check connection
            System.out.println("Checking database connection...");
            conn = DriverManager.getConnection(DatabaseConnection.getDB_URL(), DatabaseConnection.getUSER(), DatabaseConnection.getPASS());
            System.out.println("   OK");

            // Step 2: Check if database exists, create if not
            System.out.println("Creating new database...");
            stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DatabaseConnection.getDB_NAME());
            System.out.println("   diseaster_response = OK");

            // Select the database
            stmt.executeUpdate("USE " + DatabaseConnection.getDB_NAME());

            // Step 3: Check if necessary tables exists, create if not
            System.out.println("Creating necessary tables...");

            createReportsTable(stmt);
            createUsersTable(stmt);
            createMeteorologyDataTable(stmt);
            createGeoscienceDataTable(stmt);

            System.out.println("Database setup completed successfully!");

        } catch (SQLException se) {
            System.out.println("SQL Error: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(stmt, conn);
        }
    }

    /**
     * Creates the reports table if it doesn't exist.
     *
     * @param stmt The SQL statement object.
     * @throws SQLException If there's an error executing the SQL statement.
     */
    private static void createReportsTable(Statement stmt) throws SQLException {
        String createReportsTable = "CREATE TABLE IF NOT EXISTS reports ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "disaster_type VARCHAR(50) NOT NULL,"
                + "location VARCHAR(100) NOT NULL,"
                + "latitude DECIMAL(10, 8),"
                + "longitude DECIMAL(11, 8),"
                + "date_time DATETIME NOT NULL,"
                + "reporter_name VARCHAR(100) NOT NULL,"
                + "contact_info VARCHAR(100) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                // Wildfire specific fields
                + "fire_intensity VARCHAR(20),"
                + "affected_area_size VARCHAR(100),"
                + "nearby_infrastructure TEXT,"
                // Hurricane specific fields
                + "wind_speed VARCHAR(50),"
                + "flood_risk BOOLEAN,"
                + "evacuation_status VARCHAR(20),"
                // Earthquake specific fields
                + "magnitude VARCHAR(20),"
                + "depth VARCHAR(50),"
                + "aftershocks_expected BOOLEAN,"
                // Flood specific fields
                + "water_level VARCHAR(20),"
                + "flood_evacuation_status VARCHAR(20),"
                + "infrastructure_damage TEXT,"
                // Landslide specific fields
                + "slope_stability VARCHAR(20),"
                + "blocked_roads TEXT,"
                + "casualties_injuries TEXT,"
                // Other disaster specific fields
                + "disaster_description TEXT,"
                + "estimated_impact VARCHAR(20),"
                // New columns
                + "response_status VARCHAR(20),"
                + "assigned_department VARCHAR(100),"
                + "resources_needed TEXT,"
                + "communication_log TEXT,"
                + "priority_level VARCHAR(20),"
                // Department status columns
                + "fire_department_status VARCHAR(50),"
                + "health_department_status VARCHAR(50),"
                + "law_enforcement_status VARCHAR(50),"
                + "meteorology_status VARCHAR(50),"
                + "geoscience_status VARCHAR(50),"
                + "gis_status VARCHAR(50),"
                + "utility_companies_status VARCHAR(50),"
                + "utility_electricity_status VARCHAR(50),"
                + "utility_water_status VARCHAR(50),"
                + "utility_gas_status VARCHAR(50),"
                + "utility_telecommunications_status VARCHAR(50)"
                + ")";
        stmt.execute(createReportsTable);
        System.out.println("   reports = OK");
    }

    /**
     * Creates the users table if it doesn't exist.
     *
     * @param stmt The SQL statement object.
     * @throws SQLException If there's an error executing the SQL statement.
     */
    private static void createUsersTable(Statement stmt) throws SQLException {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "username VARCHAR(50) NOT NULL UNIQUE,"
                + "password VARCHAR(50) NOT NULL,"
                + "role VARCHAR(50) NOT NULL"
                + ")";
        stmt.execute(createUsersTable);
        System.out.println("   users = OK");
    }

    /**
     * Creates the meteorology_data table if it doesn't exist.
     *
     * @param stmt The SQL statement object.
     * @throws SQLException If there's an error executing the SQL statement.
     */
    private static void createMeteorologyDataTable(Statement stmt) throws SQLException {
        String createMeteorologyTable = "CREATE TABLE IF NOT EXISTS meteorology_data ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "location VARCHAR(255),"
                + "latitude DOUBLE,"
                + "longitude DOUBLE,"
                + "temperature DOUBLE,"
                + "humidity DOUBLE,"
                + "wind_speed DOUBLE,"
                + "wind_direction VARCHAR(50),"
                + "conditions VARCHAR(50),"
                + "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "UNIQUE KEY location_coords (latitude, longitude)"
                + ")";
        stmt.execute(createMeteorologyTable);
        System.out.println("   meteorology_data = OK");
    }

    /**
     * Creates the geoscience_data table if it doesn't exist.
     *
     * @param stmt The SQL statement object.
     * @throws SQLException If there's an error executing the SQL statement.
     */
    private static void createGeoscienceDataTable(Statement stmt) throws SQLException {
        String createGeoscienceDataTable = "CREATE TABLE IF NOT EXISTS geoscience_data ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "location VARCHAR(255),"
                + "latitude DOUBLE,"
                + "longitude DOUBLE,"
                + "magnitude DOUBLE,"
                + "depth DOUBLE,"
                + "aftershocks_expected BOOLEAN,"
                + "UNIQUE KEY location_coords (latitude, longitude)"
                + ")";
        stmt.execute(createGeoscienceDataTable);
        System.out.println("   geoscience_data = OK");
    }

    /**
     * Closes the database resources.
     *
     * @param stmt The SQL statement object to be closed.
     * @param conn The database connection to be closed.
     */
    private static void closeResources(Statement stmt, Connection conn) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
