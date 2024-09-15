package Model;

import java.util.ArrayList;
import java.util.List;
import ENUM.DisasterType;
import Util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Geoscience class represents a geoscience department that manages
 * geographical and seismic data. It provides methods to access and manipulate
 * geoscience data stored in a database.
 *
 * @author 12223508
 */
public class Geoscience {

    private List<String> locations;
    private List<Double> latitudes;
    private List<Double> longitudes;
    private List<Double> magnitudes;
    private List<Double> depths;
    private List<Boolean> aftershocksExpected;
    private DisasterType currentDisaster;

    /**
     * Constructs a new Geoscience object with the specified disaster type.
     *
     * @param disasterType The type of disaster associated with this geoscience
     * data.
     */
    public Geoscience(DisasterType disasterType) {
        this.currentDisaster = disasterType;
        initializeData();
    }

    /**
     * Initializes the geoscience data by loading it from the database.
     */
    private void initializeData() {
        locations = new ArrayList<>();
        latitudes = new ArrayList<>();
        longitudes = new ArrayList<>();
        magnitudes = new ArrayList<>();
        depths = new ArrayList<>();
        aftershocksExpected = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT location, latitude, longitude, magnitude, depth, aftershocks_expected FROM geoscience_data")) {

            while (rs.next()) {
                locations.add(rs.getString("location"));
                latitudes.add(rs.getDouble("latitude"));
                longitudes.add(rs.getDouble("longitude"));
                magnitudes.add(rs.getDouble("magnitude"));
                depths.add(rs.getDouble("depth"));
                aftershocksExpected.add(rs.getBoolean("aftershocks_expected"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading data from database: " + e.getMessage());
        }
    }

    /**
     * Returns the name of the location at the specified index.
     *
     * @param index The index of the location.
     * @return The name of the location, or null if the index is out of bounds.
     */
    public String getLocationName(int index) {
        return (index >= 0 && index < locations.size()) ? locations.get(index) : null;
    }

    /**
     * Returns the latitude of the location at the specified index.
     *
     * @param index The index of the location.
     * @return The latitude of the location, or Double.NaN if the index is out
     * of bounds.
     */
    public double getLatitude(int index) {
        return (index >= 0 && index < latitudes.size()) ? latitudes.get(index) : Double.NaN;
    }

    /**
     * Returns the longitude of the location at the specified index.
     *
     * @param index The index of the location.
     * @return The longitude of the location, or Double.NaN if the index is out
     * of bounds.
     */
    public double getLongitude(int index) {
        return (index >= 0 && index < longitudes.size()) ? longitudes.get(index) : Double.NaN;
    }

    /**
     * Returns the magnitude of the seismic event at the specified index.
     *
     * @param index The index of the seismic event.
     * @return The magnitude of the seismic event, or Double.NaN if the index is
     * out of bounds.
     */
    public double getMagnitude(int index) {
        return (index >= 0 && index < magnitudes.size()) ? magnitudes.get(index) : Double.NaN;
    }

    /**
     * Returns the depth of the seismic event at the specified index.
     *
     * @param index The index of the seismic event.
     * @return The depth of the seismic event, or Double.NaN if the index is out
     * of bounds.
     */
    public double getDepth(int index) {
        return (index >= 0 && index < depths.size()) ? depths.get(index) : Double.NaN;
    }

    /**
     * Returns whether aftershocks are expected for the seismic event at the
     * specified index.
     *
     * @param index The index of the seismic event.
     * @return True if aftershocks are expected, false otherwise or if the index
     * is out of bounds.
     */
    public boolean getAftershocksExpected(int index) {
        return (index >= 0 && index < aftershocksExpected.size()) ? aftershocksExpected.get(index) : false;
    }

    /**
     * Returns the total number of locations in the dataset.
     *
     * @return The number of locations.
     */
    public int getLocationCount() {
        return locations.size();
    }

    /**
     * Finds the index of a location by its name.
     *
     * @param locationName The name of the location to find.
     * @return The index of the location, or -1 if not found.
     */
    public int findLocationIndex(String locationName) {
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).equalsIgnoreCase(locationName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the coordinates (latitude and longitude) of a location by its
     * name.
     *
     * @param locationName The name of the location.
     * @return An array containing the latitude and longitude, or null if the
     * location is not found.
     */
    public double[] getCoordinates(String locationName) {
        int index = findLocationIndex(locationName);
        if (index != -1) {
            return new double[]{latitudes.get(index), longitudes.get(index)};
        }
        return null;
    }

    /**
     * Retrieves geoscience data for a specific location from the database.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A GISData object containing the geoscience data, or null if not
     * found.
     */
    public GISData getData(double latitude, double longitude) {
        String sql = "SELECT * FROM geoscience_data WHERE latitude = ? AND longitude = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, latitude);
            pstmt.setDouble(2, longitude);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new GISData(
                        rs.getString("location"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getDouble("magnitude"),
                        rs.getDouble("depth"),
                        rs.getBoolean("aftershocks_expected")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserts or updates geoscience data in the database.
     *
     * @param data The GISData object containing the geoscience data to be
     * saved.
     */
    public void setData(GISData data) {
        String sql = "INSERT INTO geoscience_data (location, latitude, longitude, magnitude, depth, aftershocks_expected) "
                + "VALUES (?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "magnitude = VALUES(magnitude), depth = VALUES(depth), "
                + "aftershocks_expected = VALUES(aftershocks_expected)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data.getLocation());
            pstmt.setDouble(2, data.getLatitude());
            pstmt.setDouble(3, data.getLongitude());
            pstmt.setDouble(4, data.getMagnitude());
            pstmt.setDouble(5, data.getDepth());
            pstmt.setBoolean(6, data.isAftershocksExpected());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                //out.println("Geoscience data saved successfully. Affected rows: " + affectedRows);
            } else {
                System.out.println("No rows affected. Data might not have been saved.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error saving geoscience data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the name of the department.
     *
     * @return The department name.
     */
    public String getDepartmentName() {
        return "GIS";
    }

    /**
     * Returns the current status of the GIS department.
     *
     * @return The status update string.
     */
    public String getStatusUpdate() {
        return "GIS Department Status: Active";
    }

    /**
     * Loads active reports from the database.
     *
     * @return A list of active reports.
     */
    public List<Report> getActiveReports() {
        List<Report> activeReports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE response_status IN ('Pending', 'In Progress')";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Report report = new Report(
                        rs.getInt("id"),
                        rs.getString("disaster_type"),
                        rs.getString("location"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("date_time"),
                        rs.getString("reporter_name"),
                        rs.getString("contact_info"),
                        rs.getString("response_status")
                );
                report.setCommunicationLog(rs.getString("communication_log"));
                report.setPriorityLevel(rs.getString("priority_level"));
                activeReports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading active reports: " + e.getMessage());
        }
        return activeReports;
    }

    /**
     * Updates the coordinates of a report in the database.
     *
     * @param reportId The ID of the report
     * @param latitude The new latitude
     * @param longitude The new longitude
     */
    public void updateCoordinates(int reportId, double latitude, double longitude) {
        String sql = "UPDATE reports SET latitude = ?, longitude = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, latitude);
            pstmt.setDouble(2, longitude);
            pstmt.setInt(3, reportId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Coordinates updated successfully in the database.");
            } else {
                System.out.println("Failed to update coordinates in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating coordinates: " + e.getMessage());
        }
    }

    /**
     * Adds a new entry to the communication log for a given report and updates
     * the database.
     *
     * @param report The report to update
     * @param logEntry The new log entry to add
     */
    public void addCommunicationLogEntry(Report report, String logEntry) {
        String currentLog = report.getCommunicationLog();
        String updatedLog = currentLog.isEmpty() ? logEntry : currentLog + "\n" + logEntry;
        report.setCommunicationLog(updatedLog);

        String sql = "UPDATE reports SET communication_log = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, updatedLog);
            pstmt.setInt(2, report.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Communication log updated successfully in the database.");
            } else {
                System.out.println("Failed to update communication log in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error updating communication log: " + e.getMessage());
        }
    }

    /**
     * Inner class to represent Geoscience data for a specific location.
     */
    public static class GISData {

        private String location;
        private double latitude;
        private double longitude;
        private double magnitude;
        private double depth;
        private boolean aftershocksExpected;

        /**
         * Constructs a new GISData object with the specified parameters.
         *
         * @param location The name of the location.
         * @param latitude The latitude of the location.
         * @param longitude The longitude of the location.
         * @param magnitude The magnitude of the seismic event.
         * @param depth The depth of the seismic event.
         * @param aftershocksExpected Whether aftershocks are expected.
         */
        public GISData(String location, double latitude, double longitude, double magnitude, double depth, boolean aftershocksExpected) {
            this.location = location;
            this.latitude = latitude;
            this.longitude = longitude;
            this.magnitude = magnitude;
            this.depth = depth;
            this.aftershocksExpected = aftershocksExpected;
        }

        // Getters
        public String getLocation() {
            return location;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public double getMagnitude() {
            return magnitude;
        }

        public double getDepth() {
            return depth;
        }

        public boolean isAftershocksExpected() {
            return aftershocksExpected;
        }
    }
}
