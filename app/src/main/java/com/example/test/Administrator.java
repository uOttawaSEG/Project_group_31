package com.example.test;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Administrator extends User {

    // Optional: indicates whether this admin is the main system admin
    private boolean superAdmin;

    //Creates a Firebase reference to registration requests
    private final DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("registrationRequests");

    // Constructor — uses the parent User constructor
    public Administrator(String firstName, String lastName, String email,
                         String password, String phoneNumber) {
        // Pass "Administrator" as the role to the User constructor
        super(firstName, lastName, email, password, phoneNumber, "Administrator");
        this.superAdmin = true;
    }

    // --- Admin-specific methods ---

    // Check if the entered credentials match the admin account
    public boolean validateCredentials(String enteredEmail, String enteredPassword) {
        return this.getEmail().equalsIgnoreCase(enteredEmail)
                && this.getPassword().equals(enteredPassword);
    }

    // Approve a user registration (placeholder for database logic)
    public void approveUser(RegistrationRequest request) {
        if (request == null) {
            return;
        }

        String dotKey = request.getEmail().replace(".","_");
        requestsReference.child(dotKey).child("status").setValue("Approved");
        System.out.println("Administrator approved user: " + request.getEmail());

    }

    // Reject a user registration (placeholder for database logic)
    public void rejectUser(User user) {
        System.out.println("Administrator rejected user: " + user.getEmail());
    }

    // --- Getters and setters ---
    public boolean isSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        this.superAdmin = superAdmin;
    }

    // --- Override toString() for better display ---
    @Override
    public String toString() {
        return "Administrator{" +
                "firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
