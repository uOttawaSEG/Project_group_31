package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    // --- Admin credentials ---
    private static final String ADMIN_EMAIL = "admin@uottawa.ca";
    private static final String ADMIN_PASSWORD = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

            // ✅ Check for admin login first
            if (email.equalsIgnoreCase(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
                Administrator admin = new Administrator(
                        "System", "Admin", ADMIN_EMAIL, ADMIN_PASSWORD, "000-000-0000"
                );

                Toast.makeText(this, "Welcome, Administrator!", Toast.LENGTH_SHORT).show();

                // ✅ Send both role and email to WelcomeActivity
                Intent adminIntent = new Intent(this, WelcomeActivity.class);
                adminIntent.putExtra("role", "Administrator");
                adminIntent.putExtra("email", ADMIN_EMAIL);
                startActivity(adminIntent);
                finish();
                return; // stop here — no need to check SharedPreferences
            }

            // 🔸 Regular user (Student/Tutor) login
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

            String storedPassword = prefs.getString(email + "_password", null);
            if (storedPassword == null || !storedPassword.equals(password)) {
                Toast.makeText(this, "Invalid password!", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = prefs.getString(email + "_role", null);

            if (role == null) {
                Toast.makeText(this, "User not found! Register first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the current logged-in email
            prefs.edit().putString("currentUserEmail", email).apply();

            // ✅ Redirect based on role (keeping your structure)
            Intent intent;
            {
                intent = new Intent(this, WelcomeActivity.class);
            }

            intent.putExtra("role", role);
            intent.putExtra("email", email); // ✅ Send email too
            startActivity(intent);
            finish();
        });
    }
}
