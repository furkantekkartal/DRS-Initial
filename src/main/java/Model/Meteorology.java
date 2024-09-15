package Model;

import Util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents meteorological data for a specific location.
 *
 * @author 12223508
 */
public class Meteorology {

    private int id;
    private String location;
    private double latitude;
    private double longitude;
    private double temperature;
    private double humidity;
    private String condition;
    private double windSpeed;
    private String windDirection;
    private Timestamp lastUpdated;

    /**
     * Constructs a new Meteorology object with the given parameters.
     *
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @param location The name of the location
     * @param temperature The temperature at the location
     * @param humidity The humidity at the location
     * @param condition The weather condition at the location
     * @param windSpeed The wind speed at the location
     * @param windDirection The wind direction at the location
     */
    public Meteorology(double latitude, double longitude, String location, double temperature, double humidity, String condition, double windSpeed, String windDirection) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Retrieves weather data for the given coordinates from the database.
     *
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @return A Meteorology object with the weather data, or null if not found
     */
    public static Meteorology getWeather(double latitude, double longitude) {
        String sql = "SELECT * FROM meteorology_data WHERE ABS(latitude - ?) < 0.01 AND ABS(longitude - ?) < 0.01 ORDER BY (POW(latitude - ?, 2) + POW(longitude - ?, 2)) ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, latitude);
            pstmt.setDouble(2, longitude);
            pstmt.setDouble(3, latitude);
            pstmt.setDouble(4, longitude);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Meteorology weather = new Meteorology(
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("location"),
                        rs.getDouble("temperature"),
                        rs.getDouble("humidity"),
                        rs.getString("conditions"),
                        rs.getDouble("wind_speed"),
                        rs.getString("wind_direction")
                );
                //System.out.println("Found weather data: " + weather);
                return weather;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("No weather data found for coordinates: " + latitude + ", " + longitude);
        return null;
    }

    /**
     * Inserts or updates weather data in the database.
     *
     * @param weather The Meteorology object containing weather data to be saved
     */
    public static void setWeather(Meteorology weather) {
        String sql = "INSERT INTO meteorology_data (latitude, longitude, location, temperature, humidity, conditions, wind_speed, wind_direction) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE "
                + "temperature = VALUES(temperature), humidity = VALUES(humidity), "
                + "conditions = VALUES(conditions), wind_speed = VALUES(wind_speed), "
                + "wind_direction = VALUES(wind_direction)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, weather.getLatitude());
            pstmt.setDouble(2, weather.getLongitude());
            pstmt.setString(3, weather.getLocation());
            pstmt.setDouble(4, weather.getTemperature());
            pstmt.setDouble(5, weather.getHumidity());
            pstmt.setString(6, weather.getCondition());
            pstmt.setDouble(7, weather.getWindSpeed());
            pstmt.setString(8, weather.getWindDirection());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if weather data exists for the given coordinates in the database.
     *
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     * @return true if weather data exists, false otherwise
     */
    public static boolean hasWeather(double latitude, double longitude) {
        String sql = "SELECT COUNT(*) FROM meteorology_data WHERE latitude = ? AND longitude = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, latitude);
            pstmt.setDouble(2, longitude);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Loads active reports from the database.
     *
     * @return A list of active reports.
     */
    public static List<Report> getActiveReports() {
        List<Report> activeReports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE response_status IN ('Pending', 'In Progress', '')";

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
                activeReports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load active reports: " + e.getMessage());
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
    public static void updateCoordinates(int reportId, double latitude, double longitude) {
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
            System.out.println("Failed to update coordinates: " + e.getMessage());
        }
    }

    /**
     * Adds a new entry to the communication log of a report and updates the
     * database.
     *
     * @param report The report to update
     * @param logEntry The new log entry to add
     */
    public static void addCommunicationLogEntry(Report report, String logEntry) {
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
            System.out.println("Failed to update communication log: " + e.getMessage());
        }
    }

    public WeatherImpact analyzeWeatherImpact(String disasterType) {
        WeatherImpact impact = new WeatherImpact();

        if ("Wildfire".equalsIgnoreCase(disasterType)) {
            if (this.humidity < 20 && this.windSpeed > 30 && this.temperature > 35) {
                impact.setRiskLevel("High Impect");
                impact.setDescription("Extreme fire danger: Very low humidity, high winds, and high temperature significantly increase fire spread risk.");
            } else if (this.humidity < 30 && this.windSpeed > 20 && this.temperature > 30) {
                impact.setRiskLevel("Medium Impect");
                impact.setDescription("High fire danger: Low humidity and strong winds increase fire spread risk.");
            } else {
                impact.setRiskLevel("Low Impect");
                impact.setDescription("Moderate fire danger: Current weather conditions may affect fire behavior.");
            }
        } else if ("Hurricane".equalsIgnoreCase(disasterType)) {
            if (this.windSpeed > 120) {
                impact.setRiskLevel("High Impect");
                impact.setDescription("Extreme hurricane danger: Catastrophic damage will occur. Most areas will be uninhabitable for weeks or months.");
            } else if (this.windSpeed > 90) {
                impact.setRiskLevel("Medium Impect");
                impact.setDescription("High hurricane danger: Extensive damage will occur. Many areas will be uninhabitable for days to weeks.");
            } else if (this.windSpeed > 60) {
                impact.setRiskLevel("Low Impect");
                impact.setDescription("Moderate hurricane danger: Some damage will occur. Some areas may be uninhabitable briefly.");
            } else {
                impact.setRiskLevel("Ineffective");
                impact.setDescription("Wind speeds below hurricane force, but still dangerous conditions possible.");
            }
        } else if ("Flood".equalsIgnoreCase(disasterType)) {
            if (this.condition.contains("Heavy Rain") && this.windSpeed > 30) {
                impact.setRiskLevel("High Impect");
                impact.setDescription("Severe flood risk: Heavy rainfall and strong winds may cause significant flooding and infrastructure damage.");
            } else if (this.condition.contains("Rain") && this.windSpeed > 20) {
                impact.setRiskLevel("Medium Impect");
                impact.setDescription("High flood risk: Rainfall and winds may cause flooding in low-lying areas.");
            } else if (this.condition.contains("Rain")) {
                impact.setRiskLevel("Low Impect");
                impact.setDescription("Moderate flood risk: Rainfall may cause some localized flooding.");
            } else {
                impact.setRiskLevel("Ineffective");
                impact.setDescription("No immediate flood risk based on current weather conditions.");
            }
        } else if ("Earthquake".equalsIgnoreCase(disasterType)) {
            if (this.condition.contains("Rain") || this.windSpeed > 20) {
                impact.setRiskLevel("Medium Impect");
                impact.setDescription("Weather may complicate earthquake response: Rain or strong winds could affect rescue efforts and increase risks of landslides.");
            } else {
                impact.setRiskLevel("Low Impect");
                impact.setDescription("Current weather conditions unlikely to significantly impact earthquake response efforts.");
            }
        } else {
            impact.setRiskLevel("N/A");
            impact.setDescription("Weather impact assessment not available for this disaster type.");
        }

        return impact;
    }

    public static class WeatherImpact {

        private String riskLevel;
        private String description;

        public String getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "Risk Level: " + riskLevel + "\nDescription: " + description;
        }
    }

    /**
     * Returns a string representation of the Meteorology object.
     *
     * @return A string containing all the weather data
     */
    @Override
    public String toString() {
        return String.format("Meteorology toString{\n   lati=%.4f, \n   long=%.4f, \n   location='%s', \n   temp=%.1f, \n   humidity=%.1f, \n   conditions='%s', \n   windSpeed=%.1f, \n   windDirection='%s'}",
                latitude, longitude, location, temperature, humidity, condition, windSpeed, windDirection);
    }
}
