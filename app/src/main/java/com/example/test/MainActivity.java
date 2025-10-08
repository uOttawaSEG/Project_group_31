package com.example.test;

import android.content.Intent;
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

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );

        btnRegisterStudent.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterStudentActivity.class))
        );

        btnRegisterTutor.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterTutorActivity.class))
        );
    }
}
