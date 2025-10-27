package com.example.test;

public class RegistrationRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    protected String role;

    private String status;

    public RegistrationRequest() {

    }

    public RegistrationRequest(String firstName, String lastName, String email, String phoneNumber, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = "PENDING";

    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    // Get the last name
    public String getLastName() {
        return lastName;
    }
    // Set or update the last name
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //methods to check status requests
   public boolean isPending() {
        return status != null && status.equals("PENDING");
   }

   public boolean isApproved() {
        return status != null && status.equals("APPROVED");
   }

   public boolean isRejected() {
        return status != null && status.equals("REJECTED");
   }































}
