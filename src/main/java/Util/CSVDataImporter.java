package Util;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for importing CSV data into the database tables.
 * 
 * @author 12223508
 */
public class CSVDataImporter {

    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Main method to run the data import process.
     *
     * @param args Command line arguments (not used).
     * @throws IOException If there's an error reading the CSV files.
     * @throws SQLException If there's an error executing SQL statements.
     */
    public static void main(String[] args) throws IOException, SQLException {
        importAllData();
    }

    /**
     * Imports all data from CSV files into respective database tables.
     *
     * @throws IOException If there's an error reading the CSV files.
     * @throws SQLException If there's an error executing SQL statements.
     */
    public static void importAllData() throws IOException, SQLException {
        importReportsData();
        importUserData();
        importMeteorologicalData();
        importGeoscienceData();
    }

    /**
     * Imports user data from CSV file into the users table.
     *
     * @throws IOException If there's an error reading the CSV file.
     * @throws SQLException If there's an error executing SQL statements.
     */
    private static void importUserData() throws IOException, SQLException {
        String csvFile = "src/main/resources/user_data.csv";
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE password = VALUES(password), role = VALUES(role)";
        importData(csvFile, "users", sql, 3);
    }

    /**
     * Imports reports data from CSV file into the reports table.
     *
     * @throws IOException If there's an error reading the CSV file.
     * @throws SQLException If there's an error executing SQL statements.
     */
    private static void importReportsData() throws IOException, SQLException {
        String csvFile = "src/main/resources/exampleDatabase2.csv";
        importData(csvFile, "reports", createReportsSQL(), 42);
    }

    /**
     * Imports meteorological data from CSV file into the meteorology_data
     * table.
     *
     * @throws IOException If there's an error reading the CSV file.
     * @throws SQLException If there's an error executing SQL statements.
     */
    private static void importMeteorologicalData() throws IOException, SQLException {
        String csvFile = "src/main/resources/meteorology_data2.csv";
        importData(csvFile, "meteorology_data", createMeteorologicalSQL(), 8);
    }

    /**
     * Imports geoscience data from CSV file into the geoscience_data table.
     *
     * @throws IOException If there's an error reading the CSV file.
     * @throws SQLException If there's an error executing SQL statements.
     */
    private static void importGeoscienceData() throws IOException, SQLException {
        String csvFile = "src/main/resources/geoscience_data2.csv";
        String sql = "INSERT INTO geoscience_data (location, latitude, longitude, magnitude, depth, aftershocks_expected) "
                + "VALUES (?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE magnitude = VALUES(magnitude), depth = VALUES(depth), "
                + "aftershocks_expected = VALUES(aftershocks_expected)";
        importData(csvFile, "geoscience_data", sql, 6);
    }

    /**
     * Generic method to import data from a CSV file into a specified table.
     *
     * @param csvFile Path to the CSV file.
     * @param tableName Name of the table to import data into.
     * @param sql SQL statement for inserting data.
     * @param expectedColumns Number of expected columns in the CSV file.
     * @throws IOException If there's an error reading the CSV file.
     * @throws SQLException If there's an error executing SQL statements.
     */
    private static void importData(String csvFile, String tableName, String sql, int expectedColumns) throws IOException, SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            // Skip the header line
            br.readLine();

            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] data = parseCsvLine(br, line, expectedColumns);

                if (data.length != expectedColumns) {
                    continue;
                }

                setStatementParameters(pstmt, data, expectedColumns, lineNumber);

                try {
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    // Error handling code here if needed
                }
            }

            System.out.println("Data import completed successfully for " + tableName + ".");
        }
    }

    /**
     * Parses a CSV line, handling quoted fields and multi-line fields.
     *
     * @param br BufferedReader for reading additional lines if needed.
     * @param line The current line being parsed.
     * @param expectedColumns The expected number of columns.
     * @return An array of String values representing the parsed fields.
     * @throws IOException If there's an error reading from the BufferedReader.
     */
    private static String[] parseCsvLine(BufferedReader br, String line, int expectedColumns) throws IOException {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        while (line != null) {
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '"') {
                    inQuotes = !inQuotes;
                    if (inQuotes && i > 0 && line.charAt(i - 1) == '"') {
                        field.append('"');
                    }
                } else if (c == ',' && !inQuotes) {
                    result.add(field.toString());
                    field = new StringBuilder();
                } else {
                    field.append(c);
                }
            }

            if (!inQuotes) {
                result.add(field.toString());
                break;
            } else {
                field.append("\n");
                line = br.readLine();
            }
        }

        // If we have fewer fields than expected, pad with nulls
        while (result.size() < expectedColumns) {
            result.add(null);
        }

        return result.toArray(new String[0]);
    }

    /**
     * Sets the parameters for the PreparedStatement based on the parsed data.
     *
     * @param pstmt The PreparedStatement to set parameters for.
     * @param data The array of data values.
     * @param expectedColumns The expected number of columns.
     * @param lineNumber The current line number being processed.
     * @throws SQLException If there's an error setting statement parameters.
     */
    private static void setStatementParameters(PreparedStatement pstmt, String[] data, int expectedColumns, int lineNumber) throws SQLException {
        for (int i = 0; i < expectedColumns; i++) {
            if (data[i] == null || data[i].equals("NULL")) {
                pstmt.setNull(i + 1, java.sql.Types.VARCHAR);
            } else if (isNumeric(data[i])) {
                pstmt.setDouble(i + 1, Double.parseDouble(data[i]));
            } else if (isDate(data[i])) {
                try {
                    java.util.Date date = inputFormat.parse(data[i]);
                    pstmt.setString(i + 1, outputFormat.format(date));
                } catch (ParseException e) {
                    pstmt.setNull(i + 1, java.sql.Types.TIMESTAMP);
                }
            } else if (data[i].equalsIgnoreCase("true") || data[i].equalsIgnoreCase("false")) {
                pstmt.setBoolean(i + 1, Boolean.parseBoolean(data[i]));
            } else {
                pstmt.setString(i + 1, data[i]);
            }
        }
    }

    /**
     * Creates the SQL statement for inserting data into the reports table.
     *
     * @return The SQL statement as a String.
     */
    private static String createReportsSQL() {
        return "INSERT INTO reports (id, disaster_type, location, latitude, longitude, date_time, reporter_name, contact_info, "
                + "created_at, fire_intensity, affected_area_size, nearby_infrastructure, wind_speed, flood_risk, "
                + "evacuation_status, magnitude, depth, aftershocks_expected, water_level, flood_evacuation_status, "
                + "infrastructure_damage, slope_stability, blocked_roads, casualties_injuries, disaster_description, "
                + "estimated_impact, response_status, assigned_department, resources_needed, communication_log, priority_level, "
                + "fire_department_status, health_department_status, law_enforcement_status, meteorology_status, geoscience_status, gis_status, "
                + "utility_companies_status, utility_electricity_status, utility_water_status, utility_gas_status, utility_telecommunications_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    /**
     * Creates the SQL statement for inserting data into the meteorology_data
     * table.
     *
     * @return The SQL statement as a String.
     */
    private static String createMeteorologicalSQL() {
        return "INSERT INTO meteorology_data (location, latitude, longitude, temperature, humidity, wind_speed, wind_direction, conditions) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    /**
     * Checks if a string can be parsed as a numeric value.
     *
     * @param str The string to check.
     * @return true if the string is numeric, false otherwise.
     */
    private static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if a string can be parsed as a date.
     *
     * @param str The string to check.
     * @return true if the string is a valid date, false otherwise.
     */
    private static boolean isDate(String str) {
        try {
            inputFormat.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
