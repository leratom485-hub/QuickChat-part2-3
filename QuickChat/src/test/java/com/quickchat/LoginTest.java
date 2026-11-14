package com.quickchat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Part 1 unit tests. */
class LoginTest {

    private Login login;

    @BeforeEach
    void setUp() {
        login = new Login("Kyle", "Smith");
    }

    // ----- assertEquals -----

    @Test
    void usernameCorrectlyFormatted_message() {
        assertEquals("Username successfully captured.", login.usernameMessage("kyl_1"));
    }

    @Test
    void usernameCorrectlyFormatted_loginWelcome() {
        // The brief lists the welcome message for the "kyl_1" test data
        assertEquals("User has been registered successfully.",
                login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976").split("\n")[3]);
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(login.loginUser("kyl_1", "Ch&&sec@ke99!")));
    }

    @Test
    void usernameIncorrectlyFormatted_message() {
        assertEquals("Username is not correctly formatted; please ensure that your username contains an "
                + "underscore and is no more than five characters in length.",
                login.usernameMessage("kyle!!!!!!!"));
    }

    @Test
    void passwordMeetsComplexity_message() {
        assertEquals("Password successfully captured.", login.passwordMessage("Ch&&sec@ke99!"));
    }

    @Test
    void passwordDoesNotMeetComplexity_message() {
        assertEquals("Password is not correctly formatted; please ensure that the password contains at "
                + "least eight characters, a capital letter, a number, and a special character.",
                login.passwordMessage("password"));
    }

    @Test
    void cellPhoneCorrectlyFormatted_message() {
        assertEquals("Cell number successfully captured.", login.cellPhoneMessage("+27838968976"));
    }

    @Test
    void cellPhoneIncorrectlyFormatted_message() {
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; "
                + "please correct the number and try again.", login.cellPhoneMessage("08966553"));
    }

    @Test
    void registerUser_returnsFirstProblem() {
        assertEquals(Login.USERNAME_BAD, login.registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976"));
        assertEquals(Login.PASSWORD_BAD, login.registerUser("kyl_1", "password", "+27838968976"));
        assertEquals(Login.CELL_BAD, login.registerUser("kyl_1", "Ch&&sec@ke99!", "08966553"));
    }

    @Test
    void loginFailedMessage() {
        assertEquals("Username or password incorrect, please try again.", login.returnLoginStatus(false));
    }

    // ----- assertTrue / assertFalse -----

    @Test
    void loginSuccessful() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void loginFailed() {
        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.loginUser("kyl_1", "wrongPassword1!"));
        assertFalse(login.loginUser("other", "Ch&&sec@ke99!"));
    }

    @Test
    void loginFailsBeforeRegistration() {
        assertFalse(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    void usernameCorrectlyFormatted() {
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    void usernameIncorrectlyFormatted() {
        assertFalse(login.checkUserName("kyle!!!!!!!"));
        assertFalse(login.checkUserName("kyle1"));      // no underscore
        assertFalse(login.checkUserName("ky_le1"));     // too long
    }

    @Test
    void passwordMeetsComplexity() {
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    void passwordDoesNotMeetComplexity() {
        assertFalse(login.checkPasswordComplexity("password"));
        assertFalse(login.checkPasswordComplexity("Pass1!"));        // too short
        assertFalse(login.checkPasswordComplexity("password1!"));    // no capital
        assertFalse(login.checkPasswordComplexity("Password!!"));    // no number
        assertFalse(login.checkPasswordComplexity("Password11"));    // no special
    }

    @Test
    void cellPhoneCorrectlyFormatted() {
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    void cellPhoneIncorrectlyFormatted() {
        assertFalse(login.checkCellPhoneNumber("08966553"));
        assertFalse(login.checkCellPhoneNumber("+27abc"));
        assertFalse(login.checkCellPhoneNumber("+2783896897612345"));
    }
}
