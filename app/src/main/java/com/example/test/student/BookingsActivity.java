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
import com.google.firebase.database.ValueEventListener;

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
                            if (booking == null) {
                                continue;
                            }

                            if (booking.getBookingId() == null) {
                                booking.setBookingId(child.getKey());
                            }

                            if (booking.isPast()) {
                                continue;
                            }

                            if ("Pending".equalsIgnoreCase(booking.getStatus())) {
                                requestedBookings.add(booking);
                            } else {
                                upcomingBookings.add(booking);
                            }
                        }

                        sortByDateDescending(upcomingBookings);
                        sortByDateDescending(requestedBookings);

                        upcomingAdapter.notifyDataSetChanged();
                        requestedAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingsActivity.this,
                                "Failed to load bookings", Toast.LENGTH_SHORT).show();
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
    public void onCancelClick(StudentBooking booking) {
        if (!booking.canCancel()) {
            Toast.makeText(this,
                    "Can't cancel with less than 24 hours before the session starts",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (booking.getBookingId() == null || booking.getSlotId() == null) {
            Toast.makeText(this,
                    "Missing booking id", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.cancelBooking(booking.getBookingId(), booking.getSlotId());
        Toast.makeText(this, "Booking is cancelled", Toast.LENGTH_SHORT).show();
    }
}