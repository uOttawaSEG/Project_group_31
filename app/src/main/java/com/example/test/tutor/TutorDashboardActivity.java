package com.example.test.tutor;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class TutorDashboardActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private String currentTutorId;
    private FirebaseRepository repository;


    private Button btnManageSlots;
    private Button btnPendingRequests;
    private Button btnViewSessions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_dashboard);

        repository = new FirebaseRepository();
        mAuth = FirebaseAuth.getInstance();
        currentTutorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentTutorId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnManageSlots = findViewById(R.id.btnManageSlots);
        btnPendingRequests = findViewById(R.id.btnPendingRequests);
        btnViewSessions = findViewById(R.id.btnViewSessions);


        btnManageSlots.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ManageSlotsActivity.class)));

        btnPendingRequests.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, PendingRequestsActivity.class)));

        btnViewSessions.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, SessionsActivity.class)));

    }
}