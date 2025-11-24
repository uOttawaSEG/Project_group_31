package com.example.test.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.example.test.sharedfiles.adapters.PastSessionAdapter;
import com.example.test.sharedfiles.model.StudentBooking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import androidx.appcompat.app.AppCompatActivity;

public class PastSessionActivity extends AppCompatActivity implements PastSessionAdapter.OnRateClickListener {
    private RecyclerView rvPastSessions;
    private Button btnReturnToDashboard;

    private FirebaseRepository repository;
    private String studentId;

    private final List<StudentBooking> pastSessions = new ArrayList<>();
    private PastSessionAdapter pastSessionAdapter;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private void fetchTutorInfo(String tutorId, TutorInfoCallback callback) {
        FirebaseDatabase.getInstance().getReference("tutors")
                .child(tutorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

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
        setContentView(R.layout.activity_past_sessions);

        repository = new FirebaseRepository();
        studentId = repository.getCurrentUserId();

        if (studentId == null) {
            Toast.makeText(this, "Student not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvPastSessions = findViewById(R.id.rvPastSessions);
        btnReturnToDashboard = findViewById(R.id.btnReturnToDashboard);

        rvPastSessions.setLayoutManager(new LinearLayoutManager(this));
        pastSessionAdapter = new PastSessionAdapter(pastSessions, this);
        rvPastSessions.setAdapter(pastSessionAdapter);

        btnReturnToDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, StudentDashboardActivity.class))
        );

        loadPastSessions();
    }

    private void loadPastSessions() {
        repository.getBookingsByStudent(studentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pastSessions.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {

                            StudentBooking booking = child.getValue(StudentBooking.class);
                            if (booking == null) continue;

                            if (!booking.isPast()) continue;

                            if (!"Approved".equalsIgnoreCase(booking.getStatus())) continue;

                            if (booking.getBookingId() == null)
                                booking.setBookingId(child.getKey());

                            String tutorId = booking.getTutorId();

                            fetchTutorInfo(tutorId, (name, rating) -> {
                                booking.setTutorName(name);
                                booking.setTutorRating(rating);

                                DatabaseReference ratingsRef = FirebaseDatabase.getInstance()
                                        .getReference("ratings")
                                        .child(tutorId);

                                ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        boolean alreadyRated = false;

                                        for (DataSnapshot rateSnap : snapshot.getChildren()) {
                                            String ratedBookingId = rateSnap.child("bookingId").getValue(String.class);
                                            String ratedStudentId = rateSnap.child("studentId").getValue(String.class);

                                            if (ratedBookingId != null && ratedStudentId != null &&
                                                    ratedBookingId.equals(booking.getBookingId()) &&
                                                    ratedStudentId.equals(booking.getStudentId())) {
                                                alreadyRated = true;
                                                break;
                                            }
                                        }

                                        booking.setAlreadyRated(alreadyRated);

                                        pastSessions.add(booking);

                                        sortByDateDescending(pastSessions);
                                        pastSessionAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
                                });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PastSessionActivity.this,
                                "Failed to load past sessions",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void sortByDateDescending(List<StudentBooking> list) {
        Collections.sort(list, new Comparator<StudentBooking>() {
            @Override
            public int compare(StudentBooking b1, StudentBooking b2) {
                try {
                    long t1 = dateFormat.parse(b1.getDate() + " " + b1.getStartTime()).getTime();
                    long t2 = dateFormat.parse(b2.getDate() + " " + b2.getStartTime()).getTime();
                    return Long.compare(t2, t1);
                } catch (Exception e) {
                    return 0;
                }
            }
        });
    }

    @Override
    public void onRateClick(StudentBooking booking) {
        if (booking == null) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_rate_tutor, null, false);

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText etComment = view.findViewById(R.id.etComment);
        Button btnCancelRate = view.findViewById(R.id.btnCancelRate);
        Button btnSubmitRate = view.findViewById(R.id.btnSubmitRate);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        btnCancelRate.setOnClickListener(v -> dialog.dismiss());

        btnSubmitRate.setOnClickListener(v -> {
            int stars = (int) ratingBar.getRating();
            String comment = etComment.getText().toString().trim();

            if (stars <= 0) {
                Toast.makeText(this, "Rating could not be submitted. No stars selected", Toast.LENGTH_SHORT).show();
            } else {
                saveRating(booking, stars, comment);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveRating(StudentBooking booking, int stars, String comment) {
        DatabaseReference ref = repository.getDatabaseReference("ratings")
                .child(booking.getTutorId());

        String ratingId = ref.push().getKey();
        if (ratingId == null) {
            Toast.makeText(this, "Could not save rating", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("ratingId", ratingId);
        data.put("tutorId", booking.getTutorId());
        data.put("studentId", booking.getStudentId());
        data.put("bookingId", booking.getBookingId());
        data.put("sessionId", booking.getSessionId());
        data.put("stars", stars);
        data.put("comment", comment);
        data.put("timestamp", System.currentTimeMillis());

        ref.child(ratingId).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Rating has been submitted successfully", Toast.LENGTH_SHORT).show();
                    updateTutorAverageRating(booking.getTutorId());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Could not submit rating. Please try again", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateTutorAverageRating(String tutorId) {

        DatabaseReference ratingsRef = FirebaseDatabase.getInstance()
                .getReference("ratings")
                .child(tutorId);

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                double total = 0;
                int count = 0;

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Integer stars = snap.child("stars").getValue(Integer.class);
                    if (stars != null) {
                        total += stars;
                        count++;
                    }
                }

                double avg = (count == 0) ? 0 : total / count;

                FirebaseDatabase.getInstance()
                        .getReference("tutors")
                        .child(tutorId)
                        .child("averageRating")
                        .setValue(avg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void checkIfAlreadyRated(StudentBooking booking, Runnable onNotRated, Runnable onRated) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("ratings")
                .child(booking.getTutorId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot child : snapshot.getChildren()) {
                    String bId = child.child("bookingId").getValue(String.class);
                    if (bId != null && bId.equals(booking.getBookingId())) {
                        onRated.run();
                        return;
                    }
                }
                onNotRated.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}

