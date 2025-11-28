package com.example.test.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.data.FirebaseRepository;
import com.example.test.sharedfiles.adapters.BookingAdapter;
import com.example.test.sharedfiles.model.StudentBooking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.test.sharedfiles.model.Session;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class BookingsActivity extends AppCompatActivity implements BookingAdapter.OnCancelClickListener {

    private RecyclerView rvStudentBookings;
    private RecyclerView rvStudentRequestedSlot;
    private Button btnReturnToDashboard;
    private FirebaseRepository repository;
    private String studentId;

    private final List<StudentBooking> upcomingBookings = new ArrayList<>();
    private final List<StudentBooking> requestedBookings = new ArrayList<>();

    private BookingAdapter upcomingAdapter;
    private BookingAdapter requestedAdapter;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_bookings);

        repository = new FirebaseRepository();
        studentId = repository.getCurrentUserId();

        if (studentId == null) {
            Toast.makeText(this, "Student not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvStudentBookings = findViewById(R.id.rvStudentBookings);
        rvStudentRequestedSlot = findViewById(R.id.rvStudentRequestedSlot);
        btnReturnToDashboard = findViewById(R.id.btnReturnToDashboard);

        rvStudentBookings.setLayoutManager(new LinearLayoutManager(this));
        rvStudentRequestedSlot.setLayoutManager(new LinearLayoutManager(this));

        upcomingAdapter = new BookingAdapter(upcomingBookings, this);
        requestedAdapter = new BookingAdapter(requestedBookings, this);

        rvStudentBookings.setAdapter(upcomingAdapter);
        rvStudentRequestedSlot.setAdapter(requestedAdapter);

        btnReturnToDashboard.setOnClickListener(v ->
                startActivity(new Intent(BookingsActivity.this, StudentDashboardActivity.class))
        );

        loadBookings();
        loadRequestedSlotRequests();
    }
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
    private void loadBookings() {

        repository.getBookingsByStudent(studentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        upcomingBookings.clear();
                        requestedBookings.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {

                            StudentBooking booking = child.getValue(StudentBooking.class);
                            if (booking == null) continue;

                            if (booking.getBookingId() == null) {
                                booking.setBookingId(child.getKey());
                            }

                            if (booking.getDate() == null ||
                                    booking.getStartTime() == null ||
                                    booking.getEndTime() == null) {
                                continue;
                            }

                            if (booking.isPast()) continue;

                            String status = booking.getStatus();
                            if (status != null &&
                                    (status.equalsIgnoreCase("Cancelled")
                                            || status.equalsIgnoreCase("Canceled")
                                            || status.equalsIgnoreCase("CANCELED"))) {
                                continue;
                            }

                            fetchTutorInfo(booking.getTutorId(), (name, rating) -> {
                                booking.setTutorName(name);
                                booking.setTutorRating(rating);

                                if ("Pending".equalsIgnoreCase(booking.getStatus())) {
                                    requestedBookings.add(booking);
                                } else {
                                    upcomingBookings.add(booking);
                                }

                                sortByDateDescending(upcomingBookings);
                                sortByDateDescending(requestedBookings);

                                upcomingAdapter.notifyDataSetChanged();
                                requestedAdapter.notifyDataSetChanged();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingsActivity.this,
                                "Failed to load bookings",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }





    private void loadRequestedSlotRequests() {

        repository.getStudentSlotRequestsByStudent(studentId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        requestedBookings.clear();

                        for (DataSnapshot child : snapshot.getChildren()) {

                            Session session = child.getValue(Session.class);
                            if (session == null) continue;

                            StudentBooking booking = new StudentBooking(
                                    null,
                                    session.getSessionId(),
                                    session.getStudentId(),
                                    session.getTutorId(),
                                    "",
                                    session.getCourseCode(),
                                    session.getDate(),
                                    session.getStartTime(),
                                    session.getEndTime(),
                                    session.getStatus(),
                                    session.getSlotId()
                            );
                            booking.setBookingId(child.getKey());


                            String tutorId = booking.getTutorId();
                            fetchTutorInfo(tutorId, (name, rating) -> {
                                booking.setTutorName(name);
                                booking.setTutorRating(rating);

                                requestedBookings.add(booking);

                                sortByDateDescending(requestedBookings);
                                requestedAdapter.notifyDataSetChanged();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingsActivity.this,
                                "Failed to load requested sessions", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void sortByDateDescending(List<StudentBooking> list) {
        Collections.sort(list, new Comparator<StudentBooking>() {
            @Override
            public int compare(StudentBooking b1, StudentBooking b2) {
                try {
                    if (b1.getDate() == null || b1.getStartTime() == null) return 1;
                    if (b2.getDate() == null || b2.getStartTime() == null) return -1;

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
    public void onCancelClick(StudentBooking booking) {

        if (!booking.canCancel()) {
            Toast.makeText(this,
                    "Can't cancel with less than 24 hours before the session starts",
                    Toast.LENGTH_LONG).show();
            return;
        }

        repository.cancelBooking(booking.getBookingId(), booking.getSlotId());
        int index = upcomingBookings.indexOf(booking);
        if (index != -1) {
            upcomingBookings.remove(index);
            upcomingAdapter.notifyItemRemoved(index);
        }

        int reqIndex = requestedBookings.indexOf(booking);
        if (reqIndex != -1) {
            requestedBookings.remove(reqIndex);
            requestedAdapter.notifyItemRemoved(reqIndex);
        }


        Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();
    }

}