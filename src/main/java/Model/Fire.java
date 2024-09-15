package Model;

import ENUM.*;
import Interfaces.*;
import Util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Fire Department in the disaster response system. This class
 * implements the Actioner interface and provides methods for managing
 * fire-related incidents and communication.
 * 
 * @author 12223508
 */
public class Fire implements Actioner {

    private DisasterType currentDisaster;
    private String departmentName;

    /**
     * Constructs a new Fire department instance. Initializes the department
     * name.
     */
    public Fire() {
        this.departmentName = "Fire Department";
    }

    /**
     * Returns the name of the department.
     *
     * @return The name of the Fire Department.
     */
    @Override
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Provides information about the current status of the Fire Department.
     *
     * @return A string indicating the Fire Department's readiness.
     */
    @Override
    public String informStatus() {
        return "Fire Department is ready to respond to incidents.";
    }

    /**
     * Updates the status of the Fire Department for a given report. This method
     * updates both the report object and the database.
     *
     * @param report The report to be updated.
     * @param status The new response status.
     */
    public void updateStatus(Report report, ResponseStatus status) {
        report.setDepartmentStatus(Department.FIRE_DEPARTMENT, status);
        // Update the database
        updateStatusInDatabase(report.getId(), status);
    }

    /**
     * Adds a new entry to the communication log for a given report. This method
     * updates both the report object and the database.
     *
     * @param report The report to which the log entry will be added.
     * @param entry The new log entry to be added.
     */
    public void addCommunicationLogEntry(Report report, String entry) {
        String currentLog = report.getCommunicationLog();
        String updatedLog = currentLog + "\n" + entry;
        report.setCommunicationLog(updatedLog);
        // Update the database
        updateCommunicationLogInDatabase(report.getId(), updatedLog);
    }

    /**
     * Updates the Fire Department's status for a specific report in the
     * database.
     *
     * @param reportId The ID of the report to be updated.
     * @param status The new response status.
     */
    private void updateStatusInDatabase(int reportId, ResponseStatus status) {
        String sql = "UPDATE reports SET fire_department_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, reportId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the communication log for a specific report in the database.
     *
     * @param reportId The ID of the report to be updated.
     * @param log The updated communication log.
     */
    private void updateCommunicationLogInDatabase(int reportId, String log) {
        String sql = "UPDATE reports SET communication_log = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, log);
            pstmt.setInt(2, reportId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads active reports from the database.
     *
     * @return A list of active reports for the Fire Department.
     */
    public List<Report> getActiveReports() {
        List<Report> activeReports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE response_status IN ('Pending', 'In Progress') "
                + "AND (assigned_department LIKE '%Fire Department%' OR fire_department_status IS NOT NULL)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Report report = createReportFromResultSet(rs);
                String fireStatus = rs.getString("fire_department_status");
                if (fireStatus != null) {
                    report.setDepartmentStatus(Department.FIRE_DEPARTMENT, ResponseStatus.valueOf(fireStatus));
                }

                if (fireStatus != null && !fireStatus.equals(ResponseStatus.NOT_RESPONSIBLE.name())) {
                    activeReports.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeReports;
    }

    /**
     * Creates a Report object from a ResultSet.
     *
     * @param rs The ResultSet containing report data
     * @return A new Report object
     * @throws SQLException If there's an error reading from the ResultSet
     */
    private Report createReportFromResultSet(ResultSet rs) throws SQLException {
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
        report.setResourcesNeeded(rs.getString("resources_needed"));
        report.setPriorityLevel(rs.getString("priority_level"));
        return report;
    }

    /**
     * Appends disaster-specific details to the StringBuilder.
     *
     * @param details The StringBuilder to append to
     * @param report The report containing the disaster information
     */
    public void appendDisasterSpecificDetails(StringBuilder details, Report report) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, report.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    switch (report.getDisasterType()) {
                        case "Wildfire":
                            details.append("Fire Intensity: ").append(rs.getString("fire_intensity")).append("\n");
                            details.append("Affected Area Size: ").append(rs.getString("affected_area_size")).append("\n");
                            details.append("Nearby Infrastructure: ").append(rs.getString("nearby_infrastructure")).append("\n");
                            break;
                        case "Hurricane":
                            details.append("Wind Speed: ").append(rs.getString("wind_speed")).append("\n");
                            details.append("Flood Risk: ").append(rs.getBoolean("flood_risk")).append("\n");
                            details.append("Evacuation Status: ").append(rs.getString("evacuation_status")).append("\n");
                            break;
                        case "Earthquake":
                            details.append("Magnitude: ").append(rs.getString("magnitude")).append("\n");
                            details.append("Depth: ").append(rs.getString("depth")).append("\n");
                            details.append("Aftershocks Expected: ").append(rs.getBoolean("aftershocks_expected")).append("\n");
                            break;
                        case "Flood":
                            details.append("Water Level: ").append(rs.getString("water_level")).append("\n");
                            details.append("Flood Evacuation Status: ").append(rs.getString("flood_evacuation_status")).append("\n");
                            details.append("Infrastructure Damage: ").append(rs.getString("infrastructure_damage")).append("\n");
                            break;
                        case "Landslide":
                            details.append("Slope Stability: ").append(rs.getString("slope_stability")).append("\n");
                            details.append("Blocked Roads: ").append(rs.getString("blocked_roads")).append("\n");
                            details.append("Casualties/Injuries: ").append(rs.getString("casualties_injuries")).append("\n");
                            break;
                        default:
                            details.append("Disaster Description: ").append(rs.getString("disaster_description")).append("\n");
                            details.append("Estimated Impact: ").append(rs.getString("estimated_impact")).append("\n");
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}