package com.example.test;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterTutorActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPassword, etPhone, etDegree, etCourses;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tutor);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etDegree = findViewById(R.id.etDegree);
        etCourses = findViewById(R.id.etCourses);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Register clicked, navigation not implemented yet.", Toast.LENGTH_SHORT).show();
        });
    }
}
