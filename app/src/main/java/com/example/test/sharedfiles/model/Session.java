package com.example.test.sharedfiles.model;

public class Session {
    private String tutorId;
    private String studentId;
    private String slotId;
    private String status;


    public Session(String tutorId, String studentId, String slotId, String status    ) {
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.slotId = slotId;
        this.status = status;


    }

    //Getters and Setters
    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;

    }

    public String studentId() {
        return studentId;
    }

    public void studentId(String studentId) {
        this.studentId = studentId;

    }

    public String slotId() {
        return slotId;
    }

    public void slotId(String slotId) {
        this.slotId = slotId;

    }

    public String status() {
        return status;
    }

    public void status(String status) {
        this.status = status;

    }



    // Status change methods
    public void approve() {
        this.status = "APPROVED";
    }

    public void reject() {
        this.status = "REJECTED";
    }

    public void cancel() {
        this.status = "CANCELED";
    }

























}
