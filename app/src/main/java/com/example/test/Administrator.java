package com.example.test;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Administrator user who can approve or reject registration requests.
 * Extends the User class with admin-specific functionality.
 */
public class Administrator extends User {

    // Flag indicating if this admin has super admin privileges
    private boolean superAdmin;

    // Firebase reference to the registration requests database node
    private final DatabaseReference reqRef = FirebaseDatabase.getInstance().getReference("registrationRequests");

    /**
     * Creates a new Administrator with the given details.
     * All administrators are set as super admins by default.
     */
    public Administrator(String firstName, String lastName, String email,
                         String password, String phoneNumber) {
        super(firstName, lastName, email, password, phoneNumber, "Administrator");
        this.superAdmin = true;
    }

    /**
     * Validates login credentials by comparing email and password.
     * Email comparison is case-insensitive.
     */
    public boolean validateCredentials(String enteredEmail, String enteredPassword) {
        return this.getEmail().equalsIgnoreCase(enteredEmail)
                && this.getPassword().equals(enteredPassword);
    }

    /**
     * Approves a registration request by updating its status in Firebase.
     * Converts email to a valid Firebase key by replacing periods with underscores.
     */
    public void approveUser(RegistrationRequest r) {
        if (r == null) return;

        // Convert email to Firebase-safe key
        String emailKey = r.getEmail().replace(".","_");

        // Update status in database
        reqRef.child(emailKey).child("status").setValue("APPROVED");
        System.out.println("Administrator approved user: " + r.getEmail());
    }

    /**
     * Rejects a registration request by updating its status in Firebase.
     * Converts email to a valid Firebase key by replacing periods with underscores.
     */
    public void rejectUser(RegistrationRequest r) {
        if (r == null) return;

        // Convert email to Firebase-safe key
        String emailKey = r.getEmail().replace(".", "_");

        // Update status in database
        reqRef.child(emailKey).child("status").setValue("REJECTED");
        System.out.println("Administrator rejected user: " + r.getEmail());
    }

    // Getter and setter for super admin status
    public boolean isSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(boolean superAdmin) { this.superAdmin = superAdmin; }

    /**
     * Returns a string representation of the Administrator object.
     */
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