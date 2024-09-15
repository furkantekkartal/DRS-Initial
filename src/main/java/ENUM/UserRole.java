package ENUM;

/**
 * Enum representing different user roles in the system.
 * This enum includes various departments and utilities involved in emergency management.
 * 
 * @author 12223508
 */
public enum UserRole {
    Admin,
    Coordinator,
    FireDepartment,
    HealthDepartment,
    LawEnforcement,
    Meteorology,
    Geoscience,
    UtilityCompanies,
    Utility_Electricity,
    Utility_Water,
    Utility_Gas,
    Utility_Telecommunications;
    
    /**
     * Checks if the current role is a specific utility.
     * 
     * @return true if the role name starts with "Utility_", false otherwise
     */
    public boolean isSubUtility() {
        return this.name().startsWith("Utility_");
    }
}