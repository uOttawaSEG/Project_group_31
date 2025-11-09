package com.example.test.sharedfiles.model;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class Slot {
    private String tutorId;
    private String date;
    private String startTime;
    private String endTime;
    private boolean requiresApproval;
    private boolean isAvailable;

    public Slot (String tutorId, String date, String startTime, String endTime, boolean requiresApproval, boolean isAvailable) {
        this.tutorId = tutorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.requiresApproval = requiresApproval;
        this.isAvailable = isAvailable;
    }

    //Getters and Setters
    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;

    }

    public String date() {
        return date;
    }

    public void date(String date) {
        this.date = date;

    }

    public String startTime() {
        return tutorId;
    }

    public void startTime(String startTime) {
        this.startTime = startTime;

    }

    public String endTime() {
        return endTime;
    }

    public void endTime(String endTime) {
        this.endTime = endTime;

    }

    public boolean requiresApproval() {
        return requiresApproval;
    }

    public void requiresApproval(boolean requiresApproval) {
        this.requiresApproval = requiresApproval;

    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void isAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;

    }

    //checks if the slot is from the past
    public boolean isPast() {
        //Parse slot's time
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date slotEndDate = simpleDateFormat.parse(date + " " + endTime);

        Date now = Calendar.getInstance().getTime();

        //returns true if the slot's end time is before the current time
        return slotEndDate.before(now);
    }

















}
