package com.example.test;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnRegisterStudent, btnRegisterTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        btnRegisterTutor = findViewById(R.id.btnRegisterTutor);

        // No navigation yet
        btnLogin.setOnClickListener(v -> {
            // Placeholder, does nothing
        });

        btnRegisterStudent.setOnClickListener(v -> {
            // Placeholder, does nothing
        });

        btnRegisterTutor.setOnClickListener(v -> {
            // Placeholder, does nothing
        });
    }
}
