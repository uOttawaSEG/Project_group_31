package com.example.test.tutor;

import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterTutorActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    EditText firstName, lastName, email, password, phone, degree, courses;
    Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tutor);

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("tutors");

        firstName = findViewById(R.id.etFirstName);
        lastName = findViewById(R.id.etLastName);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        phone = findViewById(R.id.etPhone);
        degree = findViewById(R.id.etDegree);
        courses = findViewById(R.id.etCourses);
        registerBtn = findViewById(R.id.btnRegister);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTutor();
            }
        });
    }

    private void registerTutor() {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String em = email.getText().toString().trim();
        String pw = password.getText().toString().trim();
        String ph = phone.getText().toString().trim();
        String deg = degree.getText().toString().trim();
        String crs = courses.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty() || em.isEmpty() ||
                pw.isEmpty() || ph.isEmpty() || deg.isEmpty() || crs.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(em, pw)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();

                        Map<String, Object> tutorMap = new HashMap<>();
                        tutorMap.put("firstName", fName);
                        tutorMap.put("lastName", lName);
                        tutorMap.put("email", em);
                        tutorMap.put("phone", ph);
                        tutorMap.put("highestdegree", deg);
                        tutorMap.put("coursesOffered", crs);
                        tutorMap.put("role", "Tutor");
                        tutorMap.put("status", "Pending");

                        databaseRef.child(uid).setValue(tutorMap)
                                .addOnSuccessListener(aVoid -> {
                                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString(em + "_email", em);
                                    editor.putString(em + "_password", pw);
                                    editor.putString(em + "_role", "Tutor");
                                    editor.putString(em + "_firstName", fName);
                                    editor.putString(em + "_lastName", lName);
                                    editor.putString(em + "_phone", ph);
                                    editor.putString(em + "_degree", deg);
                                    editor.putString(em + "_courses", crs);
                                    editor.apply();

                                    RegistrationRequest request = new RegistrationRequest(fName, lName, em, ph, "Tutor");
                                    request.setHighestDegree(deg);

                                    List<String> coursesArrayList = new ArrayList<>();
                                    for (String s : crs.split(",")) {
                                        String trimmedS = s.trim();
                                        if (!trimmedS.isEmpty()) coursesArrayList.add(trimmedS);
                                    }
                                    request.setCoursesOffered(coursesArrayList);

                                    String safeEmail = em.replace(".", "_");
                                    FirebaseDatabase.getInstance().getReference("registrationRequests")
                                            .child(safeEmail)
                                            .setValue(request)
                                            .addOnSuccessListener(x ->
                                                    Toast.makeText(this, "Registration submitted for admin approval", Toast.LENGTH_SHORT).show()
                                            )
                                            .addOnFailureListener(e2 ->
                                                    Toast.makeText(this, "Request failed: " + e2.getMessage(), Toast.LENGTH_SHORT).show());

                                    Toast.makeText(this, "Tutor Registered Successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
