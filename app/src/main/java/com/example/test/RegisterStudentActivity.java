package com.example.test;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterStudentActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPassword, etPhone, etProgram;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etProgram = findViewById(R.id.etProgram);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            Toast.makeText(this, "Register clicked, navigation not implemented yet.", Toast.LENGTH_SHORT).show();
        });
    }
}
