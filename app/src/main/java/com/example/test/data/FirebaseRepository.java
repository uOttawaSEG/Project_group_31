package com.example.test.data;

import java.util.Map;
import android.util.Log;

import com.example.test.sharedfiles.model.RegistrationRequest;
import com.example.test.sharedfiles.model.Session;
import com.example.test.sharedfiles.model.Slot;
import com.example.test.student.Student;
import com.example.test.tutor.Tutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.example.test.sharedfiles.model.StudentBooking;


public class FirebaseRepository {

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



    public DatabaseReference getDatabaseReference(String path) {
        return db.child(path);
    }
}
