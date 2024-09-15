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
 * Represents the Law Enforcement department in the disaster response system.
 * This class implements the Actioner interface and provides methods for
 * updating report statuses and communication logs.
 *
 * @author 12223508
 */
public class LawEnforcement implements Actioner {

    private DisasterType currentDisaster;
    private String departmentName;

    /**
     * Constructs a new LawEnforcement object. Initializes the department name
     * to "Law Enforcement".
     */
    public LawEnforcement() {
        this.departmentName = "Law Enforcement";
    }

    /**
     * Gets the name of the department.
     *
     * @return The name of the department.
     */
    @Override
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Provides information about the current status of the Law Enforcement
     * department.
     *
     * @return A string indicating that Law Enforcement is ready to respond.
     */
    @Override
    public String informStatus() {
        return "Law Enforcement is ready to respond to incidents.";
    }

    /**
     * Updates the status of the Law Enforcement department for a given report.
     *
     * @param report The report to update.
     * @param status The new status to set.
     */
    public void updateStatus(Report report, ResponseStatus status) {
        report.setDepartmentStatus(Department.LAW_ENFORCEMENT, status);
        // Update the database
        updateStatusInDatabase(report.getId(), status);
    }

    /**
     * Adds a new entry to the communication log of a given report.
     *
     * @param report The report to update.
     * @param entry The new log entry to add.
     */
    public void addCommunicationLogEntry(Report report, String entry) {
        String currentLog = report.getCommunicationLog();
        String updatedLog = currentLog + "\n" + entry;
        report.setCommunicationLog(updatedLog);
        // Update the database
        updateCommunicationLogInDatabase(report.getId(), updatedLog);
    }

    /**
     * Updates the Law Enforcement status for a report in the database.
     *
     * @param reportId The ID of the report to update.
     * @param status The new status to set.
     */
    private void updateStatusInDatabase(int reportId, ResponseStatus status) {
        String sql = "UPDATE reports SET law_enforcement_status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, reportId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the communication log for a report in the database.
     *
     * @param reportId The ID of the report to update.
     * @param log The new communication log content.
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
     * @return A list of active reports for Law Enforcement.
     */
    public List<Report> getActiveReports() {
        List<Report> activeReports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE response_status IN ('Pending', 'In Progress') "
                + "AND (assigned_department LIKE '%Law Enforcement%' OR law_enforcement_status IS NOT NULL)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Report report = createReportFromResultSet(rs);
                if (report.getDepartmentStatus(Department.LAW_ENFORCEMENT) != ResponseStatus.NOT_RESPONSIBLE) {
                    activeReports.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load active reports: " + e.getMessage());
        }
        return activeReports;
    }

    /**
     * Creates a Report object from a ResultSet.
     *
     * @param rs The ResultSet containing report data
     * @return A new Report object
     * @throws SQLException if a database access error occurs
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

        String lawEnforcementStatus = rs.getString("law_enforcement_status");
        if (lawEnforcementStatus != null) {
            report.setDepartmentStatus(Department.LAW_ENFORCEMENT, ResponseStatus.valueOf(lawEnforcementStatus));
        }

        return report;
    }

    /**
     * Appends disaster-specific details to the StringBuilder.
     *
     * @param details The StringBuilder to append to
     * @param report The Report object containing the details
     */
    public void appendDisasterSpecificDetails(StringBuilder details, Report report) {
        String sql = "SELECT * FROM reports WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
            System.out.println("Failed to fetch report details: " + e.getMessage());
        }
    }
}