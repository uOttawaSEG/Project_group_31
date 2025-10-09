package com.example.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            //  Save tutor details using the same key pattern
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
        });
    }
}
