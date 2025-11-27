package com.example.test.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test.sharedfiles.model.RegistrationRequest;
import com.example.test.sharedfiles.model.Session;
import com.example.test.sharedfiles.model.Slot;
import com.example.test.sharedfiles.model.StudentBooking;
import com.example.test.student.Student;
import com.example.test.tutor.Tutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class FirebaseRepository {

    private static final String PATH_REG_REQUESTS        = "registrationRequests";
    private static final String PATH_TUTORS              = "tutors";
    private static final String PATH_STUDENTS            = "students";
    private static final String PATH_SLOTS               = "slots";
    private static final String PATH_SESSIONS            = "sessions";
    private static final String PATH_BOOKINGS            = "bookings";

    private static final String PATH_STUDENT_SLOT_REQ    = "StudentSlotRequests";

    private static final String TAG = "FirebaseRepository";

    private final FirebaseAuth auth;
    private final DatabaseReference db;

    public FirebaseRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
    }

    public void registerUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public void logout() {
        auth.signOut();
    }

    public void addRegistrationRequest(RegistrationRequest request, OnCompleteListener<Void> listener) {
        String id = db.child("registrationRequests").push().getKey();
        db.child("registrationRequests").child(id).setValue(request)
                .addOnCompleteListener(listener);
    }

    public Query getPendingRequests() {
        return db.child("registrationRequests")
                .orderByChild("status")
                .equalTo("PENDING");
    }

    public void updateRequestStatus(String requestId, String status, OnCompleteListener<Void> listener) {
        db.child("registrationRequests").child(requestId)
                .child("status").setValue(status)
                .addOnCompleteListener(listener);
    }

    public void addTutor(String uid, Tutor tutor, OnCompleteListener<Void> listener) {
        db.child("tutors").child(uid).setValue(tutor)
                .addOnCompleteListener(listener);
    }

    public void addStudent(String uid, Student student, OnCompleteListener<Void> listener) {
        db.child("students").child(uid).setValue(student)
                .addOnCompleteListener(listener);
    }

    public void addSlot(Slot slot, OnCompleteListener<Void> listener) {
        String id = db.child("slots").push().getKey();
        slot.setSlotId(id);
        db.child("slots").child(id).setValue(slot)
                .addOnCompleteListener(listener);
    }

    public void deleteSlot(String slotId, OnCompleteListener<Void> listener) {
        db.child("slots").child(slotId).removeValue()
                .addOnCompleteListener(listener);
    }

    public Query getSlotsByTutor(String tutorId) {
        return db.child("slots").orderByChild("tutorId").equalTo(tutorId);
    }

    // we mark the slot of the tutor is already booked or not based on its current status
    public void updateSlotBooking(String slotId, boolean isBooked, String bookingId) {

        // Update slot booking state
        db.child("slots")
                .child(slotId)
                .child("isBooked")
                .setValue(isBooked);

        // Update or clear booking id
        if (isBooked) {
            db.child("slots")
                    .child(slotId)
                    .child("bookingId")
                    .setValue(bookingId);
        } else {
            db.child("slots")
                    .child(slotId)
                    .child("bookingId")
                    .setValue(null);
        }
    }


    public void addSession(Session session, OnCompleteListener<Void> listener) {
        String id = db.child("sessions").push().getKey();
        session.setSessionId(id);

        db.child("sessions").child(id).setValue(session)
                .addOnCompleteListener(listener);
    }

    public void updateSessionStatus(String sessionId, String status, OnCompleteListener<Void> listener) {
        db.child("sessions").child(sessionId)
                .child("status").setValue(status)
                .addOnCompleteListener(listener);
    }


    public Query getSessionsByTutor(String tutorId) {
        return db.child("sessions")
                .orderByChild("tutorId")
                .equalTo(tutorId);
    }

    public Query getSessionsByStudent(String studentId) {
        return db.child("sessions")
                .orderByChild("studentId")
                .equalTo(studentId);
    }

    public void createSessionFromRequest(String requestId, Map<String, Object> sessionData) {
        DatabaseReference sessionsRef = FirebaseDatabase.getInstance().getReference("sessions");
        String newSessionId = sessionsRef.push().getKey(); // always create a fresh ID

        if (newSessionId != null) {
            sessionData.put("sessionId", newSessionId);
            sessionsRef.child(newSessionId).setValue(sessionData)
                    .addOnSuccessListener(aVoid ->
                            Log.d("FirebaseRepository", "Session created successfully: " + newSessionId))
                    .addOnFailureListener(e ->
                            Log.e("FirebaseRepository", "Failed to create session", e));
        } else {
            Log.e("FirebaseRepository", "Failed to generate sessionId");
        }
    }


    public void addStudentSlotRequest(Session session, OnCompleteListener<Void> listener) {
        String id = db.child("StudentSlotRequests").push().getKey();
        session.setSessionId(id);
        db.child("StudentSlotRequests").child(id).setValue(session)
                .addOnCompleteListener(listener);
    }

    public void updateStudentSlotRequestStatus(String requestId, String status, OnCompleteListener<Void> listener) {
        db.child("StudentSlotRequests").child(requestId)
                .child("status").setValue(status)
                .addOnCompleteListener(listener);
    }

    public Query getAllStudentSlotRequests() {
        return db.child("StudentSlotRequests");
    }

    public Query getPendingStudentSlotRequests() {
        return db.child("StudentSlotRequests")
                .orderByChild("status").equalTo("PENDING");
    }

    public Query getStudentSlotRequestsByTutor(String tutorId) {
        return db.child("StudentSlotRequests")
                .orderByChild("tutorId").equalTo(tutorId);
    }

    public Query getStudentSlotRequestsByStudent(String studentId) {
        return db.child("StudentSlotRequests")
                .orderByChild("studentId")
                .equalTo(studentId);
    }


    // this go to the bookings section in database and then create each booking with an unique ID
    public void addStudentBooking(StudentBooking booking) {
        String id = db.child("bookings").push().getKey();
        booking.setBookingId(id);

        db.child("bookings").child(id).setValue(booking);
    }
    // this will show the booking that corresponds to the student based on their student ID
    public Query getBookingsByStudent(String studentId) {
        return db.child("bookings")
                .orderByChild("studentId")
                .equalTo(studentId);
    }
    // Using student ID, their booking status can be changed to cancelled and then update the status
    public void cancelBooking(String bookingId,String slotId) {
        db.child("bookings")
                .child(bookingId)
                .child("status")
                .setValue("Cancelled");
        updateSlotBooking(slotId, false, null);
        checkAndCancelSessionIfNoBookings(slotId);

    }
    public DatabaseReference getDatabaseReference(String path) {
        return db.child(path);
    }
    // this help tutor cancel the session and then cancel the slot
    public void tutorCancelBooking(String bookingId, String slotId) {
        db.child("bookings")
                .child(bookingId)
                .child("status")
                .setValue("Cancelled");
        updateSlotBooking(slotId, false, null);
        checkAndCancelSessionIfNoBookings(slotId);

    }
    public void updateTutorAverageRating(String tutorId) {
        DatabaseReference ratingsRef = db.child("ratings").child(tutorId);

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;
                int count = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    Integer stars = child.child("stars").getValue(Integer.class);
                    if (stars != null) {
                        total += stars;
                        count++;
                    }
                }

                if (count == 0) return;

                double avg = total / count;

                db.child("tutors")
                        .child(tutorId)
                        .child("averageRating")
                        .setValue(avg);

                db.child("tutors")
                        .child(tutorId)
                        .child("ratingsCount")
                        .setValue(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    // this will cancel the session if every student cancel their booking
    private void checkAndCancelSessionIfNoBookings(String slotId) {

        db.child("bookings")
                .orderByChild("slotId")
                .equalTo(slotId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        boolean hasActiveBooking = false;

                        for (DataSnapshot b : snapshot.getChildren()) {
                            String status = b.child("status").getValue(String.class);

                            if (status != null &&
                                    !status.equalsIgnoreCase("Cancelled") &&
                                    !status.equalsIgnoreCase("Rejected")) {

                                hasActiveBooking = true;
                                break;
                            }
                        }

                        if (!hasActiveBooking) {
                            cancelSession(slotId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    private void cancelSession(String slotId) {

        db.child("sessions")
                .orderByChild("slotId")
                .equalTo(slotId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot s : snapshot.getChildren()) {
                            s.getRef().child("status").setValue("CANCELED");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }
    public void notifyStudentTutorCancelled(String studentId, String bookingId) {
        FirebaseDatabase.getInstance()
                .getReference("cancel_notifications")
                .child(studentId)
                .child(bookingId)
                .setValue(true);
        notifyStudentTutorCancelled(studentId, bookingId);

    }

}
