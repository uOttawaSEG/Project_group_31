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
    private Map<String, String> studentNames; // studentId -> full name
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
                .inflate(R.layout.item_session_request, parent, false); // ✅ correct layout
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Session s = requests.get(position);

        String name = (studentNames != null) ? studentNames.get(s.getStudentId()) : null;
        if (name == null) name = "Student ID: " + s.getStudentId();
        holder.tvStudentName.setText("Student: " + name);

        String timeText = "Requested Time: " + s.getDate() + "  " + s.getStartTime() + " - " + s.getEndTime();
        holder.tvTimeRequested.setText(timeText);

        holder.btnAcceptRequest.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(s);
        });

        holder.btnRejectRequest.setOnClickListener(v -> {
            if (listener != null) listener.onReject(s);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        TextView tvTimeRequested;
        Button btnAcceptRequest;
        Button btnRejectRequest;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvTimeRequested = itemView.findViewById(R.id.tvTimeRequested);
            btnAcceptRequest = itemView.findViewById(R.id.btnAcceptRequest);
            btnRejectRequest = itemView.findViewById(R.id.btnRejectRequest);
        }
    }
}
