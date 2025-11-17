    package com.example.test.sharedfiles.model;

    import java.text.SimpleDateFormat;

    public class Session {
        private String sessionId;
        private String tutorId;
        private String studentId;
        private String slotId;
        private String status;

        private String date;
        private String startTime;
        private String endTime;

        private String courseName;
        private String studentEmail;
        private String courseCode;

        public Session() {
            // Firebase need an empty constructor
        }

        public Session(String sessionId, String tutorId, String studentId,
                       String slotId, String date, String startTime,
                       String endTime, String courseCode, String status) {

            this.sessionId = sessionId;
            this.tutorId = tutorId;
            this.studentId = studentId;
            this.slotId = slotId;
            this.date = date;
            this.startTime = startTime;
            this.endTime = endTime;
            this.courseCode = courseCode;
            this.status = status;
        }

        //Getters and Setters methods for other class to have access to read and update them
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

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }

        public String getStudentEmail() { return studentEmail; }
        public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

        // all the status of the session
        public void approve() {
            this.status = "APPROVED";
        }

        public void reject() {
            this.status = "REJECTED";
        }

        public void cancel() {
            this.status = "CANCELED";
        }

        // check if the session is already in the past
        public boolean isPast() {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                long sessionStart = sdf.parse(date + " " + startTime).getTime();
                return System.currentTimeMillis() > sessionStart;
            } catch (Exception e) {
                return false;
            }
        }

        // student can rate if the session is in the past
        public boolean canRate() {
            return "APPROVED".equals(status) && isPast();
        }
    }