package com.example.test.admin;

import com.example.test.sharedfiles.model.RegistrationRequest;
import com.example.test.sharedfiles.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Administrator extends User {

    private boolean superAdmin;
    private final DatabaseReference reqRef = FirebaseDatabase.getInstance().getReference("registrationRequests");

    public Administrator(String firstName, String lastName, String email,
                         String password, String phoneNumber) {
        super(
                email.replace(".", "_"),
                firstName,
                lastName,
                email,
                password,
                phoneNumber,
                "Administrator"
        );
        this.superAdmin = true;
    }

    public boolean validateCredentials(String enteredEmail, String enteredPassword) {
        return this.getEmail().equalsIgnoreCase(enteredEmail)
                && this.getPassword().equals(enteredPassword);
    }

    public void approveUser(RegistrationRequest r) {
        if (r == null) return;
        String emailKey = r.getEmail().replace(".","_");
        reqRef.child(emailKey).child("status").setValue("APPROVED");
        System.out.println("Administrator approved user: " + r.getEmail());
    }

    public void rejectUser(RegistrationRequest r) {
        if (r == null) return;
        String emailKey = r.getEmail().replace(".", "_");
        reqRef.child(emailKey).child("status").setValue("REJECTED");
        System.out.println("Administrator rejected user: " + r.getEmail());
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
