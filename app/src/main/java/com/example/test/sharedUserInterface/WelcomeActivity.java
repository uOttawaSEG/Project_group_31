package com.example.test.sharedUserInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

public class WelcomeActivity extends AppCompatActivity {

    TextView welcomeMessage;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeMessage = findViewById(R.id.tvWelcome);
        logoutButton = findViewById(R.id.btnLogout);

        Intent intent = getIntent();
        String role = intent.getStringExtra("role");
        String emailFromLogin = intent.getStringExtra("email");

        SharedPreferences sp = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedEmail = sp.getString("currentUserEmail", null);

        String emailToShow = "Unknown";

        if (emailFromLogin != null) {
            emailToShow = emailFromLogin;
        } else if (savedEmail != null) {
            emailToShow = savedEmail;
        }


        if (role == null) {
            role = "Unknown";
        }

        welcomeMessage.setText("Welcome! You are logged in as " + role + ".\nEmail: " + emailToShow);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.remove("currentUserEmail");
                editor.apply();

                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
