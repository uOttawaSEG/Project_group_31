package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    private FirebaseAuth auth;

    private static final String ADMIN_EMAIL = "admin@uottawa.ca";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Admin -> AdminInbox
            if (email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                Toast.makeText(this, "Welcome, Administrator!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AdminInboxActivity.class));
                finish();
                return;
            }

            // Regular users: Firebase Auth, then approval gating
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            FirebaseDatabase.getInstance().getReference("tutors")
                                    .child(user.getUid())
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        String role = snapshot.exists() ? "Tutor" : "Student";

                                        String dotKey = email.replace(".", "_");
                                        FirebaseDatabase.getInstance().getReference("registrationRequests")
                                                .child(dotKey)
                                                .get()
                                                .addOnSuccessListener(reqSnap -> {
                                                    String status = reqSnap.child("status").getValue(String.class);
                                                    if (status == null || "PENDING".equals(status)) {
                                                        Toast.makeText(this, "Awaiting administrator approval.", Toast.LENGTH_LONG).show();
                                                        return;
                                                    }
                                                    if ("REJECTED".equals(status)) {
                                                        Toast.makeText(this, "Your registration was rejected. For assistance call 555-555-5555.", Toast.LENGTH_LONG).show();
                                                        return;
                                                    }
                                                    // APPROVED -> Welcome
                                                    Intent intent = new Intent(this, WelcomeActivity.class);
                                                    intent.putExtra("email", email);
                                                    intent.putExtra("role", role);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e2 ->
                                                        Toast.makeText(this, "Approval check failed: " + e2.getMessage(), Toast.LENGTH_SHORT).show()
                                                );
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to fetch role: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }
                    })
                    .addOnFailureListener(e ->
                            checkSharedPreferencesLogin(email, password, e.getMessage())
                    );
        });
    }

    private void checkSharedPreferencesLogin(String email, String password, String firebaseError) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String storedPassword = prefs.getString(email + "_password", null);

        if (storedPassword == null || !storedPassword.equals(password)) {
            if (firebaseError != null && firebaseError.contains("password is invalid")) {
                Toast.makeText(this, "Invalid password!", Toast.LENGTH_SHORT).show();
            } else if (firebaseError != null && firebaseError.contains("no user record")) {
                Toast.makeText(this, "User not found! Register first.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Login failed: " + firebaseError, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        String role = prefs.getString(email + "_role", null);
        if (role == null) {
            Toast.makeText(this, "User not found! Register first.", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit().putString("currentUserEmail", email).apply();

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.putExtra("role", role);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}
