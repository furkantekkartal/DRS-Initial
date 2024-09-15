package Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class manages the database connection for the disaster response system.
 * It provides methods to retrieve database configuration and establish a connection.
 * 
 * @author 12223508
 */
public class DatabaseConnection {
    
    // Database configuration constants
    // Ensure you change these based on your own setup
    // The disaster_response database will be created automatically
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "disaster_response";
    private static final String USER = "root";
    private static final String PASS = "pass";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DatabaseConnection() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    /**
     * Retrieves the base URL for the database connection.
     * @return The base URL of the database.
     */
    public static String getDB_URL() {
        return DB_URL;
    }

    /**
     * Retrieves the name of the database.
     * @return The name of the database.
     */
    public static String getDB_NAME() {
        return DB_NAME;
    }

    /**
     * Retrieves the username for database access.
     * @return The username for database access.
     */
    public static String getUSER() {
        return USER;
    }

    /**
     * Retrieves the password for database access.
     * @return The password for database access.
     */
    public static String getPASS() {
        return PASS;
    }

    /**
     * Establishes and returns a connection to the database.
     * @return A Connection object representing the database connection.
     * @throws SQLException If a database access error occurs or the URL is null.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL + DB_NAME, USER, PASS);
    }
}