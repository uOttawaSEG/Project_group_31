package com.example.test.sharedfiles.model;

public class Rating {

    private String ratingId;     // ID of the rating in firebase
    private String tutorId;
    private String studentId;
    private String sessionId;    // the session that is rated

    private int stars;           // student will chose stars they think
    private String comment;      // Student can add some feedback if they want
    private long timestamp;

    public Rating() {
        // Firebase need this empty constructor
    }

    public Rating(String ratingId, String tutorId, String studentId,
                  String sessionId, int stars, String comment, long timestamp) {

        this.ratingId = ratingId;
        this.tutorId = tutorId;
        this.studentId = studentId;
        this.sessionId = sessionId;

        this.stars = stars;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // getter and setter methods which allow other class to read and update them

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
