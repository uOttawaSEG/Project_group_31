package com.example.test.tutor;

import com.example.test.sharedfiles.model.Session;
import com.example.test.sharedfiles.model.Slot;
import com.example.test.sharedfiles.model.User;

import java.util.ArrayList;
import java.util.List;

public class Tutor extends User {
    private String highestDegree;
    private List<String> coursesOffered;

    private boolean autoApproval;

    private List<Slot> slots;
    private List<Session> sessions;

    public Tutor() {
        // Firebase needs an empty constructor
    }

    public Tutor(String firstName, String lastName, String email, String password,
                 String phoneNumber, String highestDegree, List<String> coursesOffered) {

        super(firstName, lastName, email, password, phoneNumber, "Tutor");
        this.highestDegree = highestDegree;
        this.coursesOffered = coursesOffered;
        this.autoApproval = false; // default: manual approval
        this.slots = new ArrayList<>();
        this.sessions = new ArrayList<>();
    }

    // Getters and Setters
    public String getHighestDegree() {
        return highestDegree;
    }

    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree;
    }

    public List<String> getCoursesOffered() {
        return coursesOffered;
    }

    public void setCoursesOffered(List<String> coursesOffered) {
        this.coursesOffered = coursesOffered;
    }

    public boolean isAutoApproval() {
        return autoApproval;
    }

    public void setAutoApproval(boolean autoApproval) {
        this.autoApproval = autoApproval;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = (slots != null) ? slots : new ArrayList<>();
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = (sessions != null) ? sessions : new ArrayList<>();
    }

    public void approveSession(Session session) {
        if (session != null) session.approve();
    }

    public void rejectSession(Session session) {
        if (session != null) session.reject();
    }

    public void cancelSession(Session session) {
        if (session != null) session.cancel();
    }
}
