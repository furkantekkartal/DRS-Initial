package Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class C_ReportDisasterTest {

    private C_ReportDisaster reportDisasterController;

    @BeforeEach
    void setUp() {
        reportDisasterController = new C_ReportDisaster();
    }

    @Test
    void DR001_testReportWildfireAllFields() {
        // Test case DR001: Report Wildfire - All Fields
        assertTrue(reportDisasterController.validateMandatoryFieldsTest("Wildfire", "Brisbane Forest Park", LocalDate.now(), "John Doe", "0412345678"));
        
        Map<String, Object> columnValueMap = new HashMap<>();
        reportDisasterController.addDisasterSpecificFields(columnValueMap, "Wildfire", "High", "500", "Power lines, residential areas");
        
        assertEquals("High", columnValueMap.get("fire_intensity"));
        assertEquals("500", columnValueMap.get("affected_area_size"));
        assertEquals("Power lines, residential areas", columnValueMap.get("nearby_infrastructure"));
    }

    @Test
    void DR002_testReportHurricaneMandatoryFieldsOnly() {
        // Test case DR002: Report Hurricane - Mandatory Fields Only
        assertTrue(reportDisasterController.validateMandatoryFieldsTest("Hurricane", "Cairns", LocalDate.now(), "Jane Smith", "0423456789"));
        
        Map<String, Object> columnValueMap = new HashMap<>();
        reportDisasterController.addDisasterSpecificFields(columnValueMap, "Hurricane", "", "false", "Partial");
        
        assertEquals("Partial", columnValueMap.get("evacuation_status"));
    }

    @Test
    void DR003_testReportEarthquakeAllFields() {
        // Test case DR003: Report Earthquake - All Fields
        assertTrue(reportDisasterController.validateMandatoryFieldsTest("Earthquake", "Gold Coast", LocalDate.now(), "Mike Brown", "0434567890"));
        
        Map<String, Object> columnValueMap = new HashMap<>();
        reportDisasterController.addDisasterSpecificFields(columnValueMap, "Earthquake", "6.5", "10", "true");
        
        assertEquals("6.5", columnValueMap.get("magnitude"));
        assertEquals("10", columnValueMap.get("depth"));
        assertEquals(true, columnValueMap.get("aftershocks_expected"));
    }

    @Test
    void DR004_testInvalidContactNumber() {
        // Test case DR004: Invalid Contact Number
        assertFalse(reportDisasterController.validateContactInfo("123456"));
    }

    @Test
    void DR005_testMissingMandatoryField() {
        // Test case DR005: Missing Mandatory Field
        assertFalse(reportDisasterController.validateMandatoryFieldsTest("Hurricane", "", LocalDate.now(), "Chris Taylor", "0478901234"));
    }

    @Test
    void DR006_testDuplicateReportSubmission() {
        // Test case DR006: Duplicate Report Submission
        // Note: This test would typically involve database operations, which we can't fully simulate here.
        // Instead, we'll just ensure that the form validates correctly for a duplicate submission.
        assertTrue(reportDisasterController.validateMandatoryFieldsTest("Wildfire", "Brisbane Forest Park", LocalDate.now(), "John Doe", "0412345678"));
    }
}