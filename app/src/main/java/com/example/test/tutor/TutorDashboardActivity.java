package com.example.test.tutor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.google.firebase.auth.FirebaseAuth;

public class TutorDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentTutorId;
    private FirebaseRepository repository;

    private Button btnManageSlots;
    private Button btnPendingRequests;
    private Button btnViewSessions;
    private Button btnLogout;

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
        Button btnLogoutTutor = findViewById(R.id.btnLogoutTutor);

        btnManageSlots.setOnClickListener(v ->
                startActivity(new Intent(this, ManageSlotsActivity.class)));

        btnPendingRequests.setOnClickListener(v ->
                startActivity(new Intent(this, PendingRequestsActivity.class)));

        btnViewSessions.setOnClickListener(v ->
                startActivity(new Intent(this, SessionsActivity.class)));

        btnLogoutTutor.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            android.content.Intent intent = new android.content.Intent(this, com.example.test.sharedUserInterface.LoginActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
