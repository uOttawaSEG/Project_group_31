package com.example.test;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity that provides an admin interface for managing user registration requests.
 * Displays two lists: pending requests and rejected requests.
 * Admins can approve or reject requests, which updates the Firebase database in real-time.
 */
public class AdminInboxActivity extends AppCompatActivity {

    // Firebase database reference pointing to the "registrationRequests" node
    private DatabaseReference databaseRef;

    // Adapters for managing the two RecyclerView lists
    private RequestAdapter pendingAdapter, rejectedAdapter;

    /**
     * Called when the activity is first created.
     * Initializes the UI, sets up RecyclerViews, and starts listening for database changes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inbox);

        // Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("registrationRequests");

        // Find the RecyclerViews from the layout
        RecyclerView rvPending = findViewById(R.id.rvPending);
        RecyclerView rvRejected = findViewById(R.id.rvRejected);

        // Set layout managers for both RecyclerViews (vertical scrolling lists)
        rvPending.setLayoutManager(new LinearLayoutManager(this));
        rvRejected.setLayoutManager(new LinearLayoutManager(this));

        // Create adapter for pending requests with approve/reject callbacks
        pendingAdapter = new RequestAdapter(true, new RequestAdapter.RequestAdapterListener() {
            @Override public void onApprove(RegistrationRequest r) { approve(r); }
            @Override public void onReject(RegistrationRequest r) { reject(r); }
        });

        // Create adapter for rejected requests (only approve action available)
        rejectedAdapter = new RequestAdapter(false, new RequestAdapter.RequestAdapterListener() {
            @Override public void onApprove(RegistrationRequest r) { approve(r); }
            @Override public void onReject(RegistrationRequest r) { } // Empty - already rejected
        });

        // Attach adapters to RecyclerViews
        rvPending.setAdapter(pendingAdapter);
        rvRejected.setAdapter(rejectedAdapter);

        // Start listening for real-time database updates
        listenForLists();
    }

    /**
     * Sets up real-time Firebase listeners to automatically update both lists
     * when registration requests change in the database.
     */
    private void listenForLists() {
        // Listen for all requests with status "PENDING"
        databaseRef.orderByChild("status").equalTo("PENDING")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snapshot) {
                        // Create a list to hold all pending requests
                        List<RegistrationRequest> list = new ArrayList<>();

                        // Iterate through all children in the snapshot
                        for (DataSnapshot child : snapshot.getChildren()) {
                            // Convert each child to a RegistrationRequest object
                            RegistrationRequest r = child.getValue(RegistrationRequest.class);
                            if (r != null) list.add(r);
                        }

                        // Update the adapter with the new list
                        pendingAdapter.setRequests(list);
                    }
                    @Override public void onCancelled(DatabaseError e) { }
                });

        // Listen for all requests with status "REJECTED"
        databaseRef.orderByChild("status").equalTo("REJECTED")
                .addValueEventListener(new ValueEventListener() {
                    @Override public void onDataChange(DataSnapshot snapshot) {
                        // Create a list to hold all rejected requests
                        List<RegistrationRequest> list = new ArrayList<>();

                        // Iterate through all children in the snapshot
                        for (DataSnapshot child : snapshot.getChildren()) {
                            // Convert each child to a RegistrationRequest object
                            RegistrationRequest r = child.getValue(RegistrationRequest.class);
                            if (r != null) list.add(r);
                        }

                        // Update the adapter with the new list
                        rejectedAdapter.setRequests(list);
                    }
                    @Override public void onCancelled(DatabaseError e) { }
                });
    }

    /**
     * Converts an email address to a valid Firebase database key.
     * Firebase keys cannot contain periods, so they are replaced with underscores.
     *
     * @param email The email address to convert
     * @return A Firebase-safe key string
     */
    private static String keyFromEmail(String email) {
        return email.replace(".", "_");
    }

    /**
     * Approves a registration request by updating its status in Firebase.
     * Uses a transaction to ensure thread-safe updates and prevent race conditions.
     *
     * @param r The registration request to approve
     */
    private void approve(RegistrationRequest r) {
        // Get the Firebase key from the email
        String key = keyFromEmail(r.getEmail());
        DatabaseReference node = databaseRef.child(key);

        // Use a transaction to safely update the status field
        node.child("status").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData current) {
                // Get the current status value
                String cur = current.getValue(String.class);

                // If already approved, don't change anything
                if ("APPROVED".equals(cur)) {
                    return Transaction.success(current);
                }

                // Otherwise, set status to APPROVED
                current.setValue("APPROVED");
                return Transaction.success(current);
            }

            @Override
            public void onComplete(DatabaseError e, boolean committed, DataSnapshot dataSnapshot) {
                // Check if transaction failed
                if (e != null) {
                    Toast.makeText(AdminInboxActivity.this, "Approve failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update additional fields to track the decision
                long now = System.currentTimeMillis();
                node.child("decidedAt").setValue(now); // Timestamp of decision
                node.child("decidedByAdminId").setValue("admin@uottawa.ca"); // Admin who approved
                node.child("rejectionReason").removeValue(); // Clear any previous rejection reason

                // Show success message
                Toast.makeText(AdminInboxActivity.this, "Approved " + r.getEmail(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Rejects a registration request by updating its status in Firebase.
     * Checks if the request is already approved before rejecting.
     *
     * @param r The registration request to reject
     */
    private void reject(RegistrationRequest r) {
        // Get the Firebase key from the email
        String key = keyFromEmail(r.getEmail());
        DatabaseReference node = databaseRef.child(key);

        // First, read the current status to ensure it's not already approved
        node.child("status").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot snap) {
                // Get the current status value
                String cur = snap.getValue(String.class);

                // Prevent rejecting an already-approved request
                if ("APPROVED".equals(cur)) {
                    Toast.makeText(AdminInboxActivity.this, "Cannot reject: already approved", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update the status to REJECTED and add metadata
                node.child("status").setValue("REJECTED");
                node.child("decidedAt").setValue(System.currentTimeMillis()); // Timestamp
                node.child("decidedByAdminId").setValue("admin@uottawa.ca"); // Admin who rejected

                // Show success message
                Toast.makeText(AdminInboxActivity.this, "Rejected " + r.getEmail(), Toast.LENGTH_SHORT).show();
            }
            @Override public void onCancelled(DatabaseError e) { }
        });
    }
}