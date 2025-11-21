package com.example.test.tutor;

import android.content.Intent;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.example.test.sharedfiles.adapters.TutorSlotAdapter;
import com.example.test.sharedfiles.model.Slot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManageSlotsActivity extends AppCompatActivity implements TutorSlotAdapter.OnSlotDeleteListener {

    private TutorSlotAdapter adapter;
    private FirebaseRepository repository;
    private FirebaseAuth mAuth;
    private String currentTutorId;
    private Button btnReturnToDashboard;
    private RecyclerView rvSlots;
    private Button btnAddSlot;
    private Switch switchAutoApproval;
    private final List<Slot> mySlots = new ArrayList<>();
    private final Map<Slot, String> slotKeyMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slots_manager);

        repository = new FirebaseRepository();
        mAuth = FirebaseAuth.getInstance();
        currentTutorId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;

        if (currentTutorId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvSlots = findViewById(R.id.rvViewSlotsList);
        rvSlots.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TutorSlotAdapter(this);
        rvSlots.setAdapter(adapter);

        btnAddSlot = findViewById(R.id.btnAddSlot);
        switchAutoApproval = findViewById(R.id.switchAutoApproval);
        btnReturnToDashboard = findViewById(R.id.btnReturnToDashboard);

        btnAddSlot.setOnClickListener(v -> showAddSlotDialog());

        setupAutoApprovalSwitch();
        loadTutorSlots();

        btnReturnToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(ManageSlotsActivity.this, TutorDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setupAutoApprovalSwitch() {
        DatabaseReference tutorRef = FirebaseDatabase.getInstance()
                .getReference("tutors").child(currentTutorId);
        tutorRef.child("autoApproval").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Boolean auto = snapshot.getValue(Boolean.class);
                if (auto != null) switchAutoApproval.setChecked(auto);
            }
        });

        switchAutoApproval.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tutorRef.child("autoApproval").setValue(isChecked)
                    .addOnSuccessListener(a ->
                            Toast.makeText(this, "Auto-approval " +
                                    (isChecked ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to update setting", Toast.LENGTH_SHORT).show());
        });
    }

    private void showAddSlotDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);

                    TimePickerDialog startPicker = new TimePickerDialog(
                            this,
                            (view1, hourOfDay, minute) -> {
                                String startTime = String.format(Locale.getDefault(),
                                        "%02d:%02d", hourOfDay, minute);

                                TimePickerDialog endPicker = new TimePickerDialog(
                                        this,
                                        (view2, endHour, endMinute) -> {
                                            String endTime = String.format(Locale.getDefault(),
                                                    "%02d:%02d", endHour, endMinute);
                                            addNewSlot(selectedDate, startTime, endTime);
                                        },
                                        (hourOfDay + 1) % 24, minute, true
                                );
                                endPicker.setTitle("Select End Time");
                                endPicker.show();
                            },
                            9, 0, true
                    );
                    startPicker.setTitle("Select Start Time");
                    startPicker.show();
                },
                2025, 0, 1
        );

        datePickerDialog.setTitle("Select Date");
        datePickerDialog.show();
    }

    private void addNewSlot(String date, String startTime, String endTime) {
        try {
            String startDateTimeStr = date + " " + startTime;
            String endDateTimeStr = date + " " + endTime;
            java.text.SimpleDateFormat sdf =
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());

            java.util.Date startDateTime = sdf.parse(startDateTimeStr);
            java.util.Date endDateTime = sdf.parse(endDateTimeStr);
            java.util.Date now = new java.util.Date();

            if (startDateTime == null || endDateTime == null) {
                Toast.makeText(this, "Invalid date or time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (startDateTime.before(now)) {
                Toast.makeText(this, "You cannot create a slot in the past", Toast.LENGTH_LONG).show();
                return;
            }

            if (!endDateTime.after(startDateTime)) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseReference slotsRef = FirebaseDatabase.getInstance().getReference("slots");
            slotsRef.orderByChild("tutorId").equalTo(currentTutorId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean overlap = false;
                            int newStart = toMinutes(startTime);
                            int newEnd = toMinutes(endTime);

                            for (DataSnapshot s : snapshot.getChildren()) {
                                String existingDate = s.child("date").getValue(String.class);
                                String existingStart = s.child("startTime").getValue(String.class);
                                String existingEnd = s.child("endTime").getValue(String.class);

                                if (existingDate == null || existingStart == null || existingEnd == null)
                                    continue;

                                if (existingDate.equals(date)) {
                                    int existStart = toMinutes(existingStart);
                                    int existEnd = toMinutes(existingEnd);

                                    if (newStart < existEnd && newEnd > existStart) {
                                        overlap = true;
                                        break;
                                    }
                                }
                            }

                            if (overlap) {
                                Toast.makeText(ManageSlotsActivity.this,
                                        "This slot overlaps with an existing one!",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Slot newSlot = new Slot();
                                newSlot.setTutorId(currentTutorId);
                                newSlot.setDate(date);
                                newSlot.setStartTime(startTime);
                                newSlot.setEndTime(endTime);
                                newSlot.setRequiresApproval(false);
                                newSlot.setIsAvailable(true);
                                newSlot.setIsBooked(false);

                                repository.addSlot(newSlot, task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ManageSlotsActivity.this,
                                                "Slot added: " + date + " " + startTime +
                                                        " - " + endTime, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ManageSlotsActivity.this,
                                                "Failed to add slot", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ManageSlotsActivity.this,
                                    "Error checking existing slots", Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Error validating slot: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTutorSlots() {
        repository.getSlotsByTutor(currentTutorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mySlots.clear();
                slotKeyMap.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    Slot slot = s.getValue(Slot.class);
                    if (slot != null) {
                        slot.setSlotId(s.getKey());
                        mySlots.add(slot);
                        slotKeyMap.put(slot, s.getKey());
                    }
                }
                adapter.setSlots(mySlots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageSlotsActivity.this,
                        "Failed to load slots", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSlotDelete(Slot slot) {
        if (slot.getIsBooked()) {
            Toast.makeText(this, "Cannot delete a slot that has a booked session.", Toast.LENGTH_LONG).show();
            return;
        }

        String key = slot.getSlotId();
        if (key == null) key = slotKeyMap.get(slot);

        if (key == null || key.trim().isEmpty()) {
            Toast.makeText(this, "Missing slot id", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.deleteSlot(key, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ManageSlotsActivity.this,
                            "Slot deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageSlotsActivity.this,
                            "Failed to delete slot", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int toMinutes(String time) {
        try {
            String[] parts = time.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return h * 60 + m;
        } catch (Exception e) {
            return 0;
        }
    }
}