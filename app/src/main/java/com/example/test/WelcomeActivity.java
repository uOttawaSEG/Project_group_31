package com.example.test;

import android.os.Bundle;
import android.widget.*;
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

        tvWelcome.setText("Welcome! Navigation not implemented yet.");

        btnLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logout clicked, navigation not implemented yet.", Toast.LENGTH_SHORT).show();
        });
    }
}
