package com.example.test.sharedUserInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.admin.AdminInboxActivity;
import com.example.test.student.StudentDashboardActivity;
import com.example.test.tutor.TutorDashboardActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText emailField, passwordField;
    Button loginButton;

    private FirebaseAuth mAuth;

    private static final String ADMIN_EMAIL = "admin@uottawa.ca";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.etEmail);
        passwordField = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);

        loginButton.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            Toast.makeText(this, "Welcome, Administrator!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AdminInboxActivity.class));
            finish();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> checkUserStatus())
                .addOnFailureListener(e -> checkSharedPreferencesLogin(email, password, e.getMessage()));
    }

    private void checkUserStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        FirebaseDatabase.getInstance().getReference("tutors")
                .child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (snapshot.exists()) {
                        String status = snapshot.child("status").getValue(String.class);

                        if (status == null || status.equals("PENDING")) {
                            Toast.makeText(this, "Awaiting administrator approval.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (status.equals("REJECTED")) {
                            Toast.makeText(this, "Registration rejected.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        goToTutorDashboard(user.getEmail());
                        return;
                    }

                    FirebaseDatabase.getInstance().getReference("students")
                            .child(uid)
                            .get()
                            .addOnSuccessListener(studentSnap -> {

                                if (!studentSnap.exists()) {
                                    Toast.makeText(this, "Still waiting for admin approval", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String status = studentSnap.child("status").getValue(String.class);

                                if (status == null || status.equals("PENDING")) {
                                    Toast.makeText(this, "Awaiting administrator approval.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (status.equals("REJECTED")) {
                                    Toast.makeText(this, "Registration rejected.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                goToStudentDashboard(user.getEmail());

                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to fetch student record: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to fetch user role: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void goToTutorDashboard(String email) {
        Intent intent = new Intent(this, TutorDashboardActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("role", "Tutor");
        startActivity(intent);
        finish();
    }

    private void goToStudentDashboard(String email) {
        Intent intent = new Intent(this, StudentDashboardActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("role", "Student");
        startActivity(intent);
        finish();
    }

    private void checkSharedPreferencesLogin(String email, String password, String firebaseError) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String storedPassword = prefs.getString(email + "_password", null);

        if (storedPassword == null || !storedPassword.equals(password)) {
            Toast.makeText(this, "Login failed: " + firebaseError, Toast.LENGTH_SHORT).show();
            return;
        }

        String role = prefs.getString(email + "_role", "Student");

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("role", role);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}
