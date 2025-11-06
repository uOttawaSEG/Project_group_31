package com.example.test;

/**
 * Represents a student user in the system.
 * Extends the User class with student-specific information.
 */
public class Student extends User {
    // Student's program of study (e.g., "Computer Science", "Engineering")
    private String programOfStudy;

    /**
     * Creates a new Student with the given details.
     * Role is automatically set to "Student".
     */
    public Student(String firstName, String lastName, String email, String password,
                   String phoneNumber, String programOfStudy){

        super(firstName, lastName, email, password, phoneNumber, "Student");
        this.programOfStudy = programOfStudy;
    }

    // Getter and setter for program of study
    public String getProgramOfStudy() {
        return programOfStudy;
    }
    public void setProgramOfStudy(String programOfStudy) {
        this.programOfStudy = programOfStudy;
    }
}