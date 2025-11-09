package com.example.test.tutor;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.example.test.sharedfiles.adapters.SessionAdapter;
import com.example.test.sharedfiles.model.Session;
import com.example.test.sharedfiles.model.Slot;
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

public class SessionsActivity extends AppCompatActivity implements SessionAdapter.OnSessionCancelListener {

    private RecyclerView rvUpcomingSessions_REPLACE_WITH_XML_ID;
    private RecyclerView rvPastSessions_REPLACE_WITH_XML_ID;

    private SessionAdapter upcomingAdapter;
    private SessionAdapter pastAdapter;

    private FirebaseRepository repository;
    private FirebaseAuth mAuth;
    private String currentTutorId;

    private Map<String, String> studentNameMap = new HashMap<>();
    private Map<String, String> slotTimeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sessions_REPLACE_ME);

        repository = new FirebaseRepository();
        mAuth = FirebaseAuth.getInstance();
        currentTutorId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (currentTutorId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvUpcomingSessions_REPLACE_WITH_XML_ID = findViewById(R.id.rvUpcomingSessions_REPLACE_WITH_XML_ID);
        rvPastSessions_REPLACE_WITH_XML_ID = findViewById(R.id.rvPastSessions_REPLACE_WITH_XML_ID);

        rvUpcomingSessions_REPLACE_WITH_XML_ID.setLayoutManager(new LinearLayoutManager(this));
        rvPastSessions_REPLACE_WITH_XML_ID.setLayoutManager(new LinearLayoutManager(this));

        upcomingAdapter = new SessionAdapter(this, true);
        pastAdapter = new SessionAdapter(this, false);

        rvUpcomingSessions_REPLACE_WITH_XML_ID.setAdapter(upcomingAdapter);
        rvPastSessions_REPLACE_WITH_XML_ID.setAdapter(pastAdapter);

        loadStudentNamesAndSlotTimes();
    }

    private void loadStudentNamesAndSlotTimes() {
        repository.getDatabaseReference("students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                studentNameMap.clear();
                for (DataSnapshot snapshot : studentSnapshot.getChildren()) {
                    Student student = snapshot.getValue(Student.class);
                    if (student != null) {
                        studentNameMap.put(snapshot.getKey(), student.getFirstName() + " " + student.getLastName());
                    }
                }

                repository.getDatabaseReference("slots").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot slotSnapshot) {
                        slotTimeMap.clear();
                        for (DataSnapshot snapshot : slotSnapshot.getChildren()) {
                            Slot slot = snapshot.getValue(Slot.class);
                            if (slot != null) {
                                String time = slot.getDate() + " " + slot.getStartTime();
                                slotTimeMap.put(snapshot.getKey(), time);
                            }
                        }

                        loadTutorSessions();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SessionsActivity.this, "Failed to load slots", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SessionsActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTutorSessions() {
        repository.getSessionsByTutor(currentTutorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Session> allSessions = new ArrayList<>();
                for (DataSnapshot sessionSnap : snapshot.getChildren()) {
                    Session session = sessionSnap.getValue(Session.class);
                    if (session != null) {
                        session.setSessionId(sessionSnap.getKey()); // Store the key
                        allSessions.add(session);
                    }
                }
                sortAndDisplaySessions(allSessions);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SessionsActivity.this, "Failed to load sessions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortAndDisplaySessions(List<Session> sessions) {
        List<Session> upcoming = new ArrayList<>();
        List<Session> past = new ArrayList<>();

        for (Session s : sessions) {
            String status = s.getStatus();

            if (status.equals("APPROVED")) {
                upcoming.add(s);
            } else if (status.equals("CANCELED") || status.equals("COMPLETED") || status.equals("REJECTED")) {
                past.add(s);
            }
        }

        upcomingAdapter.setSessions(upcoming, studentNameMap, slotTimeMap);
        pastAdapter.setSessions(past, studentNameMap, slotTimeMap);
    }

    @Override
    public void onSessionCancel(Session session) {
        repository.updateSessionStatus(session.getSessionId(), "CANCELED", new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SessionsActivity.this, "Session Canceled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SessionsActivity.this, "Failed to cancel session", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}