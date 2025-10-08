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

        // Get role from intent
        String role = getIntent().getStringExtra("role");

        // Get user email from SharedPreferences (we can store current user email there at login)
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String currentUserEmail = prefs.getString("currentUserEmail", null);

        tvWelcome.setText("Welcome! " + " You are logged in as " + role + ".");

        btnLogout.setOnClickListener(v -> {
            // Clear current user and return to login
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("currentUserEmail");
            editor.apply();

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }
}
