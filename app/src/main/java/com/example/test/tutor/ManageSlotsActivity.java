package com.example.test.tutor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Button;
import android.os.Bundle;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageSlotsActivity extends AppCompatActivity implements TutorSlotAdapter.OnSlotDeleteListener {

    private TutorSlotAdapter adapter;
    private FirebaseRepository repository;
    private FirebaseAuth mAuth;
    private String currentTutorId;

    private RecyclerView rvSlots;
    private Button btnAddSlot;
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
        btnAddSlot.setOnClickListener(v -> showAddSlotDialog());

        loadTutorSlots();
    }


    private void showAddSlotDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);

                    TimePickerDialog startPicker = new TimePickerDialog(
                            this,
                            (view1, hourOfDay, minute) -> {
                                String startTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);

                                TimePickerDialog endPicker = new TimePickerDialog(
                                        this,
                                        (view2, endHour, endMinute) -> {
                                            String endTime = String.format(Locale.getDefault(), "%02d:%02d", endHour, endMinute);
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
        Slot newSlot = new Slot();
        newSlot.setTutorId(currentTutorId);
        newSlot.setDate(date);
        newSlot.setStartTime(startTime);
        newSlot.setEndTime(endTime);
        newSlot.setRequiresApproval(false);
        newSlot.setIsAvailable(true);

        repository.addSlot(newSlot, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Slot added: " + date + " " + startTime + " - " + endTime, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add slot", Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(ManageSlotsActivity.this, "Failed to load slots", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public void onSlotDelete(Slot slot) {
        String key = slot.getSlotId();
        if (key == null) {
            key = slotKeyMap.get(slot);
        }

        if (key == null || key.trim().isEmpty()) {
            Toast.makeText(this, "Missing slot id", Toast.LENGTH_SHORT).show();
            return;
        }
        repository.deleteSlot(key, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ManageSlotsActivity.this, "Slot deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageSlotsActivity.this, "Failed to delete slot", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}