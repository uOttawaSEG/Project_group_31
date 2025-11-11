package com.example.test.sharedfiles.model;

public class Session {
    private String sessionId;
    private String tutorId;
    private String studentId;
    private String slotId;
    private String status;

    private String date;
    private String startTime;
    private String endTime;

    public Session() {
        // Firebase requires an empty constructor
    }

    public Session(String tutorId, String studentId, String slotId, String status) {
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.slotId = slotId;
        this.status = status;
    }

    //Getters and Setters

    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTutorId() {
        return tutorId;
    }
    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }



    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }


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