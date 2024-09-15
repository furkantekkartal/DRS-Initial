package Controller;

import ENUM.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class C_LoginTest {

    private C_Login loginController;

    @BeforeEach
    void setUp() {
        loginController = new C_Login();
    }

    @Test
    void UA001_testValidLogin() {
        // Test case: Valid login credentials should be accepted
        // Expected: true (login successful)
        assertTrue(loginController.validateLogin("john.smith", "pass", UserRole.Coordinator),
                "Valid login should return true");
    }

    @Test
    void UA002_testAdminLogin() {
        // Test case: Valid admin login credentials
        // Expected: true (admin login successful)
        assertTrue(loginController.validateLogin("admin", "admin", UserRole.Admin),
                "Valid admin login should return true");
    }

    @Test
    void UA003_testInvalidUsername() {
        // Test case: Login attempt with a non-existent username
        // Expected: false (login failed due to invalid username)
        assertFalse(loginController.validateLogin("nonexistent", "pass", UserRole.Coordinator),
                "Login with invalid username should return false");
    }

    @Test
    void UA004_testInvalidPassword() {
        // Test case: Login attempt with correct username but wrong password
        // Expected: false (login failed due to incorrect password)
        assertFalse(loginController.validateLogin("john.smith", "wrongpass", UserRole.Coordinator),
                "Login with invalid password should return false");
    }

    @Test
    void UA005_testEmptyFields() {
        // Test case: Login attempt with empty username and password
        // Expected: false (login failed due to empty credentials)
        assertFalse(loginController.validateLogin("", "", UserRole.Coordinator),
                "Login with empty fields should return false");
    }

    @Test
    void UA006_testUtilitySubDepartmentLogin() {
        // Test case: Valid login for a utility sub-department user
        // Expected: true (utility sub-department login successful)
        assertTrue(loginController.validateLogin("james.martin", "pass", UserRole.UtilityCompanies),
                "Valid utility sub-department login should return true");
    }

    @Test
    void UA007_testNullFields() {
        // Test case: Login attempt with null username, password, and role
        // Expected: false (login failed due to null inputs)
        assertFalse(loginController.validateLogin(null, null, null),
                "Login with null fields should return false");
    }

    @Test
    void UA008_testCaseSensitiveUsername() {
        // Test case: Login attempt with correct password but username in wrong case
        // Expected: false (login failed due to case-sensitive username check)
        assertFalse(loginController.validateLogin("John.Smith", "pass", UserRole.Coordinator),
                "Login should be case-sensitive for username");
    }

    @Test
    void UA009_testCaseSensitivePassword() {
        // Test case: Login attempt with correct username but password in wrong case
        // Expected: false (login failed due to case-sensitive password check)
        assertFalse(loginController.validateLogin("john.smith", "Pass", UserRole.Coordinator),
                "Login should be case-sensitive for password");
    }

    @Test
    void UA010_testWrongRoleLogin() {
        // Test case: Login attempt with correct credentials but incorrect role
        // Expected: false (login failed due to role mismatch)
        // assertTrue is used instead of assertFalse to show Failure tests example
        assertTrue(loginController.validateLogin("john.smith", "pass", UserRole.FireDepartment),
                "Login with correct credentials but wrong role should return false");
    }
}
