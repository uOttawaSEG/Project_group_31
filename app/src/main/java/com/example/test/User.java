package com.example.test;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;

    protected String role;


    //Constructor to create student object with first name and last name
    public User(String firstName, String lastName, String email, String password, String phoneNumber, String role) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
    //Get the first name
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

