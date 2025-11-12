package com.example.test.tutor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.sharedfiles.adapters.SessionAdapter;
import com.example.test.sharedfiles.model.Session;
import com.example.test.student.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SessionsActivity extends AppCompatActivity implements SessionAdapter.OnSessionCancelListener {

    private RecyclerView rvUpcomingSessions;
    private RecyclerView rvPastSessions;
    private Button btnReturnToDashboard;

    private SessionAdapter upcomingAdapter;
    private SessionAdapter pastAdapter;

    private FirebaseAuth mAuth;
    private String currentTutorId;

    private final Map<String, String> studentNameMap = new HashMap<>();
    private final SimpleDateFormat dateTimeFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);

        mAuth = FirebaseAuth.getInstance();
        currentTutorId = (mAuth.getCurrentUser() != null)
                ? mAuth.getCurrentUser().getUid()
                : null;

        if (currentTutorId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvUpcomingSessions = findViewById(R.id.rvUpcomingSessions);
        rvPastSessions = findViewById(R.id.rvPastSessions);
        btnReturnToDashboard = findViewById(R.id.btnReturnToDashboard);

        rvUpcomingSessions.setLayoutManager(new LinearLayoutManager(this));
        rvPastSessions.setLayoutManager(new LinearLayoutManager(this));

        upcomingAdapter = new SessionAdapter(this, true);
        pastAdapter = new SessionAdapter(this, false);

        rvUpcomingSessions.setAdapter(upcomingAdapter);
        rvPastSessions.setAdapter(pastAdapter);

        // Go back to dashboard when button clicked
        btnReturnToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(SessionsActivity.this, TutorDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        loadStudentNames();
    }

    private void loadStudentNames() {
        FirebaseDatabase.getInstance().getReference("students")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        studentNameMap.clear();
                        for (DataSnapshot s : snapshot.getChildren()) {
                            Student student = s.getValue(Student.class);
                            if (student != null) {
                                studentNameMap.put(s.getKey(),
                                        student.getFirstName() + " " + student.getLastName());
                            }
                        }
                        loadTutorSessions();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SessionsActivity.this,
                                "Failed to load students", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadTutorSessions() {
        FirebaseDatabase.getInstance().getReference("sessions")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Session> tutorSessions = new ArrayList<>();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            Session session = s.getValue(Session.class);
                            if (session != null
                                    && currentTutorId.equals(session.getTutorId())
                                    && "APPROVED".equalsIgnoreCase(session.getStatus())) {
                                session.setSessionId(s.getKey());
                                tutorSessions.add(session);
                            }
                        }
                        sortAndDisplaySessions(tutorSessions);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SessionsActivity.this,
                                "Failed to load sessions", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sortAndDisplaySessions(List<Session> sessions) {
        List<Session> upcoming = new ArrayList<>();
        List<Session> past = new ArrayList<>();
        Date now = new Date();

        for (Session s : sessions) {
            try {
                Date end = dateTimeFormat.parse(s.getDate() + " " + s.getEndTime());
                if (end != null && end.before(now)) {
                    past.add(s);
                } else {
                    upcoming.add(s);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                upcoming.add(s);
            }
        }

        upcomingAdapter.setSessions(upcoming, studentNameMap);
        pastAdapter.setSessions(past, studentNameMap);
    }

    @Override
    public void onSessionCancel(Session session) {
        FirebaseDatabase.getInstance().getReference("sessions")
                .child(session.getSessionId())
                .child("status")
                .setValue("CANCELED")
                .addOnSuccessListener(a ->
                        Toast.makeText(this, "Session canceled", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
