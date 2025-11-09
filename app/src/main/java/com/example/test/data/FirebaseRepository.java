package com.example.test.data;

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
        if (user != null) {
            return user.getUid();
        } else {
            return null;
        }
    }

    public void logout() {
        auth.signOut();
    }

    public void addRegistrationRequest(RegistrationRequest request, OnCompleteListener<Void> listener) {
        String id = db.child("registrationRequests").push().getKey();
        // request.setRequestId(id); // Your RegistrationRequest model has no setRequestId

        db.child("registrationRequests").child(id).setValue(request)
                .addOnCompleteListener(listener);
    }

    public Query getPendingRequests() {
        return db.child("registrationRequests")
                .orderByChild("status")
                .equalTo("PENDING");
    }

    public Query getRejectedRequests() {
        return db.child("registrationRequests")
                .orderByChild("status")
                .equalTo("REJECTED");
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

    public DatabaseReference getDatabaseReference(String path) {
        return db.child(path);
    }
}