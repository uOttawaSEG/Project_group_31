package com.example.test;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Administrator extends User {

    private boolean superAdmin;
    private final DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("registrationRequests");

    public Administrator(String firstName, String lastName, String email,
                         String password, String phoneNumber) {
        super(firstName, lastName, email, password, phoneNumber, "Administrator");
        this.superAdmin = true;
    }

    public boolean validateCredentials(String enteredEmail, String enteredPassword) {
        return this.getEmail().equalsIgnoreCase(enteredEmail)
                && this.getPassword().equals(enteredPassword);
    }

    public void approveUser(RegistrationRequest request) {
        if (request == null) return;
        String dotKey = request.getEmail().replace(".","_");
        requestsReference.child(dotKey).child("status").setValue("APPROVED");
        System.out.println("Administrator approved user: " + request.getEmail());
    }

    public void rejectUser(RegistrationRequest request) {
        if (request == null) return;
        String dotKey = request.getEmail().replace(".", "_");
        requestsReference.child(dotKey).child("status").setValue("REJECTED");
        System.out.println("Administrator rejected user: " + request.getEmail());
    }

    public boolean isSuperAdmin() { return superAdmin; }
    public void setSuperAdmin(boolean superAdmin) { this.superAdmin = superAdmin; }

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
