package com.example.test.student;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.sharedfiles.model.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CreateSlotRequestActivity extends AppCompatActivity {

    private Spinner spTutor;
    private EditText etDate, etStart, etEnd;
    private Button btnSend, btnCancel;

    private DatabaseReference tutorsRef, requestsRef;
    private List<String> tutorNames = new ArrayList<>();
    private List<String> tutorIds = new ArrayList<>();
    private Map<String, Boolean> autoApprovalMap = new HashMap<>();

    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_create_slot_request);

        etDate = findViewById(R.id.etDate);
        etStart = findViewById(R.id.etStartTime);
        etEnd = findViewById(R.id.etEndTime);
        btnSend = findViewById(R.id.btnSendRequest);
        btnCancel = findViewById(R.id.btnCancel);
        spTutor = findViewById(R.id.spTutor);

        tutorsRef = FirebaseDatabase.getInstance().getReference("tutors");
        requestsRef = FirebaseDatabase.getInstance().getReference("StudentSlotRequests");

        loadTutorsIntoSpinner();

        // help to open pickers on tap
        etDate.setOnClickListener(v -> showDatePicker());
        etStart.setOnClickListener(v -> showTimePicker(etStart, "Select Start Time"));
        etEnd.setOnClickListener(v -> showTimePicker(etEnd, "Select End Time"));

        btnSend.setOnClickListener(v -> sendRequest());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    String formatted = String.format(Locale.getDefault(),
                            "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDate.setText(formatted);
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        // prevent the tutor from  choosing past dates
        picker.getDatePicker().setMinDate(System.currentTimeMillis());
        picker.setTitle("Select Date");
        picker.show();
    }

    private void showTimePicker(EditText target, String title) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog picker = new TimePickerDialog(
                this,
                (view, hour, minute) -> {
                    String formatted = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                    target.setText(formatted);
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        picker.setTitle(title);
        picker.show();
    }

    private void loadTutorsIntoSpinner() {
        tutorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tutorNames.clear();
                tutorIds.clear();
                autoApprovalMap.clear();

                for (DataSnapshot tSnap : snapshot.getChildren()) {
                    String tutorId = tSnap.getKey();
                    String firstName = tSnap.child("firstName").getValue(String.class);
                    String lastName = tSnap.child("lastName").getValue(String.class);
                    Boolean autoApproval = tSnap.child("autoApproval").getValue(Boolean.class);

                    if (tutorId != null && firstName != null && lastName != null) {
                        tutorNames.add(firstName + " " + lastName);
                        tutorIds.add(tutorId);
                        autoApprovalMap.put(tutorId, autoApproval != null && autoApproval);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        CreateSlotRequestActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        tutorNames
                );
                spTutor.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateSlotRequestActivity.this,
                        "Failed to load tutors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequest() {
        String date = etDate.getText().toString().trim();
        String start = etStart.getText().toString().trim();
        String end = etEnd.getText().toString().trim();

        if (date.isEmpty() || start.isEmpty() || end.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spTutor.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select a tutor", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date startDateTime = sdf.parse(date + " " + start);
            Date endDateTime = sdf.parse(date + " " + end);
            Date now = new Date();

            if (startDateTime == null || endDateTime == null) {
                Toast.makeText(this, "Invalid date or time", Toast.LENGTH_SHORT).show();
                return;
            }

            if (startDateTime.before(now)) {
                Toast.makeText(this, "You cannot book a session in the past", Toast.LENGTH_LONG).show();
                return;
            }

            if (!endDateTime.after(startDateTime)) {
                Toast.makeText(this, "End time must be after start time", Toast.LENGTH_LONG).show();
                return;
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error validating time: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        String tutorId = tutorIds.get(spTutor.getSelectedItemPosition());
        boolean autoApproval = autoApprovalMap.getOrDefault(tutorId, false);
        String status = autoApproval ? "APPROVED" : "PENDING";

        String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String requestId = requestsRef.push().getKey();

        if (requestId == null) {
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            return;
        }

        Session session = new Session(
                requestId,
                tutorId,
                studentId,
                requestId,
                date,
                start,
                end,
                "",
                status
        );
        session.setDate(date);
        session.setStartTime(start);
        session.setEndTime(end);

        requestsRef.child(requestId).setValue(session)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this,
                            autoApproval ? "Request auto-approved!" : "Request sent!",
                            Toast.LENGTH_SHORT).show();

                    if (autoApproval) {
                        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
                        String sessionId = sessionsRef.push().getKey();
                        if (sessionId != null) {
                            Map<String, Object> sessionData = new HashMap<>();
                            sessionData.put("sessionId", sessionId);
                            sessionData.put("tutorId", tutorId);
                            sessionData.put("studentId", studentId);
                            sessionData.put("slotId", requestId); // optional link back
                            sessionData.put("date", date);
                            sessionData.put("startTime", start);
                            sessionData.put("endTime", end);
                            sessionData.put("status", "APPROVED");

                            sessionsRef.child(sessionId).setValue(sessionData);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }}