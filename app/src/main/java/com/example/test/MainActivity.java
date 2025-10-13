package com.example.test;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("test_message");
        ref.setValue("Hello Firebase! ")
                .addOnSuccessListener(aVoid ->
                        System.out.println(" Firebase write successful!")
                )
                .addOnFailureListener(e ->
                        System.out.println(" Firebase write failed: " + e.getMessage())
                );
        // Navigation between screens
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
