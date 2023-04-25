package com.example.mainscreen;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedInUserView {
    private String displayName;
    private String email; // Add an email field

    public LoggedInUserView(String displayName) {
        this.displayName = displayName;
    }

    // Add a new constructor that takes both display name and email as parameters
    public LoggedInUserView(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Add a getter method for the email field
    public String getEmail() {
        return email;
    }
}

