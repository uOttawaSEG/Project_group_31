package com.example.test;

import android.os.Bundle;
import android.view.View;
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

/**
 * Handles student registration by creating a Firebase auth account,
 * storing student data in the database, and submitting a registration request for admin approval.
 */
public class RegisterStudentActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private EditText firstName, lastName, email, password, phone, program;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        // Initialize Firebase authentication and database reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("students");

        // Get references to input fields
        firstName = findViewById(R.id.etFirstName);
        lastName = findViewById(R.id.etLastName);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        phone = findViewById(R.id.etPhone);
        program = findViewById(R.id.etProgram);
        registerButton = findViewById(R.id.btnRegister);

        // Set up register button click handler
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStudent();
            }
        });
    }

    /**
     * Validates input, creates a Firebase auth account, saves student data to the database,
     * and submits a registration request for admin approval.
     */
    private void registerStudent() {
        // Get and trim input values
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String em = email.getText().toString().trim();
        String pw = password.getText().toString().trim();
        String ph = phone.getText().toString().trim();
        String prog = program.getText().toString().trim();

        // Validate that all fields are filled
        if (fName.isEmpty() || lName.isEmpty() || em.isEmpty() ||
                pw.isEmpty() || ph.isEmpty() || prog.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate password length
        if (pw.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Firebase authentication account
        mAuth.createUserWithEmailAndPassword(em, pw)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        // Prepare student data map
                        Map<String, Object> studentMap = new HashMap<>();
                        studentMap.put("firstName", fName);
                        studentMap.put("lastName", lName);
                        studentMap.put("email", em);
                        studentMap.put("phone", ph);
                        studentMap.put("programOfStudy", prog);
                        studentMap.put("role", "Student");
                        studentMap.put("status", "Pending");

                        // Save student data to database using their UID as the key
                        databaseReference.child(uid).setValue(studentMap)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Student registered successfully!", Toast.LENGTH_SHORT).show();

                                    // Create registration request for admin approval
                                    RegistrationRequest req = new RegistrationRequest(fName, lName, em, ph, "Student");
                                    req.setProgramOfStudy(prog);

                                    // Convert email to Firebase-safe key
                                    String safeEmailKey = em.replace(".", "_");

                                    // Submit registration request to admin queue
                                    FirebaseDatabase.getInstance().getReference("registrationRequests")
                                            .child(safeEmailKey)
                                            .setValue(req)
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