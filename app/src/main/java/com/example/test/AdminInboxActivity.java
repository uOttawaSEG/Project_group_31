package com.example.test;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

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
            @Override public void onApprove(RegistrationRequest r) { approve(r); }
            @Override public void onReject(RegistrationRequest r) { reject(r); }
        });

        rejectedAdapter = new RequestAdapter(false, new RequestAdapter.RequestAdapterListener() {
            @Override public void onApprove(RegistrationRequest r) { approve(r); }
            @Override public void onReject(RegistrationRequest r) { }
        });

        rvPending.setAdapter(pendingAdapter);
        rvRejected.setAdapter(rejectedAdapter);

        listenForLists();
    }

    private void listenForLists() {
        databaseRef.orderByChild("status").equalTo("PENDING")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snapshot) {
                        List<RegistrationRequest> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            RegistrationRequest r = child.getValue(RegistrationRequest.class);
                            if (r != null) list.add(r);
                        }
                        pendingAdapter.setRequests(list);
                    }
                    @Override public void onCancelled(DatabaseError e) { }
                });

        databaseRef.orderByChild("status").equalTo("REJECTED")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snapshot) {
                        List<RegistrationRequest> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            RegistrationRequest r = child.getValue(RegistrationRequest.class);
                            if (r != null) list.add(r);
                        }
                        rejectedAdapter.setRequests(list);
                    }
                    @Override public void onCancelled(DatabaseError e) { }
                });
    }

    private static String keyFromEmail(String email) {
        return email.replace(".", "_");
    }

    private void approve(RegistrationRequest r) {
        String key = keyFromEmail(r.getEmail());
        DatabaseReference node = databaseRef.child(key);
        node.child("status").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData current) {
                String cur = current.getValue(String.class);
                if ("APPROVED".equals(cur)) {
                    return Transaction.success(current);
                }
                current.setValue("APPROVED");
                return Transaction.success(current);
            }
            @Override
            public void onComplete(DatabaseError e, boolean committed, DataSnapshot dataSnapshot) {
                if (e != null) {
                    Toast.makeText(AdminInboxActivity.this, "Approve failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                long now = System.currentTimeMillis();
                node.child("decidedAt").setValue(now);
                node.child("decidedByAdminId").setValue("admin@uottawa.ca");
                node.child("rejectionReason").removeValue();
                Toast.makeText(AdminInboxActivity.this, "Approved " + r.getEmail(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void reject(RegistrationRequest r) {
        String key = keyFromEmail(r.getEmail());
        DatabaseReference node = databaseRef.child(key);
        node.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                String cur = snap.getValue(String.class);
                if ("APPROVED".equals(cur)) {
                    Toast.makeText(AdminInboxActivity.this, "Cannot reject: already approved", Toast.LENGTH_SHORT).show();
                    return;
                }
                node.child("status").setValue("REJECTED");
                node.child("decidedAt").setValue(System.currentTimeMillis());
                node.child("decidedByAdminId").setValue("admin@uottawa.ca");
                Toast.makeText(AdminInboxActivity.this, "Rejected " + r.getEmail(), Toast.LENGTH_SHORT).show();
            }
            @Override public void onCancelled(DatabaseError e) { }
        });
    }
}
