package com.example.test.sharedfiles.model;

import java.text.SimpleDateFormat;

public class StudentBooking {

    private String bookingId;
    private String sessionId;
    private String studentId;
    private String tutorId;
    private String courseCode;
    private String date;
    private String startTime;
    private String endTime;

    private String status;     // it can be "Pending", "Approved", "Rejected", "Cancelled"
    private String slotId;   // the slot ID that linked to the booking

    public StudentBooking() {
        // Firebase need this empty constructor
    }

    public StudentBooking(String bookingId, String sessionId, String studentId, String tutorId,
                          String courseCode, String date, String startTime, String endTime,
                          String status, String slotId) {

        this.bookingId = bookingId;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.courseCode = courseCode;

        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;

        this.status = status;

        this.slotId = slotId;
    }


    // getter and setter methods which allow other class to read and update them

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getTutorId() { return tutorId; }
    public void setTutorId(String tutorId) { this.tutorId = tutorId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }


    // this toMillis method allows the systems to convert the time into millisecons, which will serve
    // to compare if the moment student cancel their booked session is more than 24h before the session start
    private long toMillis(String d, String t) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.parse(d + " " + t).getTime();
    }

    // this method determine if the student is allowed to cancel their booked session or not
    public boolean canCancel() {
        if (status == null) return false;

        if (status.equals("Pending")) return true;
        if (status.equals("Approved")) return moreThan24HoursBefore();

        return false;
    }

    // we need to check if the session is already in the past or not
    public boolean isPast() {
        try {
            long sessionStart = toMillis(date, startTime);
            return System.currentTimeMillis() > sessionStart;
        } catch (Exception e) {
            return false;
        }
    }
    // we check if it's more than 24 hours before the starting time of the session
    private boolean moreThan24HoursBefore() {
        try {
            long sessionStart = toMillis(date, startTime);
            long diff = sessionStart - System.currentTimeMillis();

            return diff > (24L * 60L * 60L * 1000L); // this equation is 24 hours
        } catch (Exception e) {
            return false;
        }
    }
}
