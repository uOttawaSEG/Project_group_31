package com.example.test.student;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.example.test.sharedfiles.model.RegistrationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterStudentActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText firstName, lastName, email, password, phone, program;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        mAuth = FirebaseAuth.getInstance();

        firstName = findViewById(R.id.etFirstName);
        lastName = findViewById(R.id.etLastName);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        phone = findViewById(R.id.etPhone);
        program = findViewById(R.id.etProgram);
        registerButton = findViewById(R.id.btnRegister);

        registerButton.setOnClickListener(v -> registerStudent());
    }

    private void registerStudent() {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String em = email.getText().toString().trim();
        String pw = password.getText().toString().trim();
        String ph = phone.getText().toString().trim();
        String prog = program.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty() || em.isEmpty() ||
                pw.isEmpty() || ph.isEmpty() || prog.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pw.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(em, pw)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        // Create registration request
                        RegistrationRequest req = new RegistrationRequest(
                                fName,
                                lName,
                                em,
                                ph,
                                "Student",
                                uid,
                                pw
                        );
                        req.setProgramOfStudy(prog);

                        String safeEmailKey = em.replace(".", "_");

                        FirebaseDatabase.getInstance().getReference("registrationRequests")
                                .child(safeEmailKey)
                                .setValue(req)
                                .addOnSuccessListener(r ->
                                        Toast.makeText(this, "Registration submitted for admin approval", Toast.LENGTH_LONG).show()
                                )
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Request failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
