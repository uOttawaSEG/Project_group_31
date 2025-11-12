package com.example.test.sharedfiles.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.sharedfiles.model.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PendingSessionRequestAdapter
        extends RecyclerView.Adapter<PendingSessionRequestAdapter.RequestViewHolder> {

    public interface OnRequestDecisionListener {
        void onApprove(Session session);
        void onReject(Session session);
    }

    private final List<Session> requests = new ArrayList<>();
    private Map<String, String> studentNames;
    private final OnRequestDecisionListener listener;

    public PendingSessionRequestAdapter(OnRequestDecisionListener listener) {
        this.listener = listener;
    }

    public void setSessions(List<Session> sessions, Map<String, String> studentNameMap) {
        requests.clear();
        if (sessions != null) requests.addAll(sessions);
        this.studentNames = studentNameMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session_request, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Session session = requests.get(position);

        String studentName = (studentNames != null) ? studentNames.get(session.getStudentId()) : null;
        if (studentName == null) studentName = "Student ID: " + session.getStudentId();

        holder.tvStudentName.setText("Student: " + studentName);

        if (session.getStudentEmail() != null) {
            holder.tvStudentEmail.setText("Email: " + session.getStudentEmail());
            holder.tvStudentEmail.setVisibility(View.VISIBLE);
        } else {
            holder.tvStudentEmail.setVisibility(View.GONE);
        }

        if (session.getCourseName() != null) {
            holder.tvCourseName.setText("Course: " + session.getCourseName());
            holder.tvCourseName.setVisibility(View.VISIBLE);
        } else {
            holder.tvCourseName.setVisibility(View.GONE);
        }

        String timeText = "Requested Time: " + session.getDate() + "  " +
                session.getStartTime() + " - " + session.getEndTime();
        holder.tvRequestTime.setText(timeText);

        holder.btnAcceptRequest.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(session);
        });

        holder.btnRejectRequest.setOnClickListener(v -> {
            if (listener != null) listener.onReject(session);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        TextView tvStudentEmail;
        TextView tvCourseName;
        TextView tvRequestTime;
        Button btnAcceptRequest;
        Button btnRejectRequest;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentEmail = itemView.findViewById(R.id.tvStudentEmail);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvRequestTime = itemView.findViewById(R.id.tvRequestTime);
            btnAcceptRequest = itemView.findViewById(R.id.btnApprove);
            btnRejectRequest = itemView.findViewById(R.id.btnReject);
        }
    }
}
