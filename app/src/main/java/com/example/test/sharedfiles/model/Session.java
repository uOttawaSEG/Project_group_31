package com.example.test.sharedfiles.model;

public class Session {
    private String tutorId;
    private String studentId;
    private String slotId;
    private String status;


    public Session(String tutorId, String studentId, String slotId, String status    ) {
        this.slotId = slotId;
        this.studentId = studentId;
        this.slotId = slotId;
        this.status = status;


    }

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
