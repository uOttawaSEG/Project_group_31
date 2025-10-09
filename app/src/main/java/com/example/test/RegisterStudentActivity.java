package com.example.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            //  Save student details in the same key format that LoginActivity expects
            editor.putString(email + "_email", email);
            editor.putString(email + "_password", password);
            editor.putString(email + "_role", "Student");
            editor.putString(email + "_firstName", firstName);
            editor.putString(email + "_lastName", lastName);
            editor.putString(email + "_phone", phone);
            editor.putString(email + "_program", program);
            editor.apply();

            Toast.makeText(this, "Student Registered Successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
