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

class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    interface RequestAdapterListener {
        void onApprove(RegistrationRequest r);
        void onReject(RegistrationRequest r);
    }

    private List<RegistrationRequest> requestList = new ArrayList<>();
    private final RequestAdapterListener myListener;
    private final boolean isPending;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    RequestAdapter(boolean isPending, RequestAdapterListener listener) {
        this.isPending = isPending;
        this.myListener = listener;
    }

    void setRequests(List<RegistrationRequest> items) {
        requestList.clear();
        if (items != null) {
            requestList.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RegistrationRequest currentRequest = requestList.get(position);

        holder.name.setText(currentRequest.getFirstName() + " " + currentRequest.getLastName() + " — " + currentRequest.getRole());
        holder.emailPhone.setText(currentRequest.getEmail() + " • " + currentRequest.getPhone());
        holder.date.setText("Submitted: " + simpleDateFormat.format(new Date(currentRequest.getSubmittedAt())));

        if ("Student".equalsIgnoreCase(currentRequest.getRole())) {
            String program = currentRequest.getProgramOfStudy();
            if (program != null && !program.trim().isEmpty()) {
                holder.program.setText("Program: " + program);
                holder.program.setVisibility(View.VISIBLE);
            } else {
                holder.program.setVisibility(View.GONE);
            }
            holder.degree.setVisibility(View.GONE);
            holder.courses.setVisibility(View.GONE);

        } else if ("Tutor".equalsIgnoreCase(currentRequest.getRole())) {
            String degree = currentRequest.getHighestDegree();
            List<String> courses = currentRequest.getCoursesOffered();

            if (degree != null && !degree.trim().isEmpty()) {
                holder.degree.setText("Degree: " + degree);
                holder.degree.setVisibility(View.VISIBLE);
            } else {
                holder.degree.setVisibility(View.GONE);
            }

            if (courses != null && !courses.isEmpty()) {
                // Manually build the string
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
            holder.program.setVisibility(View.GONE);
        } else {
            holder.program.setVisibility(View.GONE);
            holder.degree.setVisibility(View.GONE);
            holder.courses.setVisibility(View.GONE);
        }

        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListener.onApprove(currentRequest);
            }
        });

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

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView name, emailPhone, date, program, degree, courses;
        Button approveButton, rejectButton;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
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
