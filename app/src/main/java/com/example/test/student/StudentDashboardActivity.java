package com.example.test.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import com.example.test.R;
import com.google.firebase.auth.FirebaseAuth;

public class StudentDashboardActivity extends AppCompatActivity{
    private Button btnBrowseSessions;
    private Button btnMyBookings;
    private Button btnPastSessions;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        btnBrowseSessions = findViewById(R.id.btnBrowseSessions);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        btnPastSessions = findViewById(R.id.btnPastSessions);
        btnLogout = findViewById(R.id.btnLogout);

        btnBrowseSessions.setOnClickListener(v ->
                startActivity(new Intent(this, BrowseSessionsActivity.class))
        );
        btnMyBookings.setOnClickListener(v ->
                startActivity(new Intent(this, BookingsActivity.class))
        );
        btnPastSessions.setOnClickListener(v ->
                startActivity(new Intent(this, PastSessionActivity.class))
        );
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
}

