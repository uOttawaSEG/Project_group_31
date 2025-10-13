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

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    // --- Firebase ---
    private FirebaseAuth auth;

    // --- Admin credentials ---
    private static final String ADMIN_EMAIL = "admin@uottawa.ca";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
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

            // --- Admin login ---
            if (email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                Administrator admin = new Administrator(
                        "System", "Admin", ADMIN_EMAIL, ADMIN_PASSWORD, "000-000-0000"
                );

                Toast.makeText(this, "Welcome, Administrator!", Toast.LENGTH_SHORT).show();

                Intent adminIntent = new Intent(this, WelcomeActivity.class);
                adminIntent.putExtra("role", "Administrator");
                adminIntent.putExtra("email", ADMIN_EMAIL);
                startActivity(adminIntent);
                finish();
                return;
            }

            // --- Try Firebase login first ---
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(this, WelcomeActivity.class);
                            intent.putExtra("role", "Student"); // Default role for Firebase users
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // If Firebase login fails, check SharedPreferences fallback
                        checkSharedPreferencesLogin(email, password, e.getMessage());
                    });
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
