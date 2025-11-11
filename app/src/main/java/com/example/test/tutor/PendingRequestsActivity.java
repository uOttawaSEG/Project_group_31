package com.example.test.tutor;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.example.test.sharedfiles.adapters.PendingSessionRequestAdapter;
import com.example.test.sharedfiles.model.Slot;
import com.example.test.sharedfiles.model.Session;
import com.example.test.student.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingRequestsActivity extends AppCompatActivity {

    private RecyclerView rvPendingSessions_REPLACE_WITH_XML_ID;

    private SessionAdapter adapter;
    private FirebaseRepository repository;
    private FirebaseAuth mAuth;
    private String currentTutorId;

    private final List<Session> pending = new ArrayList<>();
    private final Map<String, String> studentNameMap = new HashMap<>();
    private final Map<String, String> slotTimeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests_REPLACE_ME);

        repository = new FirebaseRepository();
        mAuth = FirebaseAuth.getInstance();
        currentTutorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentTutorId == null) {
            Toast.makeText(this, "Error: Not logged on", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvPendingSessions_REPLACE_WITH_XML_ID = findViewById(R.id.rvPendingSessions_REPLACE_WITH_XML_ID);
        rvPendingSessions_REPLACE_WITH_XML_ID.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PendingSessionRequestAdapter(new PendingSessionRequestAdapter.Listener() {
            @Override
            public void onApprove(Session s) {
                repository.updateSessionStatus(s.getSessionId(), "Approved", task ->
                        Toast.makeText(PendingRequestsActivity.this,
                                task.isSuccessful() ? "Approved" : "Update failed",
                                Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onReject(Session s) {
                repository.updateSessionStatus(s.getSessionId(), "Rejected", task ->
                        Toast.makeText(PendingRequestsActivity.this,
                                task.isSuccessful() ? "Rejected" : "Update failed",
                                Toast.LENGTH_SHORT).show());
            }
        });
        rvPendingSessions_REPLACE_WITH_XML_ID.setAdapter(adapter);

        loadStudentNamesAndSlotTimes();
    }

    private void loadStudentNamesAndSlotTimes() {
        repository.getDatabaseReference("students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot studentSnap) {
                studentNameMap.clear();
                for (DataSnapshot s : studentSnap.getChildren()) {
                    Student st = s.getValue(Student.class);
                    if (st != null) {
                        studentNameMap.put(s.getKey(), st.getFirstName() + " " + st.getLastName());
                    }
                }

                repository.getDatabaseReference("slots").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot slotSnap) {
                        slotTimeMap.clear();
                        for (DataSnapshot s : slotSnap.getChildren()) {
                            Slot slot = s.getValue(Slot.class);
                            if (slot != null) {
                                String time = slot.getDate() + " " + slot.getStartTime();
                                slotTimeMap.put(s.getKey(), time);
                            }
                        }
                        loadPendingSessions();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PendingRequestsActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingRequestsActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPendingSessions() {
        repository.getSessionsByTutor(currentTutorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pending.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Session sess = s.getValue(Session.class);
                    if (sess != null) {
                        sess.setSessionId(s.getKey());
                        if ("Pending".equalsIgnoreCase(sess.getStatus())) {
                            pending.add(sess);
                        }
                    }
                }
                adapter.setSessions(pending, studentNameMap, slotTimeMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingRequestsActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
            }
        });
    }
}