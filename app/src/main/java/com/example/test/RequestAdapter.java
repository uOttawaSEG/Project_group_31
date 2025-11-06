package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying registration requests in a list.
 * Shows different information based on user role (Student vs Tutor) and request status.
 */
class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    /**
     * Interface for handling approve/reject button clicks.
     */
    interface RequestAdapterListener {
        void onApprove(RegistrationRequest r);
        void onReject(RegistrationRequest r);
    }

    private List<RegistrationRequest> requestList = new ArrayList<>();
    private final RequestAdapterListener myListener;
    private final boolean isPending; // True for pending list, false for rejected list
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    /**
     * Constructor to initialize the adapter.
     * @param isPending Whether this adapter is for pending or rejected requests
     * @param listener Callback for approve/reject actions
     */
    RequestAdapter(boolean isPending, RequestAdapterListener listener) {
        this.isPending = isPending;
        this.myListener = listener;
    }

    /**
     * Updates the list of requests and refreshes the display.
     */
    void setRequests(List<RegistrationRequest> items) {
        requestList.clear();
        if (items != null) {
            requestList.addAll(items);
        }
        notifyDataSetChanged();
    }

    /**
     * Creates a new ViewHolder by inflating the row layout.
     */
    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request, parent, false);
        return new RequestViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder for a specific position.
     * Displays different fields based on user role (Student vs Tutor).
     */
    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RegistrationRequest currentRequest = requestList.get(position);

        // Display common information
        holder.name.setText(currentRequest.getFirstName() + " " + currentRequest.getLastName() + " — " + currentRequest.getRole());
        holder.emailPhone.setText(currentRequest.getEmail() + " • " + currentRequest.getPhone());
        holder.date.setText("Submitted: " + simpleDateFormat.format(new Date(currentRequest.getSubmittedAt())));

        // Show student-specific fields
        if ("Student".equalsIgnoreCase(currentRequest.getRole())) {
            String program = currentRequest.getProgramOfStudy();
            if (program != null && !program.trim().isEmpty()) {
                holder.program.setText("Program: " + program);
                holder.program.setVisibility(View.VISIBLE);
            } else {
                holder.program.setVisibility(View.GONE);
            }
            // Hide tutor fields
            holder.degree.setVisibility(View.GONE);
            holder.courses.setVisibility(View.GONE);

            // Show tutor-specific fields
        } else if ("Tutor".equalsIgnoreCase(currentRequest.getRole())) {
            String degree = currentRequest.getHighestDegree();
            List<String> courses = currentRequest.getCoursesOffered();

            // Display degree if available
            if (degree != null && !degree.trim().isEmpty()) {
                holder.degree.setText("Degree: " + degree);
                holder.degree.setVisibility(View.VISIBLE);
            } else {
                holder.degree.setVisibility(View.GONE);
            }

            // Display courses if available
            if (courses != null && !courses.isEmpty()) {
                // Build comma-separated courses string
                StringBuilder coursesString = new StringBuilder();
                for (int i = 0; i < courses.size(); i++) {
                    coursesString.append(courses.get(i));
                    if (i < courses.size() - 1) {
                        coursesString.append(", ");
                    }
                }
                holder.courses.setText("Courses: " + coursesString.toString());
                holder.courses.setVisibility(View.VISIBLE);
            } else {
                holder.courses.setVisibility(View.GONE);
            }
            // Hide student fields
            holder.program.setVisibility(View.GONE);
        } else {
            // Unknown role - hide all role-specific fields
            holder.program.setVisibility(View.GONE);
            holder.degree.setVisibility(View.GONE);
            holder.courses.setVisibility(View.GONE);
        }

        // Set up approve button click listener
        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListener.onApprove(currentRequest);
            }
        });

        // Show reject button only for pending requests
        if (isPending) {
            holder.rejectButton.setVisibility(View.VISIBLE);
            holder.rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myListener.onReject(currentRequest);
                }
            });
        } else {
            holder.rejectButton.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return requestList.size();
    }

    /**
     * ViewHolder class that holds references to all views in a row.
     */
    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView name, emailPhone, date, program, degree, courses;
        Button approveButton, rejectButton;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find and store references to all views
            name = itemView.findViewById(R.id.tvNameRole);
            emailPhone = itemView.findViewById(R.id.tvEmailPhone);
            date = itemView.findViewById(R.id.tvSubmittedAt);
            program = itemView.findViewById(R.id.tvProgram);
            degree = itemView.findViewById(R.id.tvDegree);
            courses = itemView.findViewById(R.id.tvCourses);
            approveButton = itemView.findViewById(R.id.btnApprove);
            rejectButton = itemView.findViewById(R.id.btnReject);
        }
    }
}