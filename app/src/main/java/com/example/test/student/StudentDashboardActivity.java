package com.example.test.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import com.example.test.R;
import com.google.firebase.auth.FirebaseAuth;

public class StudentDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        Button btnBrowseSessions = findViewById(R.id.btnBrowseSessions);
        Button btnMyBookings = findViewById(R.id.btnMyBookings);
        Button btnPastSessions = findViewById(R.id.btnPastSessions);
        Button btnLogout = findViewById(R.id.btnLogout);

        // this will open BookSlotActivity
        btnBrowseSessions.setOnClickListener(v ->
                startActivity(new Intent(this, BrowseSessionsActivity.class))
        );

        // Prepare for delivarable 4:  show student’s booked sessions ( this will be done next delivarable)
        btnMyBookings.setOnClickListener(v ->
                startActivity(new Intent(this, BookingsActivity.class))
        );

        btnPastSessions.setOnClickListener(v ->
                startActivity(new Intent(this, PastSessionActivity.class))
        );

        // Log out
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
}
