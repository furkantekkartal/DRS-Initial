package Model;

import javafx.beans.property.*;
import ENUM.Department;
import ENUM.ResponseStatus;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a disaster report with various properties and methods to manage
 * the report data.
 * 
 * @author 12223508
 */
public class Report {

    // Basic report information
    private final IntegerProperty id;
    private final StringProperty disasterType;
    private final StringProperty location;
    private final StringProperty dateTime;
    private final StringProperty reporterName;
    private final StringProperty contactInfo;
    private final StringProperty createdAt;
    private final DoubleProperty latitude;
    private final DoubleProperty longitude;

    // Wildfire specific fields
    private final StringProperty fireIntensity;
    private final StringProperty affectedAreaSize;
    private final StringProperty nearbyInfrastructure;

    // Hurricane specific fields
    private final StringProperty windSpeed;
    private final BooleanProperty floodRisk;
    private final StringProperty evacuationStatus;

    // Earthquake specific fields
    private final StringProperty magnitude;
    private final StringProperty depth;
    private final BooleanProperty aftershocksExpected;

    // Flood specific fields
    private final StringProperty waterLevel;
    private final StringProperty floodEvacuationStatus;
    private final StringProperty infrastructureDamage;

    // Landslide specific fields
    private final StringProperty slopeStability;
    private final StringProperty blockedRoads;
    private final StringProperty casualtiesInjuries;

    // Other disaster specific fields
    private final StringProperty disasterDescription;
    private final StringProperty estimatedImpact;

    // Response and management fields
    private final StringProperty responseStatus;
    private final StringProperty assignedDepartment;
    private final StringProperty resourcesNeeded;
    private final StringProperty communicationLog;
    private final StringProperty priorityLevel;

    private Map<Department, ResponseStatus> departmentStatuses;

    /**
     * Constructs a new Report object with the given parameters.
     *
     * @param id The unique identifier for the report
     * @param disasterType The type of disaster
     * @param location The location of the disaster
     * @param latitude The latitude coordinate of the disaster location
     * @param longitude The longitude coordinate of the disaster location
     * @param dateTime The date and time of the disaster occurrence
     * @param reporterName The name of the person reporting the disaster
     * @param contactInfo The contact information of the reporter
     * @param responseStatus The current status of the response to the disaster
     */
    public Report(int id, String disasterType, String location, Double latitude, Double longitude,
            String dateTime, String reporterName, String contactInfo, String responseStatus) {
        this.id = new SimpleIntegerProperty(id);
        this.disasterType = new SimpleStringProperty(disasterType);
        this.location = new SimpleStringProperty(location);
        this.latitude = new SimpleDoubleProperty(latitude);
        this.longitude = new SimpleDoubleProperty(longitude);
        this.dateTime = new SimpleStringProperty(dateTime);
        this.reporterName = new SimpleStringProperty(reporterName);
        this.contactInfo = new SimpleStringProperty(contactInfo);
        this.responseStatus = new SimpleStringProperty(responseStatus);

        // Initialize other fields
        this.createdAt = new SimpleStringProperty("");
        this.fireIntensity = new SimpleStringProperty("");
        this.affectedAreaSize = new SimpleStringProperty("");
        this.nearbyInfrastructure = new SimpleStringProperty("");
        this.windSpeed = new SimpleStringProperty("");
        this.floodRisk = new SimpleBooleanProperty(false);
        this.evacuationStatus = new SimpleStringProperty("");
        this.magnitude = new SimpleStringProperty("");
        this.depth = new SimpleStringProperty("");
        this.aftershocksExpected = new SimpleBooleanProperty(false);
        this.waterLevel = new SimpleStringProperty("");
        this.floodEvacuationStatus = new SimpleStringProperty("");
        this.infrastructureDamage = new SimpleStringProperty("");
        this.slopeStability = new SimpleStringProperty("");
        this.blockedRoads = new SimpleStringProperty("");
        this.casualtiesInjuries = new SimpleStringProperty("");
        this.disasterDescription = new SimpleStringProperty("");
        this.estimatedImpact = new SimpleStringProperty("");
        this.assignedDepartment = new SimpleStringProperty("");
        this.resourcesNeeded = new SimpleStringProperty("");
        this.communicationLog = new SimpleStringProperty("");
        this.priorityLevel = new SimpleStringProperty("");

        this.departmentStatuses = new EnumMap<>(Department.class);
    }

    // Getters and setters for basic report information
    /**
     * @return The report's unique identifier
     */
    public int getId() {
        return id.get();
    }

    /**
     * @return The IntegerProperty for the report's unique identifier
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * @return The disaster type
     */
    public String getDisasterType() {
        return disasterType.get();
    }

    /**
     * @param value The new disaster type
     */
    public void setDisasterType(String value) {
        disasterType.set(value);
    }

    /**
     * @return The StringProperty for the disaster type
     */
    public StringProperty disasterTypeProperty() {
        return disasterType;
    }

    /**
     * @return The location of the disaster
     */
    public String getLocation() {
        return location.get();
    }

    /**
     * @param value The new location of the disaster
     */
    public void setLocation(String value) {
        location.set(value);
    }

    /**
     * @return The StringProperty for the location
     */
    public StringProperty locationProperty() {
        return location;
    }

    /**
     * @return The latitude of the disaster location
     */
    public double getLatitude() {
        return latitude.get();
    }

    /**
     * @param value The new latitude of the disaster location
     */
    public void setLatitude(double value) {
        latitude.set(value);
    }

    /**
     * @return The DoubleProperty for the latitude
     */
    public DoubleProperty latitudeProperty() {
        return latitude;
    }

    /**
     * @return The longitude of the disaster location
     */
    public double getLongitude() {
        return longitude.get();
    }

    /**
     * @param value The new longitude of the disaster location
     */
    public void setLongitude(double value) {
        longitude.set(value);
    }

    /**
     * @return The DoubleProperty for the longitude
     */
    public DoubleProperty longitudeProperty() {
        return longitude;
    }

    /**
     * @return The date and time of the disaster occurrence
     */
    public String getDateTime() {
        return dateTime.get();
    }

    /**
     * @param value The new date and time of the disaster occurrence
     */
    public void setDateTime(String value) {
        dateTime.set(value);
    }

    /**
     * @return The StringProperty for the date and time
     */
    public StringProperty dateTimeProperty() {
        return dateTime;
    }

    /**
     * @return The name of the reporter
     */
    public String getReporterName() {
        return reporterName.get();
    }

    /**
     * @param value The new name of the reporter
     */
    public void setReporterName(String value) {
        reporterName.set(value);
    }

    /**
     * @return The StringProperty for the reporter's name
     */
    public StringProperty reporterNameProperty() {
        return reporterName;
    }

    /**
     * @return The contact information of the reporter
     */
    public String getContactInfo() {
        return contactInfo.get();
    }

    /**
     * @param value The new contact information of the reporter
     */
    public void setContactInfo(String value) {
        contactInfo.set(value);
    }

    /**
     * @return The StringProperty for the contact information
     */
    public StringProperty contactInfoProperty() {
        return contactInfo;
    }

    /**
     * @return The creation time of the report
     */
    public String getCreatedAt() {
        return createdAt.get();
    }

    /**
     * @param value The new creation time of the report
     */
    public void setCreatedAt(String value) {
        createdAt.set(value);
    }

    /**
     * @return The StringProperty for the creation time
     */
    public StringProperty createdAtProperty() {
        return createdAt;
    }

    // Getters and setters for wildfire specific fields
    /**
     * @return The fire intensity
     */
    public String getFireIntensity() {
        return fireIntensity.get();
    }

    /**
     * @param value The new fire intensity
     */
    public void setFireIntensity(String value) {
        fireIntensity.set(value);
    }

    /**
     * @return The StringProperty for the fire intensity
     */
    public StringProperty fireIntensityProperty() {
        return fireIntensity;
    }

    /**
     * @return The affected area size
     */
    public String getAffectedAreaSize() {
        return affectedAreaSize.get();
    }

    /**
     * @param value The new affected area size
     */
    public void setAffectedAreaSize(String value) {
        affectedAreaSize.set(value);
    }

    /**
     * @return The StringProperty for the affected area size
     */
    public StringProperty affectedAreaSizeProperty() {
        return affectedAreaSize;
    }

    /**
     * @return The nearby infrastructure information
     */
    public String getNearbyInfrastructure() {
        return nearbyInfrastructure.get();
    }

    /**
     * @param value The new nearby infrastructure information
     */
    public void setNearbyInfrastructure(String value) {
        nearbyInfrastructure.set(value);
    }

    /**
     * @return The StringProperty for the nearby infrastructure
     */
    public StringProperty nearbyInfrastructureProperty() {
        return nearbyInfrastructure;
    }

    // Getters and setters for hurricane specific fields
    /**
     * @return The wind speed
     */
    public String getWindSpeed() {
        return windSpeed.get();
    }

    /**
     * @param value The new wind speed
     */
    public void setWindSpeed(String value) {
        windSpeed.set(value);
    }

    /**
     * @return The StringProperty for the wind speed
     */
    public StringProperty windSpeedProperty() {
        return windSpeed;
    }

    /**
     * @return The flood risk status
     */
    public boolean getFloodRisk() {
        return floodRisk.get();
    }

    /**
     * @param value The new flood risk status
     */
    public void setFloodRisk(boolean value) {
        floodRisk.set(value);
    }

    /**
     * @return The BooleanProperty for the flood risk
     */
    public BooleanProperty floodRiskProperty() {
        return floodRisk;
    }

    /**
     * @return The evacuation status
     */
    public String getEvacuationStatus() {
        return evacuationStatus.get();
    }

    /**
     * @param value The new evacuation status
     */
    public void setEvacuationStatus(String value) {
        evacuationStatus.set(value);
    }

    /**
     * @return The StringProperty for the evacuation status
     */
    public StringProperty evacuationStatusProperty() {
        return evacuationStatus;
    }

    // Getters and setters for earthquake specific fields
    /**
     * @return The earthquake magnitude
     */
    public String getMagnitude() {
        return magnitude.get();
    }

    /**
     * @param value The new earthquake magnitude
     */
    public void setMagnitude(String value) {
        magnitude.set(value);
    }

    /**
     * @return The StringProperty for the earthquake magnitude
     */
    public StringProperty magnitudeProperty() {
        return magnitude;
    }

    /**
     * @return The earthquake depth
     */
    public String getDepth() {
        return depth.get();
    }

    /**
     * @param value The new earthquake depth
     */
    public void setDepth(String value) {
        depth.set(value);
    }

    /**
     * @return The StringProperty for the earthquake depth
     */
    public StringProperty depthProperty() {
        return depth;
    }

    /**
     * @return Whether aftershocks are expected
     */
    public boolean getAftershocksExpected() {
        return aftershocksExpected.get();
    }

    /**
     * @param value The new aftershocks expectation status
     */
    public void setAftershocksExpected(boolean value) {
        aftershocksExpected.set(value);
    }

    /**
     * @return The BooleanProperty for aftershocks expectation
     */
    public BooleanProperty aftershocksExpectedProperty() {
        return aftershocksExpected;
    }

    // Getters and setters for flood specific fields
    /**
     * @return The water level
     */
    public String getWaterLevel() {
        return waterLevel.get();
    }

    /**
     * @param value The new water level
     */
    public void setWaterLevel(String value) {
        waterLevel.set(value);
    }

    /**
     * @return The StringProperty for the water level
     */
    public StringProperty waterLevelProperty() {
        return waterLevel;
    }

    /**
     * @return The flood evacuation status
     */
    public String getFloodEvacuationStatus() {
        return floodEvacuationStatus.get();
    }

    /**
     * @param value The new flood evacuation status
     */
    public void setFloodEvacuationStatus(String value) {
        floodEvacuationStatus.set(value);
    }

    /**
     * @return The StringProperty for the flood evacuation status
     */
    public StringProperty floodEvacuationStatusProperty() {
        return floodEvacuationStatus;
    }

    /**
     * @return The infrastructure damage information
     */
    public String getInfrastructureDamage() {
        return infrastructureDamage.get();
    }

    /**
     * @param value The new infrastructure damage information
     */
    public void setInfrastructureDamage(String value) {
        infrastructureDamage.set(value);
    }

    /**
     * @return The StringProperty for the infrastructure damage
     */
    public StringProperty infrastructureDamageProperty() {
        return infrastructureDamage;
    }

    // Getters and setters for landslide specific fields
    /**
     * @return The slope stability information
     */
    public String getSlopeStability() {
        return slopeStability.get();
    }

    /**
     * @param value The new slope stability information
     */
    public void setSlopeStability(String value) {
        slopeStability.set(value);
    }

    /**
     * @return The StringProperty for the slope stability
     */
    public StringProperty slopeStabilityProperty() {
        return slopeStability;
    }

    /**
     * @return The blocked roads information
     */
    public String getBlockedRoads() {
        return blockedRoads.get();
    }

    /**
     * @param value The new blocked roads information
     */
    public void setBlockedRoads(String value) {
        blockedRoads.set(value);
    }

    /**
     * @return The StringProperty for the blocked roads
     */
    public StringProperty blockedRoadsProperty() {
        return blockedRoads;
    }

    /**
     * @return The casualties and injuries information
     */
    public String getCasualtiesInjuries() {
        return casualtiesInjuries.get();
    }

    /**
     * @param value The new casualties and injuries information
     */
    public void setCasualtiesInjuries(String value) {
        casualtiesInjuries.set(value);
    }

    /**
     * @return The StringProperty for the casualties and injuries
     */
    public StringProperty casualtiesInjuriesProperty() {
        return casualtiesInjuries;
    }

    // Getters and setters for other disaster specific fields
    /**
     * @return The disaster description
     */
    public String getDisasterDescription() {
        return disasterDescription.get();
    }

    /**
     * @param value The new disaster description
     */
    public void setDisasterDescription(String value) {
        disasterDescription.set(value);
    }

    /**
     * @return The StringProperty for the disaster description
     */
    public StringProperty disasterDescriptionProperty() {
        return disasterDescription;
    }

    /**
     * @return The estimated impact information
     */
    public String getEstimatedImpact() {
        return estimatedImpact.get();
    }

    /**
     * @param value The new estimated impact information
     */
    public void setEstimatedImpact(String value) {
        estimatedImpact.set(value);
    }

    /**
     * @return The StringProperty for the estimated impact
     */
    public StringProperty estimatedImpactProperty() {
        return estimatedImpact;
    }

    // Getters and setters for response and management fields
    /**
     * @return The response status
     */
    public String getResponseStatus() {
        return responseStatus.get();
    }

    /**
     * @param value The new response status
     */
    public void setResponseStatus(String value) {
        responseStatus.set(value);
    }

    /**
     * @return The StringProperty for the response status
     */
    public StringProperty responseStatusProperty() {
        return responseStatus;
    }

    /**
     * @return The assigned department
     */
    public String getAssignedDepartment() {
        return assignedDepartment.get();
    }

    /**
     * @param value The new assigned department
     */
    public void setAssignedDepartment(String value) {
        assignedDepartment.set(value);
    }

    /**
     * @return The StringProperty for the assigned department
     */
    public StringProperty assignedDepartmentProperty() {
        return assignedDepartment;
    }

    /**
     * @return The resources needed
     */
    public String getResourcesNeeded() {
        return resourcesNeeded.get();
    }

    /**
     * @param value The new resources needed
     */
    public void setResourcesNeeded(String value) {
        resourcesNeeded.set(value);
    }

    /**
     * @return The StringProperty for the resources needed
     */
    public StringProperty resourcesNeededProperty() {
        return resourcesNeeded;
    }

    /**
     * @return The communication log
     */
    public String getCommunicationLog() {
        return communicationLog.get();
    }

    /**
     * @param value The new communication log
     */
    public void setCommunicationLog(String value) {
        communicationLog.set(value);
    }

    /**
     * @return The StringProperty for the communication log
     */
    public StringProperty communicationLogProperty() {
        return communicationLog;
    }

    /**
     * @return The priority level
     */
    public String getPriorityLevel() {
        return priorityLevel.get();
    }

    /**
     * @param value The new priority level
     */
    public void setPriorityLevel(String value) {
        priorityLevel.set(value);
    }

    /**
     * @return The StringProperty for the priority level
     */
    public StringProperty priorityLevelProperty() {
        return priorityLevel;
    }

    /**
     * Sets the response status for a specific department.
     *
     * @param department The department to set the status for
     * @param status The new response status for the department
     */
    public void setDepartmentStatus(Department department, ResponseStatus status) {
        departmentStatuses.put(department, status);
    }

    /**
     * Gets the response status for a specific department.
     *
     * @param department The department to get the status for
     * @return The response status of the department
     */
    public ResponseStatus getDepartmentStatus(Department department) {
        return departmentStatuses.getOrDefault(department, ResponseStatus.NOT_RESPONDED_YET);
    }

    /**
     * @return The response status of the Health Department
     */
    public ResponseStatus getHealthDepartmentStatus() {
        return getDepartmentStatus(Department.HEALTH_DEPARTMENT);
    }

    /**
     * @return The response status of the Fire Department
     */
    public String getFireDeptStatus() {
        return getDepartmentStatus(Department.FIRE_DEPARTMENT).getDisplayName();
    }

    /**
     * @return The response status of the Health Department
     */
    public String getHealthDeptStatus() {
        return getDepartmentStatus(Department.HEALTH_DEPARTMENT).getDisplayName();
    }

    /**
     * @return The response status of Law Enforcement
     */
    public String getLawEnforcementStatus() {
        return getDepartmentStatus(Department.LAW_ENFORCEMENT).getDisplayName();
    }

    /**
     * @return The response status of the Meteorology Department
     */
    public String getMeteorologyStatus() {
        return getDepartmentStatus(Department.METEOROLOGY).getDisplayName();
    }

    /**
     * @return The response status of the Geoscience Department
     */
    public String getGeoscienceStatus() {
        return getDepartmentStatus(Department.GEOSCIENCE).getDisplayName();
    }

    /**
     * @return The response status of Utility Companies
     */
    public String getUtilityCompanyStatus() {
        return getDepartmentStatus(Department.UTILITY_COMPANIES).getDisplayName();
    }
}
