package com.example.test.tutor;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageSlotsActivity extends AppCompatActivity implements TutorSlotAdapter.OnSlotDeleteListener {

    private TutorSlotAdapter adapter;
    private FirebaseRepository repository;
    private FirebaseAuth mAuth;
    private String currentTutorId;

    private RecyclerView rvSlots_REPLACE_WITH_XML_ID;

    private final List<Slot> mySlots = new ArrayList<>();
    private final Map<Slot, String> slotKeyMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_slots_REPLACE_ME);

        repository = new FirebaseRepository();
        mAuth = FirebaseAuth.getInstance();
        currentTutorId = (mAuth.getCurrentUser() != null) ? mAuth.getCurrentUser().getUid() : null;

        if (currentTutorId == null) {
            Toast.makeText(this, "Error: Not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvSlots_REPLACE_WITH_XML_ID = findViewById(R.id.rvSlots_REPLACE_WITH_XML_ID);
        rvSlots_REPLACE_WITH_XML_ID.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TutorSlotAdapter(this);
        rvSlots_REPLACE_WITH_XML_ID.setAdapter(adapter);

        loadTutorSlots();
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
        String key = slotKeyMap.get(slot);
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