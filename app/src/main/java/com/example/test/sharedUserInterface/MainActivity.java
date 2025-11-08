package com.example.test.sharedUserInterface;

import com.example.test.R;
import com.example.test.student.RegisterStudentActivity;
import com.example.test.tutor.RegisterTutorActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button loginBtn;
    Button registerStudentBtn;
    Button registerTutorBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.btnLogin);
        registerStudentBtn = findViewById(R.id.btnRegisterStudent);
        registerTutorBtn = findViewById(R.id.btnRegisterTutor);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        registerStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterStudentActivity.class));
            }
        });

        registerTutorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterTutorActivity.class));
            }
        });
    }
}
