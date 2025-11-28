package com.example.test.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.sharedfiles.adapters.RequestAdapter;
import com.example.test.sharedfiles.model.RegistrationRequest;
import com.example.test.sharedfiles.model.User;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminInboxActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private RequestAdapter pendingAdapter, rejectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inbox);

        databaseRef = FirebaseDatabase.getInstance().getReference("registrationRequests");

        RecyclerView rvPending = findViewById(R.id.rvPending);
        RecyclerView rvRejected = findViewById(R.id.rvRejected);

        rvPending.setLayoutManager(new LinearLayoutManager(this));
        rvRejected.setLayoutManager(new LinearLayoutManager(this));

        pendingAdapter = new RequestAdapter(true, new RequestAdapter.RequestAdapterListener() {
            @Override
            public void onApprove(RegistrationRequest r) {
                approve(r);
            }

            @Override
            public void onReject(RegistrationRequest r) {
                reject(r);
            }
        });

        rejectedAdapter = new RequestAdapter(false, new RequestAdapter.RequestAdapterListener() {
            @Override
            public void onApprove(RegistrationRequest r) {
                approve(r);
            }

            @Override
            public void onReject(RegistrationRequest r) { }
        });

        rvPending.setAdapter(pendingAdapter);
        rvRejected.setAdapter(rejectedAdapter);

        listenForLists();
    }

    private void listenForLists() {
        databaseRef.orderByChild("status").equalTo("PENDING")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<RegistrationRequest> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            RegistrationRequest r = child.getValue(RegistrationRequest.class);
                            if (r != null) list.add(r);
                        }
                        pendingAdapter.setRequests(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        databaseRef.orderByChild("status").equalTo("REJECTED")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<RegistrationRequest> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            RegistrationRequest r = child.getValue(RegistrationRequest.class);
                            if (r != null) list.add(r);
                        }
                        rejectedAdapter.setRequests(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private static String keyFromEmail(String email) {
        return email.replace(".", "_");
    }

    private void approve(RegistrationRequest r) {

        if (r.getUserId() == null || r.getUserId().isEmpty()) {
            Toast.makeText(this, "Error: Missing userId in request", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailKey = keyFromEmail(r.getEmail());
        DatabaseReference requestNode = databaseRef.child(emailKey);

        requestNode.child("status").setValue("APPROVED");
        requestNode.child("decidedAt").setValue(System.currentTimeMillis());
        requestNode.child("decidedByAdminId").setValue("admin@uottawa.ca");
        requestNode.child("rejectionReason").removeValue();

        String path = r.getRole().equalsIgnoreCase("Tutor") ? "tutors" : "students";
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(path).child(r.getUserId());

        User baseUser = new User(
                r.getUserId(),
                r.getFirstName(),
                r.getLastName(),
                r.getEmail(),
                r.getPassword(),
                r.getPhone(),
                r.getRole()
        );
        baseUser.setStatus("APPROVED");

        Map<String, Object> extra = new HashMap<>();

        if (r.getRole().equalsIgnoreCase("Student")) {
            extra.put("programOfStudy", r.getProgramOfStudy());
        }

        if (r.getRole().equalsIgnoreCase("Tutor")) {
            extra.put("highestDegree", r.getHighestDegree());
            extra.put("coursesOffered", r.getCoursesOffered());
        }

        userRef.setValue(baseUser)
                .addOnSuccessListener(unused -> {

                    userRef.updateChildren(extra)
                            .addOnSuccessListener(unused2 -> {

                                requestNode.removeValue();

                                Toast.makeText(this,
                                        "Approved  ",
                                        Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Failed to copy extra fields: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show()
                            );

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to create user: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void reject(RegistrationRequest r) {
        String key = keyFromEmail(r.getEmail());
        DatabaseReference node = databaseRef.child(key);

        node.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                String cur = snap.getValue(String.class);
                if ("APPROVED".equals(cur)) {
                    Toast.makeText(AdminInboxActivity.this,
                            "Cannot reject: already approved", Toast.LENGTH_SHORT).show();
                    return;
                }

                node.child("status").setValue("REJECTED");
                node.child("decidedAt").setValue(System.currentTimeMillis());
                node.child("decidedByAdminId").setValue("admin@uottawa.ca");

                Toast.makeText(AdminInboxActivity.this,
                        "Rejected " + r.getEmail(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
