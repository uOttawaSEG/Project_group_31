package com.example.test.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

public class BookingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_bookings);

        Button btnReturnToDashboard = findViewById(R.id.btnReturnToDashboard);

        btnReturnToDashboard.setOnClickListener(v ->
                startActivity(new Intent(BookingsActivity.this, StudentDashboardActivity.class))
        );
    }
}
