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

public class PendingSessionRequestAdapter extends RecyclerView.Adapter<PendingSessionRequestAdapter.PendingSessionRequestViewHolder> {


    public interface Listener {
        void onApprove(Session s);
        void onReject(Session s);
    }

    private final List<Session> items = new ArrayList<>();
    private Map<String, String> studentNames;
    private Map<String, String> slotTimes;
    private final Listener listener;

    public PendingSessionRequestAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setSessions(List<Session> sessions,
                            Map<String, String> studentNameMap,
                            Map<String, String> slotTimeMap) {
        items.clear();
        if (sessions != null) items.addAll(sessions);
        this.studentNames = studentNameMap;
        this.slotTimes = slotTimeMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PendingSessionRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pending_session_REPLACE_ME, parent, false);
        return new PendingSessionRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingSessionRequestViewHolder h, int position) {
        Session s = items.get(position);

        String name = (studentNames != null) ? studentNames.get(s.getStudentId()) : null;
        if (name == null) name = "Student ID: " + s.getStudentId();
        h.nameTxt.setText("Student: " + name);

        String time = (slotTimes != null) ? slotTimes.get(s.getSlotId()) : null;
        if (time == null) time = "Slot ID: " + s.getSlotId();
        h.timeTxt.setText("Time: " + time);

        h.statusTxt.setText("Status: " + s.getStatus());

        h.approveBtn.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(s);
        });

        h.rejectBtn.setOnClickListener(v -> {
            if (listener != null) listener.onReject(s);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PendingSessionRequestViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        TextView timeTxt;
        TextView statusTxt;
        Button approveBtn;
        Button rejectBtn;

        PendingSessionRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt   = itemView.findViewById(R.id.nameTxt);
            timeTxt   = itemView.findViewById(R.id.timeTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
            approveBtn = itemView.findViewById(R.id.approveBtn);
            rejectBtn  = itemView.findViewById(R.id.rejectBtn);
        }
    }
}


