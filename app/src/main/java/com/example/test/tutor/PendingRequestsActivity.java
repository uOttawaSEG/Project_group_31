package com.example.test.tutor;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingRequestsActivity extends AppCompatActivity {

    private RecyclerView rvPendingSessions;
    private PendingSessionRequestAdapter adapter;
    private FirebaseRepository repository;
    private FirebaseAuth mAuth;
    private String currentTutorId;

    private final List<Session> pending = new ArrayList<>();
    private final Map<String, Student> studentInfoMap = new HashMap<>();
    private final Map<String, String> slotTimeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        repository = new FirebaseRepository();
        mAuth = FirebaseAuth.getInstance();
        currentTutorId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;

        if (currentTutorId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvPendingSessions = findViewById(R.id.rvPendingRequests);
        rvPendingSessions.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PendingSessionRequestAdapter(new PendingSessionRequestAdapter.OnRequestDecisionListener() {
            @Override
            public void onApprove(Session s) {
                repository.getDatabaseReference("StudentSlotRequests")
                        .child(s.getSessionId())
                        .child("tutorId")
                        .setValue(currentTutorId);

                repository.updateStudentSlotRequestStatus(s.getSessionId(), "APPROVED", task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> sessionData = new HashMap<>();
                        sessionData.put("tutorId", currentTutorId);
                        sessionData.put("studentId", s.getStudentId());
                        sessionData.put("slotId", s.getSlotId());
                        sessionData.put("date", s.getDate());
                        sessionData.put("startTime", s.getStartTime());
                        sessionData.put("endTime", s.getEndTime());
                        sessionData.put("status", "APPROVED");

                        Student st = studentInfoMap.get(s.getStudentId());
                        if (st != null) {
                            sessionData.put("studentEmail", st.getEmail());
                            sessionData.put("courseName", st.getProgramOfStudy());
                        }

                        repository.createSessionFromRequest(s.getSessionId(), sessionData);
                        Toast.makeText(PendingRequestsActivity.this, "Approved and moved to sessions", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PendingRequestsActivity.this, "Failed to approve request", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onReject(Session s) {
                repository.updateStudentSlotRequestStatus(s.getSessionId(), "REJECTED", new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(PendingRequestsActivity.this,
                                task.isSuccessful() ? "Rejected" : "Update failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        rvPendingSessions.setAdapter(adapter);

        Button btnReturn = findViewById(R.id.btnReturnToDashboard);
        btnReturn.setOnClickListener(v -> {
            Intent i = new Intent(PendingRequestsActivity.this, TutorDashboardActivity.class);
            startActivity(i);
            finish();
        });

        loadStudentInfoAndSlots();
    }

    private void loadStudentInfoAndSlots() {
        repository.getDatabaseReference("students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot studentSnap) {
                studentInfoMap.clear();
                for (DataSnapshot s : studentSnap.getChildren()) {
                    Student st = s.getValue(Student.class);
                    if (st != null) studentInfoMap.put(s.getKey(), st);
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
                        loadPendingRequests();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PendingRequestsActivity.this, "Failed to load slots", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingRequestsActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPendingRequests() {
        repository.getDatabaseReference("StudentSlotRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pending.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Session sess = s.getValue(Session.class);
                    if (sess != null) {
                        sess.setSessionId(s.getKey());
                        if ("PENDING".equalsIgnoreCase(sess.getStatus()) &&
                                ("UNASSIGNED".equals(sess.getTutorId()) || currentTutorId.equals(sess.getTutorId()))) {

                                Student st = studentInfoMap.get(sess.getStudentId());
                            if (st != null) {
                                sess.setStudentEmail(st.getEmail());
                                sess.setCourseName(st.getCourse());
                            }

                            pending.add(sess);
                        }
                    }
                }

                Map<String, String> studentNames = new HashMap<>();
                for (Map.Entry<String, Student> e : studentInfoMap.entrySet()) {
                    studentNames.put(e.getKey(), e.getValue().getFirstName() + " " + e.getValue().getLastName());
                }

                adapter.setSessions(pending, studentNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingRequestsActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
