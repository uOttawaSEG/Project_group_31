package com.example.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a user registration request.
 * Stores user information and tracks the approval status and admin decision.
 * Used for both student and tutor registrations.
 */
public class RegistrationRequest {
    // Common fields for all users
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role; // "Student" or "Tutor"
    private String status; // "PENDING", "APPROVED", or "REJECTED"
    private long submittedAt; // Timestamp when request was submitted
    private Long decidedAt; // Timestamp when admin made decision (null if pending)
    private String decidedByAdminId; // Email of admin who made decision
    private String rejectionReason; // Reason for rejection (if applicable)

    // Student-specific field
    private String programOfStudy;

    // Tutor-specific fields
    private String highestDegree;
    private List<String> coursesOffered;

    /**
     * Empty constructor required for Firebase data deserialization.
     */
    public RegistrationRequest() {
    }

    /**
     * Constructor to create a new registration request.
     * Status is automatically set to "PENDING" and submission time is recorded.
     */
    public RegistrationRequest(String firstName, String lastName, String email, String phone, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = "PENDING";
        this.submittedAt = System.currentTimeMillis();
        this.coursesOffered = new ArrayList<>();
    }

    // Getters and setters for all fields
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(long submittedAt) { this.submittedAt = submittedAt; }

    public Long getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Long decidedAt) { this.decidedAt = decidedAt; }

    public String getDecidedByAdminId() { return decidedByAdminId; }
    public void setDecidedByAdminId(String decidedByAdminId) { this.decidedByAdminId = decidedByAdminId; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getProgramOfStudy() { return programOfStudy; }
    public void setProgramOfStudy(String programOfStudy) { this.programOfStudy = programOfStudy; }

    public String getHighestDegree() { return highestDegree; }
    public void setHighestDegree(String highestDegree) { this.highestDegree = highestDegree; }

    public List<String> getCoursesOffered() { return coursesOffered; }
    public void setCoursesOffered(List<String> coursesOffered) { this.coursesOffered = coursesOffered; }
}