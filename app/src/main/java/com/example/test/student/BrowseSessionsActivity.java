package com.example.test.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;

public class BrowseSessionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_browse_sessions);

        Button btnCreateSlot = findViewById(R.id.btnCreateSlot);

        // this will open BookSlotActivity
        btnCreateSlot.setOnClickListener(v ->
                startActivity(new Intent(BrowseSessionsActivity.this, CreateSlotRequestActivity.class))
        );
    }
}
