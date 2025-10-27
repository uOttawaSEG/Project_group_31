package com.example.test;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class AdminInboxActivity extends AppCompatActivity {

    private DatabaseReference requestsRef;
    private RequestAdapter pendingAdapter, rejectedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inbox);

        requestsRef = FirebaseDatabase.getInstance().getReference("registrationRequests");

        RecyclerView rvPending = findViewById(R.id.rvPending);
        RecyclerView rvRejected = findViewById(R.id.rvRejected);

        rvPending.setLayoutManager(new LinearLayoutManager(this));
        rvRejected.setLayoutManager(new LinearLayoutManager(this));

        pendingAdapter = new RequestAdapter(RequestAdapter.Mode.PENDING, new RequestAdapter.Listener() {
            @Override public void onApprove(RegistrationRequest r) { approve(r); }
            @Override public void onReject(RegistrationRequest r) { reject(r); }
        });

        rejectedAdapter = new RequestAdapter(RequestAdapter.Mode.REJECTED, new RequestAdapter.Listener() {
            @Override public void onApprove(RegistrationRequest r) { approve(r); } // reversal path
            @Override public void onReject(RegistrationRequest r) { /* no-op */ }
        });

        rvPending.setAdapter(pendingAdapter);
        rvRejected.setAdapter(rejectedAdapter);

        listenForLists();
    }

    private void listenForLists() {
        requestsRef.orderByChild("status").equalTo("PENDING")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        List<RegistrationRequest> list = new ArrayList<>();
                        for (DataSnapshot child : snap.getChildren()) {
                            RegistrationRequest r = child.getValue(RegistrationRequest.class);
                            if (r != null) list.add(r);
                        }
                        pendingAdapter.submit(list);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) { }
                });

        requestsRef.orderByChild("status").equalTo("REJECTED")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snap) {
                        List<RegistrationRequest> list = new ArrayList<>();
                        for (DataSnapshot child : snap.getChildren()) {
                            RegistrationRequest r = child.getValue(RegistrationRequest.class);
                            if (r != null) list.add(r);
                        }
                        rejectedAdapter.submit(list);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) { }
                });
    }

    private static String keyFromEmail(String email) {
        return email.replace(".", "_");
    }

    /** APPROVAL IS IRREVERSIBLE. If already APPROVED, do nothing. */
    private void approve(RegistrationRequest r) {
        String key = keyFromEmail(r.getEmail());
        DatabaseReference node = requestsRef.child(key);
        node.child("status").runTransaction(new Transaction.Handler() {
            @NonNull @Override
            public Transaction.Result doTransaction(@NonNull MutableData current) {
                String cur = current.getValue(String.class);
                if ("APPROVED".equals(cur)) {
                    return Transaction.success(current); // already approved
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

    /** Only allowed when NOT already APPROVED. */
    private void reject(RegistrationRequest r) {
        String key = keyFromEmail(r.getEmail());
        DatabaseReference node = requestsRef.child(key);
        node.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
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
            @Override public void onCancelled(@NonNull DatabaseError e) { }
        });
    }
}
