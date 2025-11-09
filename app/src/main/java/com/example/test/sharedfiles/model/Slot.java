package com.example.test.sharedfiles.model;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class Slot {
    private String slotId;
    private String tutorId;
    private String date;
    private String startTime;
    private String endTime;
    private boolean requiresApproval;
    private boolean isAvailable;

    public Slot() {
        // Firebase requires an empty constructor
    }

    public Slot (String tutorId, String date, String startTime, String endTime, boolean requiresApproval, boolean isAvailable) {
        this.tutorId = tutorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.requiresApproval = requiresApproval;
        this.isAvailable = isAvailable;
    }

    //Getters and Setters
    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;

    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;

    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;

    }

    public boolean isRequiresApproval() {
        return requiresApproval;
    }

    public void setRequiresApproval(boolean requiresApproval) {
        this.requiresApproval = requiresApproval;

    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;

    }

    //checks if the slot is from the past
    public boolean isPast() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //combines date and endTime strings and converts it to Date objects
            Date slotEndDate = simpleDateFormat.parse(date + " " + endTime);

            Date now = Calendar.getInstance().getTime();

            //returns true if the slot's end time is before the current time
            return slotEndDate.before(now);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }

    //checks overlapping slots
    public boolean overlaps(Slot other) {
        if (other == null) {
            return false;
        }

        if (!this.date.equals(other.date)) {
            return false;
        }

        try {
            //converting string to Date objects
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date start = simpleDateFormat.parse(startTime);
            Date end = simpleDateFormat.parse(endTime);
            Date startOther = simpleDateFormat.parse(other.startTime);
            Date endOther = simpleDateFormat.parse(other.endTime);

            return start.before(endOther) && end.after(startOther);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

    }
}