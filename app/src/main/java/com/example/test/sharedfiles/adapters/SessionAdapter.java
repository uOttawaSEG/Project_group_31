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

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    private final List<Session> sessionList = new ArrayList<>();
    private final OnSessionCancelListener cancelListener;
    private final boolean showCancelButton;

    private Map<String, String> studentNameMap;

    // Listener interface for cancel button
    public interface OnSessionCancelListener {
        void onSessionCancel(Session session);
    }

    public SessionAdapter(OnSessionCancelListener listener, boolean showCancelButton) {
        this.cancelListener = listener;
        this.showCancelButton = showCancelButton;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session currentSession = sessionList.get(position);

        String studentName = "Unknown Student";
        if (studentNameMap != null && currentSession.getStudentId() != null) {
            String name = studentNameMap.get(currentSession.getStudentId());
            if (name != null) {
                studentName = name;
            }
        }
        holder.tvStudentName.setText("Student: " + studentName);

        String sessionTime = "Date: " + currentSession.getDate()
                + " | " + currentSession.getStartTime() + " - " + currentSession.getEndTime();
        holder.tvSessionTime.setText(sessionTime);

        holder.tvSessionStatus.setText("Status: " + currentSession.getStatus());

        if (showCancelButton) {
            holder.btnCancelSession.setVisibility(View.VISIBLE);
            holder.btnCancelSession.setOnClickListener(v -> {
                if (cancelListener != null) {
                    cancelListener.onSessionCancel(currentSession);
                }
            });
        } else {
            holder.btnCancelSession.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public void setSessions(List<Session> sessions, Map<String, String> studentNames) {
        this.sessionList.clear();
        if (sessions != null) {
            this.sessionList.addAll(sessions);
        }
        this.studentNameMap = studentNames;
        notifyDataSetChanged();
    }


    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;
        TextView tvSessionTime;
        TextView tvSessionStatus;
        Button btnCancelSession;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvSessionTime = itemView.findViewById(R.id.tvSessionTime);
            tvSessionStatus = itemView.findViewById(R.id.tvSessionStatus);
            btnCancelSession = itemView.findViewById(R.id.btnCancelSession);
        }
    }
}
