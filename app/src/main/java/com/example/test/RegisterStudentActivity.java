package com.example.test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import com.example.test.RegistrationRequest;

public class RegisterStudentActivity extends AppCompatActivity {

    // Declare Firebase and UI variables
    private FirebaseAuth auth;
    private DatabaseReference studentsRef;
    private EditText etFirstName, etLastName, etEmail, etPassword, etPhone, etProgram;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        studentsRef = FirebaseDatabase.getInstance().getReference("students");

        // Link UI components
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etProgram = findViewById(R.id.etProgram);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerStudent());
    }

    private void registerStudent() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String program = etProgram.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || phone.isEmpty() || program.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        // Validate password length (Firebase requires at least 6 chars)
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create Firebase Authentication user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        // Prepare student data
                        Map<String, Object> studentData = new HashMap<>();
                        studentData.put("firstName", firstName);
                        studentData.put("lastName", lastName);
                        studentData.put("email", email);
                        studentData.put("phone", phone);
                        studentData.put("program", program);
                        studentData.put("role", "student");

                        // Save to Realtime Database
                        studentsRef.child(uid).setValue(studentData)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Student registered successfully!", Toast.LENGTH_SHORT).show();

                                    //Admin Approval
                                    RegistrationRequest request = new RegistrationRequest(firstName, lastName, email, phone, "Student");

                                    //Need this because char dot causes errors in Firebase
                                    String dotKey = email.replace(".", "_");

                                    FirebaseDatabase.getInstance().getReference("registrationRequests").child(dotKey).setValue(request)
                                            .addOnSuccessListener(r ->
                                                    Toast.makeText(this, "Registration submitted for admin approval", Toast.LENGTH_SHORT).show()
                                            )
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(this, "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                                })

                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
