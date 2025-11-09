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
}