package com.example.test.sharedfiles.model;

import java.util.ArrayList;
import java.util.List;

public class RegistrationRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private String status;
    private long submittedAt;

    private Long decidedAt;
    private String decidedByAdminId;
    private String rejectionReason;

    private String programOfStudy;

    private String highestDegree;
    private List<String> coursesOffered = new ArrayList<>();

    private String userId;
    private String password;

    public RegistrationRequest() {
        // Firebase needs this empty constructor
    }

    public RegistrationRequest(String firstName,
                               String lastName,
                               String email,
                               String phone,
                               String role,
                               String userId,
                               String password) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.userId = userId;
        this.password = password;

        this.status = "PENDING";
        this.submittedAt = System.currentTimeMillis();
        this.coursesOffered = new ArrayList<>();
    }

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

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
