package com.example.test;

/**
 * Base class representing a user in the system.
 * Contains common fields shared by all user types (Student, Tutor, Administrator).
 */
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;

    // Role is protected so subclasses can access it (e.g., "Student", "Tutor", "Administrator")
    protected String role;

    /**
     * Constructor to create a user with all required information.
     * @param role The user's role in the system
     */
    public User(String firstName, String lastName, String email, String password, String phoneNumber, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Getters and setters for all fields
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns a string representation of the user with all their information.
     */
    @Override
    public String toString(){
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}