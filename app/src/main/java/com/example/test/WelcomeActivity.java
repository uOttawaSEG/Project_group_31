package com.example.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Welcome screen displayed after successful login.
 * Shows the user's role and email, and provides a logout button.
 */
public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeMessage;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Get references to UI elements
        welcomeMessage = findViewById(R.id.tvWelcome);
        logoutButton = findViewById(R.id.btnLogout);

        // Get user info passed from login screen
        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        String emailFromLogin = intent.getStringExtra("email");

        // Try to get email from SharedPreferences as fallback
        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedEmail = sp.getString("currentUserEmail", null);

        // Determine which email to display (prioritize login intent data)
        String emailToShow = "Unknown";
        if (emailFromLogin != null) {
            emailToShow = emailFromLogin;
        } else if (savedEmail != null) {
            emailToShow = savedEmail;
        }

        // Set default role if none provided
        if (role == null) {
            role = "Unknown";
        }

        // Display welcome message with role and email
        welcomeMessage.setText("Welcome! You are logged in as " + role + ".\nEmail: " + emailToShow);

        // Set up logout button to clear session and return to login
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear current user email from SharedPreferences
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("currentUserEmail");
                editor.apply();

                // Navigate back to login screen
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}