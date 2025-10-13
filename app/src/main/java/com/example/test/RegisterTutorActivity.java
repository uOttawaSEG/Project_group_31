package com.example.test;

import android.content.SharedPreferences;
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

public class RegisterTutorActivity extends AppCompatActivity {

    // --- Firebase ---
    private FirebaseAuth auth;
    private DatabaseReference tutorsRef;

    EditText etFirstName, etLastName, etEmail, etPassword, etPhone, etDegree, etCourses;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tutor);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        tutorsRef = FirebaseDatabase.getInstance().getReference("tutors");

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etDegree = findViewById(R.id.etDegree);
        etCourses = findViewById(R.id.etCourses);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerTutor());
    }

    private void registerTutor() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String degree = etDegree.getText().toString().trim();
        String courses = etCourses.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || phone.isEmpty() || degree.isEmpty() || courses.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Create Firebase Authentication user ---
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        // --- Prepare tutor data for Realtime Database ---
                        Map<String, Object> tutorData = new HashMap<>();
                        tutorData.put("firstName", firstName);
                        tutorData.put("lastName", lastName);
                        tutorData.put("email", email);
                        tutorData.put("phone", phone);
                        tutorData.put("degree", degree);
                        tutorData.put("courses", courses);
                        tutorData.put("role", "tutor");

                        // --- Save to Firebase Realtime Database ---
                        tutorsRef.child(uid).setValue(tutorData)
                                .addOnSuccessListener(aVoid -> {
                                    // --- Also save locally in SharedPreferences (old logic preserved) ---
                                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString(email + "_email", email);
                                    editor.putString(email + "_password", password);
                                    editor.putString(email + "_role", "Tutor");
                                    editor.putString(email + "_firstName", firstName);
                                    editor.putString(email + "_lastName", lastName);
                                    editor.putString(email + "_phone", phone);
                                    editor.putString(email + "_degree", degree);
                                    editor.putString(email + "_courses", courses);
                                    editor.apply();

                                    Toast.makeText(this, "Tutor Registered Successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
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
