package com.example.test;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The main entry point of the app.
 * Displays three buttons: Login, Register as Student, and Register as Tutor.
 */
public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    Button registerStudentBtn;
    Button registerTutorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get references to the three buttons
        loginBtn = findViewById(R.id.btnLogin);
        registerStudentBtn = findViewById(R.id.btnRegisterStudent);
        registerTutorBtn = findViewById(R.id.btnRegisterTutor);

        // Test Firebase connection by writing a test message
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("test_message");
        ref.setValue("Hello Firebase! ");

        // Navigate to login screen
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        // Navigate to student registration screen
        registerStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterStudentActivity.class));
            }
        });

        // Navigate to tutor registration screen
        registerTutorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterTutorActivity.class));
            }
        });
    }
}