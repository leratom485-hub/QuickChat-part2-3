package com.quickchat;

import java.util.regex.Pattern;

/**
 * Handles user registration and login for QuickChat (Part 1).
 */
public class Login {

    /*
     * Cell phone regex: a "+" international code (1-3 digits) followed by a number of
     * no more than ten digits. Built using the java.util.regex.Pattern documentation:
     * Oracle (2024) Pattern (Java SE 17 API).
     * https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
     * (Replace/extend this reference with the exact source(s) you consulted.)
     */
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+\\d{1,3}\\d{1,10}$");

    // Messages required by the brief
    public static final String USERNAME_OK = "Username successfully captured.";
    public static final String USERNAME_BAD = "Username is not correctly formatted; please ensure that your username "
            + "contains an underscore and is no more than five characters in length.";
    public static final String PASSWORD_OK = "Password successfully captured.";
    public static final String PASSWORD_BAD = "Password is not correctly formatted; please ensure that the password "
            + "contains at least eight characters, a capital letter, a number, and a special character.";
    public static final String CELL_OK = "Cell number successfully captured.";
    public static final String CELL_BAD = "Cell number is incorrectly formatted or does not contain an international "
            + "code; please correct the number and try again.";
    public static final String REGISTER_OK = "User has been registered successfully.";
    public static final String LOGIN_FAIL = "Username or password incorrect, please try again.";

    private final String firstName;
    private final String lastName;
    private String username;
    private String password;
    private String cellPhone;

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /** True if the username contains an underscore and is no more than five characters long. */
    public boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    /** True if the password is 8+ characters with a capital letter, a number and a special character. */
    public boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetter(c)) {
                hasSpecial = true;
            }
        }
        return hasCapital && hasNumber && hasSpecial;
    }

    /** True if the number has an international code and is correctly sized. */
    public boolean checkCellPhoneNumber(String cell) {
        return isValidCellNumber(cell);
    }

    /** Shared by Login and Message so the cell rule lives in one place. */
    public static boolean isValidCellNumber(String cell) {
        return cell != null && CELL_PATTERN.matcher(cell).matches();
    }

    /** Message for the cell number check (wording used in the Part 1 unit tests). */
    public String cellPhoneMessage(String cell) {
        return checkCellPhoneNumber(cell) ? CELL_OK : CELL_BAD;
    }

    /** Message for the username check. */
    public String usernameMessage(String username) {
        return checkUserName(username) ? USERNAME_OK : USERNAME_BAD;
    }

    /** Message for the password check. */
    public String passwordMessage(String password) {
        return checkPasswordComplexity(password) ? PASSWORD_OK : PASSWORD_BAD;
    }

    /**
     * Validates the details and registers the user if everything is valid.
     * Returns the first problem found, or the success messages.
     */
    public String registerUser(String username, String password, String cell) {
        if (!checkUserName(username)) {
            return USERNAME_BAD;
        }
        if (!checkPasswordComplexity(password)) {
            return PASSWORD_BAD;
        }
        if (!checkCellPhoneNumber(cell)) {
            return CELL_BAD;
        }
        this.username = username;
        this.password = password;
        this.cellPhone = cell;
        return USERNAME_OK + "\n" + PASSWORD_OK + "\n" + CELL_OK + "\n" + REGISTER_OK;
    }

    /** True if the entered details match the registered details. */
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return username != null
                && username.equals(enteredUsername)
                && password.equals(enteredPassword);
    }

    /** Login status message. */
    public String returnLoginStatus(boolean loggedIn) {
        if (loggedIn) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return LOGIN_FAIL;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCellPhone() {
        return cellPhone;
    }
}
