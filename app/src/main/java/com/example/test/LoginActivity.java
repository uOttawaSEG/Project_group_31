package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Handles user login with support for admin, Firebase authentication, and fallback to local storage.
 * Checks registration approval status before allowing access.
 */
public class LoginActivity extends AppCompatActivity {

    EditText emailField, passwordField;
    Button loginButton;

    private FirebaseAuth mAuth;

    // Hardcoded admin credentials
    private static final String ADMIN_EMAIL = "admin@uottawa.ca";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Get references to UI elements
        emailField = findViewById(R.id.etEmail);
        passwordField = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);

        // Set up login button click handler
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get entered credentials
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                // Validate input fields
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if logging in as admin
                if (email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                    Toast.makeText(LoginActivity.this, "Welcome, Administrator!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, AdminInboxActivity.class));
                    finish();
                    return;
                }

                // Attempt Firebase authentication for regular users
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Check if user is a tutor by looking in the tutors database
                                    FirebaseDatabase.getInstance().getReference("tutors")
                                            .child(user.getUid())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                @Override
                                                public void onSuccess(DataSnapshot snapshot) {
                                                    // Determine role based on presence in tutors database
                                                    String role = snapshot.exists() ? "Tutor" : "Student";

                                                    // Convert email to Firebase-safe key
                                                    String dotKey = email.replace(".", "_");

                                                    // Check registration approval status
                                                    FirebaseDatabase.getInstance().getReference("registrationRequests")
                                                            .child(dotKey)
                                                            .get()
                                                            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DataSnapshot reqSnap) {
                                                                    String status = reqSnap.child("status").getValue(String.class);

                                                                    // Block login if awaiting approval
                                                                    if (status == null || "PENDING".equals(status)) {
                                                                        Toast.makeText(LoginActivity.this, "Awaiting administrator approval.", Toast.LENGTH_LONG).show();
                                                                        return;
                                                                    }

                                                                    // Block login if rejected
                                                                    if ("REJECTED".equals(status)) {
                                                                        Toast.makeText(LoginActivity.this, "Your registration was rejected. For assistance call 555-555-5555.", Toast.LENGTH_LONG).show();
                                                                        return;
                                                                    }

                                                                    // If approved, proceed to welcome screen
                                                                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                                                                    intent.putExtra("email", email);
                                                                    intent.putExtra("role", role);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e2) {
                                                                    Toast.makeText(LoginActivity.this, "Approval check failed: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(LoginActivity.this, "Failed to fetch role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // If Firebase auth fails, try local SharedPreferences as fallback
                                checkSharedPreferencesLogin(email, password, e.getMessage());
                            }
                        });
            }
        });
    }

    /**
     * Fallback login method using SharedPreferences when Firebase authentication fails.
     * Provides user-friendly error messages based on the Firebase error.
     */
    private void checkSharedPreferencesLogin(String email, String password, String firebaseError) {
        // Access locally stored credentials
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String storedPassword = prefs.getString(email + "_password", null);

        // Validate stored credentials
        if (storedPassword == null || !storedPassword.equals(password)) {
            // Provide specific error messages based on Firebase error
            if (firebaseError != null && firebaseError.contains("password is invalid")) {
                Toast.makeText(this, "Invalid password!", Toast.LENGTH_SHORT).show();
            } else if (firebaseError != null && firebaseError.contains("no user record")) {
                Toast.makeText(this, "User not found! Register first.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login failed: " + firebaseError, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Retrieve stored role
        String role = prefs.getString(email + "_role", null);
        if (role == null) {
            Toast.makeText(this, "User not found! Register first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store current user email for session management
        prefs.edit().putString("currentUserEmail", email).apply();

        // Proceed to welcome screen
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("role", role);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}