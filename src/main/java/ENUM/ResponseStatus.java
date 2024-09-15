package ENUM;

/**
 * Represents the various states of a response in an emergency management
 * system. This enum provides a set of predefined response statuses along with
 * their display names.
 * 
 * @author 12223508
 */
public enum ResponseStatus {
    NOT_RESPONSIBLE("N/A"),
    NOT_RESPONDED_YET("Not Responded Yet"),
    WAITING_AVAILABLE_RESOURCE("Waiting for available resources"),
    MOBILIZING("Mobilizing"),
    DEPLOYED_FIELD("Deployed In Field"),
    CONCLUDING("Concluding Operations"),
    COMPLETED("Response Completed");

    private final String displayName;

    /**
     * Constructs a ResponseStatus enum constant with the specified display
     * name.
     *
     * @param displayName The human-readable name for the response status.
     */
    ResponseStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of the response status.
     *
     * @return The display name as a String.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the display name of the response status. This method overrides
     * the default toString() method to provide a more meaningful
     * representation.
     *
     * @return The display name as a String.
     */
    @Override
    public String toString() {
        return displayName;
    }
}
