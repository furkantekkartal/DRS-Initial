package Model;

import ENUM.*;
import Interfaces.*;
import Util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Utility Companies department in the disaster management
 * system. This class manages the status of utility sub-departments and their
 * responses to incidents.
 *
 * @author 12223508
 */
public class UtilityCompanies implements Actioner {

    private DisasterType currentDisaster;
    private String departmentName;
    private Map<Department, ResponseStatus> subDepartmentStatuses;

    /**
     * Constructs a new UtilityCompanies object. Initializes the department name
     * and sets up the initial status for all utility sub-departments.
     */
    public UtilityCompanies() {
        this.departmentName = "Utility Companies";
        this.subDepartmentStatuses = new EnumMap<>(Department.class);
        for (Department subDept : getUtilitySubDepartments()) {
            subDepartmentStatuses.put(subDept, ResponseStatus.NOT_RESPONDED_YET);
        }
    }

    /**
     * Returns the name of the department.
     *
     * @return The department name as a String.
     */
    @Override
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Provides information about the department's readiness status.
     *
     * @return A String indicating the department's readiness to respond.
     */
    @Override
    public String informStatus() {
        return "Utility Companies are ready to respond to incidents.";
    }

    /**
     * Updates the status of the Utility Companies department for a given
     * report.
     *
     * @param report The Report object to update.
     * @param status The new ResponseStatus.
     */
    public void updateStatus(Report report, ResponseStatus status) {
        report.setDepartmentStatus(Department.UTILITY_COMPANIES, status);
        updateStatusInDatabase(report.getId(), Department.UTILITY_COMPANIES, status);
    }

    /**
     * Updates the status of a specific utility sub-department for a given
     * report.
     *
     * @param report The Report object to update.
     * @param subDept The specific utility sub-department.
     * @param status The new ResponseStatus.
     */
    public void updateSubDepartmentStatus(Report report, Department subDept, ResponseStatus status) {
        if (isUtilitySubDepartment(subDept)) {
            subDepartmentStatuses.put(subDept, status);
            report.setDepartmentStatus(subDept, status);
            updateStatusInDatabase(report.getId(), subDept, status);
        }
    }

    /**
     * Retrieves the current status of a specific utility sub-department.
     *
     * @param subDept The utility sub-department to check.
     * @return The current ResponseStatus of the sub-department.
     */
    public ResponseStatus getSubDepartmentStatus(Department subDept) {
        return subDepartmentStatuses.get(subDept);
    }

    /**
     * Adds a new entry to the communication log of a report.
     *
     * @param report The Report object to update.
     * @param entry The new log entry to add.
     */
    public void addCommunicationLogEntry(Report report, String entry) {
        String currentLog = report.getCommunicationLog();
        String updatedLog = currentLog + "\n" + entry;
        report.setCommunicationLog(updatedLog);
        updateCommunicationLogInDatabase(report.getId(), updatedLog);
    }

    /**
     * Updates the status of a department or sub-department in the database.
     *
     * @param reportId The ID of the report to update.
     * @param dept The department or sub-department being updated.
     * @param status The new status to set.
     */
    private void updateStatusInDatabase(int reportId, Department dept, ResponseStatus status) {
        String columnName = dept.name().toLowerCase() + "_status";
        String sql = "UPDATE reports SET " + columnName + " = ? WHERE id = ?";
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
     * Returns an array of all utility sub-departments.
     *
     * @return An array of Department enums representing utility
     * sub-departments.
     */
    public static Department[] getUtilitySubDepartments() {
        return new Department[]{
            Department.UTILITY_ELECTRICITY,
            Department.UTILITY_WATER,
            Department.UTILITY_GAS,
            Department.UTILITY_TELECOMMUNICATIONS
        };
    }

    /**
     * Checks if a given department is a utility sub-department.
     *
     * @param dept The Department to check.
     * @return true if the department is a utility sub-department, false
     * otherwise.
     */
    public static boolean isUtilitySubDepartment(Department dept) {
        return dept == Department.UTILITY_ELECTRICITY || dept == Department.UTILITY_WATER
                || dept == Department.UTILITY_GAS || dept == Department.UTILITY_TELECOMMUNICATIONS;
    }

    /**
     * Loads active reports from the database.
     *
     * @return A list of active reports for Utility Companies.
     */
    public List<Report> getActiveReports() {
        List<Report> activeReports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE response_status IN ('Pending', 'In Progress') "
                + "AND (assigned_department LIKE '%Utility Companies%' OR utility_companies_status IS NOT NULL)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Report report = createReportFromResultSet(rs);
                activeReports.add(report);
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

        for (Department dept : Department.values()) {
            String statusString = rs.getString(dept.name().toLowerCase() + "_status");
            if (statusString != null) {
                report.setDepartmentStatus(dept, ResponseStatus.valueOf(statusString));
            }
        }

        return report;
    }
}
