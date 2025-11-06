package com.example.test;

import java.util.List;

/**
 * Represents a tutor user in the system.
 * Extends the User class with tutor-specific information.
 */
public class Tutor extends User {
    // Tutor's highest educational degree (e.g., "Bachelor's", "Master's", "PhD")
    private String highestDegree;

    // List of courses the tutor can teach (e.g., ["Math 101", "Physics 201"])
    private List<String> coursesOffered;

    /**
     * Creates a new Tutor with the given details.
     * Role is automatically set to "Tutor".
     */
    public Tutor(String firstName, String lastName, String email, String password,
                 String phoneNumber, String highestDegree, List<String> coursesOffered) {

        super(firstName, lastName, email, password, phoneNumber, "Tutor");
        this.highestDegree = highestDegree;
        this.coursesOffered = coursesOffered;
    }

    // Getters and setters for tutor-specific fields
    public String getHighestDegree() {
        return highestDegree;
    }
    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree;
    }

    public List<String> getCoursesOffered() {
        return coursesOffered;
    }
    public void setCoursesOffered(List<String> coursesOffered) {
        this.coursesOffered = coursesOffered;
    }
}