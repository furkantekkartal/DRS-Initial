package ENUM;

/**
 * Enum representing different departments in an emergency management system.
 * This enum includes various departments such as Fire Department, Health Department,
 * Law Enforcement, and different utility companies.
 * 
 * @author 12223508
 */
public enum Department {
    FIRE_DEPARTMENT("Fire Department"),
    HEALTH_DEPARTMENT("Health Department"),
    LAW_ENFORCEMENT("Law Enforcement"),
    METEOROLOGY("Meteorology"),
    GEOSCIENCE("Geoscience"),
    UTILITY_COMPANIES("Utility Companies"),
    UTILITY_ELECTRICITY("Utility - Electricity"),
    UTILITY_WATER("Utility - Water"),
    UTILITY_GAS("Utility - Gas"),
    UTILITY_TELECOMMUNICATIONS("Utility - Telecommunications");

    private final String displayName;

    /**
     * Constructor for Department enum.
     *
     * @param displayName The display name of the department.
     */
    Department(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name of the department.
     *
     * @return The display name of the department.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the display name of the department when the enum is converted to a string.
     *
     * @return The display name of the department.
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Returns an array of utility sub-departments.
     *
     * @return An array containing the utility sub-departments (Electricity, Water, Gas, Telecommunications).
     */
    public static Department[] getUtilitySubDepartments() {
        return new Department[] {
            UTILITY_ELECTRICITY,
            UTILITY_WATER,
            UTILITY_GAS,
            UTILITY_TELECOMMUNICATIONS
        };
    }
}