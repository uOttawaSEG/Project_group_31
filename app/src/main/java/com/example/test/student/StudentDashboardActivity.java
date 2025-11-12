package com.example.test.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.example.test.R;
import com.google.firebase.auth.FirebaseAuth;

public class StudentDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        Button btnBook = findViewById(R.id.btnBookSlot);
        Button btnView = findViewById(R.id.btnViewSessions);
        Button btnLogout = findViewById(R.id.btnLogout);

        // Open BookSlotActivity
        btnBook.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, StudentCreateSlotRequestActivity.class))
        );

        // (Optional) future feature: show student’s booked sessions
        btnView.setOnClickListener(v ->
                Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show()
        );

        // Log out
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
}
