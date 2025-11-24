package com.example.test.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.example.test.sharedfiles.adapters.AvailableSlotAdapter;
import com.example.test.sharedfiles.model.Slot;
import com.example.test.sharedfiles.model.StudentBooking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class BrowseSessionsActivity extends AppCompatActivity {

    private EditText etCourseSearch;
    private Button btnSearch;
    private Button btnCreateSlot;
    private RecyclerView rvAvailableSlots;

    private FirebaseRepository repository;
    private String studentId;

    private final List<Slot> slots = new ArrayList<>();
    private AvailableSlotAdapter slotAdapter;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());


    private void fetchTutorInfo(String tutorId, TutorInfoCallback callback) {
        FirebaseDatabase.getInstance().getReference("tutors")
                .child(tutorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            callback.onResult("Unknown Tutor", 0.0);
                            return;
                        }

                        String first = snapshot.child("firstName").getValue(String.class);
                        String last = snapshot.child("lastName").getValue(String.class);
                        Double rating = snapshot.child("averageRating").getValue(Double.class);

                        if (first == null) first = "";
                        if (last == null) last = "";
                        if (rating == null) rating = 0.0;

                        callback.onResult(first + " " + last, rating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult("Unknown Tutor", 0.0);
                    }
                });
    }

    private interface TutorInfoCallback {
        void onResult(String tutorName, double rating);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_browse_sessions);

        repository = new FirebaseRepository();
        studentId = repository.getCurrentUserId();

        if (studentId == null) {
            Toast.makeText(this, "Student not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etCourseSearch = findViewById(R.id.etCourseSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnCreateSlot = findViewById(R.id.btnCreateSlot);
        rvAvailableSlots = findViewById(R.id.rvAvailableSlots);

        rvAvailableSlots.setLayoutManager(new LinearLayoutManager(this));
        slotAdapter = new AvailableSlotAdapter(slots, this::onBookClicked);
        rvAvailableSlots.setAdapter(slotAdapter);

        btnSearch.setOnClickListener(v -> searchSlots());

        btnCreateSlot.setOnClickListener(v ->
                startActivity(new Intent(BrowseSessionsActivity.this,
                        CreateSlotRequestActivity.class))
        );
    }

    private void searchSlots() {
        String code = etCourseSearch.getText().toString().trim().toUpperCase(Locale.getDefault());

        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Enter course code", Toast.LENGTH_SHORT).show();
            return;
        }

        Query q = repository.getDatabaseReference("slots")
                .orderByChild("courseCode")
                .equalTo(code);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slots.clear();

                if (!snapshot.hasChildren()) {
                    Toast.makeText(BrowseSessionsActivity.this,
                            "No available slots found",
                            Toast.LENGTH_SHORT).show();
                    slotAdapter.notifyDataSetChanged();
                    return;
                }

                final int total = (int) snapshot.getChildrenCount();
                final int[] processed = {0};

                for (DataSnapshot s : snapshot.getChildren()) {
                    Slot slot = s.getValue(Slot.class);
                    if (slot == null) {
                        processed[0]++;
                        continue;
                    }

                    slot.setSlotId(s.getKey());

                    if (slot.isPast() || !slot.isAvailable() || slot.getIsBooked()) {
                        processed[0]++;
                        continue;
                    }

                    fetchTutorInfo(slot.getTutorId(), (name, rating) -> {
                        slot.setTutorName(name);
                        slot.setTutorRating(rating);

                        slots.add(slot);

                        processed[0]++;

                        if (processed[0] == total) {
                            if (slots.isEmpty()) {
                                Toast.makeText(BrowseSessionsActivity.this,
                                        "No available slots found",
                                        Toast.LENGTH_SHORT).show();
                            }
                            slotAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BrowseSessionsActivity.this,
                        "Failed to load slots", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onBookClicked(Slot slot) {
        if (slot == null || slot.getSlotId() == null) {
            Toast.makeText(this, "Invalid slot", Toast.LENGTH_SHORT).show();
            return;
        }

        checkConflicts(slot, hasConflict -> {
            if (hasConflict) {
                Toast.makeText(this,
                        "There is already a booking scheduled during this time",
                        Toast.LENGTH_LONG).show();
            } else {
                createBooking(slot);
            }
        });
    }

    private void checkConflicts(Slot newSlot, ConflictCallback cb) {
        repository.getBookingsByStudent(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            long newStart = dateFormat
                                    .parse(newSlot.getDate() + " " + newSlot.getStartTime())
                                    .getTime();
                            long newEnd = dateFormat
                                    .parse(newSlot.getDate() + " " + newSlot.getEndTime())
                                    .getTime();

                            for (DataSnapshot s : snapshot.getChildren()) {
                                StudentBooking b = s.getValue(StudentBooking.class);
                                if (b == null) continue;

                                if ("Cancelled".equalsIgnoreCase(b.getStatus())) continue;
                                if ("Rejected".equalsIgnoreCase(b.getStatus())) continue;
                                if (b.isPast()) continue;

                                long bStart = dateFormat
                                        .parse(b.getDate() + " " + b.getStartTime())
                                        .getTime();
                                long bEnd = dateFormat
                                        .parse(b.getDate() + " " + b.getEndTime())
                                        .getTime();

                                boolean overlap = newStart < bEnd && newEnd > bStart;
                                if (overlap) {
                                    cb.onResult(true);
                                    return;
                                }
                            }

                            cb.onResult(false);
                        } catch (Exception e) {
                            cb.onResult(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cb.onResult(false);
                    }
                });
    }

    private void createBooking(Slot slot) {

        String code = slot.getCourseCode();
        if (code == null || code.trim().isEmpty()) {
            code = etCourseSearch.getText().toString().trim().toUpperCase(Locale.getDefault());
        }

        String status = slot.isRequiresApproval() ? "Pending" : "Approved";

        StudentBooking booking = new StudentBooking(
                null,
                null,
                studentId,
                slot.getTutorId(),
                "",                     // tutor name will be fetched later
                code,
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                status,
                slot.getSlotId()
        );

        repository.addStudentBooking(booking);

        repository.updateSlotBooking(slot.getSlotId(), true, booking.getBookingId());

        DatabaseReference sessionsRef =
                FirebaseDatabase.getInstance().getReference("sessions");

        String newSessionId = sessionsRef.push().getKey();

        if (newSessionId != null) {

            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("sessionId", newSessionId);
            sessionData.put("slotId", slot.getSlotId());
            sessionData.put("studentId", studentId);
            sessionData.put("tutorId", slot.getTutorId());
            sessionData.put("courseCode", code);
            sessionData.put("date", slot.getDate());
            sessionData.put("startTime", slot.getStartTime());
            sessionData.put("endTime", slot.getEndTime());
            sessionData.put("status", status);

            sessionsRef.child(newSessionId).setValue(sessionData);
        }

        Toast.makeText(this,
                "Booking has been created (" + status + ").",
                Toast.LENGTH_SHORT).show();

        slots.remove(slot);
        slotAdapter.notifyDataSetChanged();
    }

    private interface ConflictCallback {
        void onResult(boolean hasConflict);
    }
}

