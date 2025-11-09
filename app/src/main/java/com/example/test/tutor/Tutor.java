package com.example.test.tutor;

import com.example.test.sharedfiles.model.Session;
import com.example.test.sharedfiles.model.Slot;
import com.example.test.sharedfiles.model.User;

import java.util.ArrayList;
import java.util.List;

public class Tutor extends User {
    private String highestDegree;
    private List<String> coursesOffered;

    private boolean autoApproved;
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

        this.slots = new ArrayList<>();
        this.sessions = new ArrayList<>();
        this.autoApproved = false;
    }


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

    public boolean getIsAutoApproved() {
        return autoApproved;
    }

    public void setAutoApproved(boolean autoApproved) {
        this.autoApproved = autoApproved;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        if (slots != null) {
            this.slots = slots;
        } else {
            this.slots = new ArrayList<>();
        }
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        if (sessions != null) {
            this.sessions = sessions;
        } else {
            this.sessions = new ArrayList<>();
        }
    }


    //sets status to APPROVED, REJECTED, or CANCELED
    public void approveSession(Session session) {
        if (session != null) {
            session.approve();
        }
    }


    public void rejectedSession(Session session) {
        if (session != null) {
            session.reject();
        }
    }

    public void cancelSession(Session session) {
        if (session != null) {
            session.cancel();
        }
    }
}