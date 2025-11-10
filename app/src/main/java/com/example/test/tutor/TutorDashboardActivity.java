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


    private Button btnManageSlots_REPLACE_WITH_XML_ID;
    private Button btnPendingRequests_REPLACE_WITH_XML_ID;
    private Button btnSessions_REPLACE_WITH_XML_ID;
    private Switch switchAutoApproval_REPLACE_WITH_XML_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_dashboard_REPLACE_ME);

        repository = new FirebaseRepository();
        mAuth = FirebaseAuth.getInstance();
        currentTutorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentTutorId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnManageSlots_REPLACE_WITH_XML_ID = findViewById(R.id.btnManageSlots_REPLACE_WITH_XML_ID);
        btnPendingRequests_REPLACE_WITH_XML_ID = findViewById(R.id.btnPendingRequests_REPLACE_WITH_XML_ID);
        btnSessions_REPLACE_WITH_XML_ID = findViewById(R.id.btnSessions_REPLACE_WITH_XML_ID);
        switchAutoApproval_REPLACE_WITH_XML_ID = findViewById(R.id.switchAutoApproval_REPLACE_WITH_XML_ID);

        btnManageSlots_REPLACE_WITH_XML_ID.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ManageSlotsActivity.class)));

        btnPendingRequests_REPLACE_WITH_XML_ID.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, PendingRequestsActivity.class)));

        btnSessions_REPLACE_WITH_XML_ID.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, SessionsActivity.class)));


        repository.getDatabaseReference("tutors").child(currentTutorId)
                .child("autoApproved")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean value = snapshot.getValue(Boolean.class);
                        switchAutoApproval_REPLACE_WITH_XML_ID.setChecked(value != null && value);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(TutorDashboardActivity.this, "Failed to load setting", Toast.LENGTH_SHORT).show();
                    }
                }
                );

        switchAutoApproval_REPLACE_WITH_XML_ID.setOnCheckedChangeListener((buttonView, isChecked) -> {
            repository.getDatabaseReference("tutors")
                    .child(currentTutorId)
                    .child("autoApproved")
                    .setValue(isChecked)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TutorDashboardActivity.this, "Auto-approval updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TutorDashboardActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}