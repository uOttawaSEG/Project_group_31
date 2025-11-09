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
}