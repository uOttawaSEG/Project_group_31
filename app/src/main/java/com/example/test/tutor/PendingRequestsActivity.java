package com.example.test.tutor;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.example.test.sharedfiles.adapters.SessionAdapter;
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
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvPendingSessions_REPLACE_WITH_XML_ID = findViewById(R.id.rvPendingSessions_REPLACE_WITH_XML_ID);
        rvPendingSessions_REPLACE_WITH_XML_ID.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SessionAdapter(/* OnSessionCancelListener */ null, /* showCancelButton */ false);
        rvPendingSessions_REPLACE_WITH_XML_ID.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(@NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder vh,
                                            @NonNull RecyclerView.ViewHolder tgt) {
                return false;
            }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getBindingAdapterPosition();
                if (pos < 0 || pos >= pending.size()) return;

                Session s = pending.get(pos);
                String newStatus = (dir == ItemTouchHelper.RIGHT) ? "APPROVED" : "REJECTED";

                repository.updateSessionStatus(s.getSessionId(), newStatus, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(PendingRequestsActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(pos);
                    } else {
                        Toast.makeText(PendingRequestsActivity.this,
                                (newStatus.equals("APPROVED") ? "Approved" : "Rejected"),
                                Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).attachToRecyclerView(rvPendingSessions_REPLACE_WITH_XML_ID);

        loadStudentNamesAndSlotTimes();
    }

    private void loadStudentNamesAndSlotTimes() {
        repository.getDatabaseReference("students").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                studentNameMap.clear();
                for (DataSnapshot snap : studentSnapshot.getChildren()) {
                    Student s = snap.getValue(Student.class);
                    if (s != null) {
                        studentNameMap.put(snap.getKey(), s.getFirstName() + " " + s.getLastName());
                    }
                }

                repository.getDatabaseReference("slots").addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot slotSnapshot) {
                        slotTimeMap.clear();
                        for (DataSnapshot snap : slotSnapshot.getChildren()) {
                            Slot sl = snap.getValue(Slot.class);
                            if (sl != null) {
                                String time = sl.getDate() + " " + sl.getStartTime();
                                slotTimeMap.put(snap.getKey(), time);
                            }
                        }
                        loadPendingSessions();
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PendingRequestsActivity.this, "Failed to load slots", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingRequestsActivity.this, "Failed to load students", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPendingSessions() {
        repository.getSessionsByTutor(currentTutorId).addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                pending.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    Session sess = s.getValue(Session.class);
                    if (sess != null) {
                        sess.setSessionId(s.getKey());
                        if ("PENDING".equalsIgnoreCase(sess.getStatus())) {
                            pending.add(sess);
                        }
                    }
                }
                adapter.setSessions(pending, studentNameMap, slotTimeMap);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PendingRequestsActivity.this, "Failed to load sessions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
