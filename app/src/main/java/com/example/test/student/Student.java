package com.example.test.student;

import com.example.test.sharedfiles.model.User;


public class Student extends User {
    private String programOfStudy;

    public Student() {
        // Firebase needs an empty constructor
    }

    public Student(String firstName, String lastName, String email, String password,
                   String phoneNumber, String programOfStudy){

        super(firstName, lastName, email, password, phoneNumber, "Student");
        this.programOfStudy = programOfStudy;
    }


    public String getProgramOfStudy() {
        return programOfStudy;
    }
    public void setProgramOfStudy(String programOfStudy) {
        this.programOfStudy = programOfStudy;
    }

    public String getCourse() {
        return programOfStudy;
    }

    public void setCourse(String course) {
        this.programOfStudy = course;
    }

    @Override
    public String toString() {
        return "Student{" +
                "firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", programOfStudy='" + programOfStudy + '\'' +
                '}';
    }
}
