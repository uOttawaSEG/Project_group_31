package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    TextView tvWelcome;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);

        // Retrieve data passed from LoginActivity
        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        String emailFromIntent = intent.getStringExtra("email");

        // Retrieve SharedPreferences (for regular users)
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUserEmail = prefs.getString("currentUserEmail", null);

        // Determine which email to display
        String displayEmail;
        if (emailFromIntent != null && !emailFromIntent.isEmpty()) {
            displayEmail = emailFromIntent;  // For Administrator
        } else if (currentUserEmail != null) {
            displayEmail = currentUserEmail; // For Student/Tutor
        } else {
            displayEmail = "Unknown";
        }

        if (role == null) role = "Unknown";

        // Display combined message on one TextView
        tvWelcome.setText("Welcome! You are logged in as " + role + ".\nEmail: " + displayEmail);

        // Log Out button
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("currentUserEmail");
            editor.apply();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }
}
