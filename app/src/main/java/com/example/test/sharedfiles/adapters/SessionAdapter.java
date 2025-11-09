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
    private Map<String, String> slotTimeMap;

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
                .inflate(R.layout.item_session_REPLACE_ME, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session currentSession = sessionList.get(position);

        String studentName = "Loading...";
        if (studentNameMap != null) {
            studentName = studentNameMap.get(currentSession.getStudentId());
        }
        if (studentName == null) {
            studentName = "Student ID: " + currentSession.getStudentId();
        }
        holder.tvStudentName_REPLACE_WITH_XML_ID.setText("Student: " + studentName);

        String sessionTime = "Loading...";
        if (slotTimeMap != null) {
            sessionTime = slotTimeMap.get(currentSession.getSlotId());
        }
        if (sessionTime == null) {
            sessionTime = "Slot ID: " + currentSession.getSlotId();
        }
        holder.tvSessionTime_REPLACE_WITH_XML_ID.setText("Time: " + sessionTime);

        holder.tvSessionStatus_REPLACE_WITH_XML_ID.setText("Status: " + currentSession.getStatus());

        if (showCancelButton) {
            holder.btnCancelSession_REPLACE_WITH_XML_ID.setVisibility(View.VISIBLE);
            holder.btnCancelSession_REPLACE_WITH_XML_ID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cancelListener != null) {
                        cancelListener.onSessionCancel(currentSession);
                    }
                }
            });
        } else {
            holder.btnCancelSession_REPLACE_WITH_XML_ID.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public void setSessions(List<Session> sessions, Map<String, String> studentNames, Map<String, String> slotTimes) {
        this.sessionList.clear();
        if (sessions != null) {
            this.sessionList.addAll(sessions);
        }
        this.studentNameMap = studentNames;
        this.slotTimeMap = slotTimes;
        notifyDataSetChanged();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {

        TextView tvStudentName_REPLACE_WITH_XML_ID;
        TextView tvSessionTime_REPLACE_WITH_XML_ID;
        TextView tvSessionStatus_REPLACE_WITH_XML_ID;
        Button btnCancelSession_REPLACE_WITH_XML_ID;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);

            tvStudentName_REPLACE_WITH_XML_ID = itemView.findViewById(R.id.tvStudentName_REPLACE_WITH_XML_ID);
            tvSessionTime_REPLACE_WITH_XML_ID = itemView.findViewById(R.id.tvSessionTime_REPLACE_WITH_XML_ID);
            tvSessionStatus_REPLACE_WITH_XML_ID = itemView.findViewById(R.id.tvSessionStatus_REPLACE_WITH_XML_ID);
            btnCancelSession_REPLACE_WITH_XML_ID = itemView.findViewById(R.id.btnCancelSession_REPLACE_WITH_XML_ID);
        }
    }
}