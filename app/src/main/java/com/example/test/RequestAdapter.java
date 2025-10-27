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

class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.VH> {
    interface Listener {
        void onApprove(RegistrationRequest r);
        void onReject(RegistrationRequest r);
    }

    enum Mode { PENDING, REJECTED }

    private final List<RegistrationRequest> data = new ArrayList<>();
    private final Listener listener;
    private final Mode mode;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    RequestAdapter(Mode mode, Listener listener) {
        this.mode = mode;
        this.listener = listener;
    }

    void submit(List<RegistrationRequest> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RegistrationRequest r = data.get(pos);

        h.nameRole.setText(r.getFirstName() + " " + r.getLastName() + " — " + r.getRole());
        h.emailPhone.setText(r.getEmail() + " • " + r.getPhone());
        h.submittedAt.setText("Submitted: " + sdf.format(new Date(r.getSubmittedAt())));

        if ("Student".equalsIgnoreCase(r.getRole())) {
            String program = r.getProgramOfStudy();
            if (program != null && !program.trim().isEmpty()) {
                h.program.setText("Program: " + program);
                h.program.setVisibility(View.VISIBLE);
            } else h.program.setVisibility(View.GONE);

            h.degree.setVisibility(View.GONE);
            h.courses.setVisibility(View.GONE);
        } else if ("Tutor".equalsIgnoreCase(r.getRole())) {
            String degree = r.getHighestDegree();
            List<String> courses = r.getCoursesOffered();

            if (degree != null && !degree.trim().isEmpty()) {
                h.degree.setText("Degree: " + degree);
                h.degree.setVisibility(View.VISIBLE);
            } else h.degree.setVisibility(View.GONE);

            if (courses != null && !courses.isEmpty()) {
                String joined = android.text.TextUtils.join(", ", courses);
                h.courses.setText("Courses: " + joined);
                h.courses.setVisibility(View.VISIBLE);
            } else h.courses.setVisibility(View.GONE);

            h.program.setVisibility(View.GONE);
        } else {
            h.program.setVisibility(View.GONE);
            h.degree.setVisibility(View.GONE);
            h.courses.setVisibility(View.GONE);
        }

        h.btnApprove.setOnClickListener(v -> listener.onApprove(r));
        if (mode == Mode.PENDING) {
            h.btnReject.setVisibility(View.VISIBLE);
            h.btnReject.setOnClickListener(v -> listener.onReject(r));
        } else {
            h.btnReject.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView nameRole, emailPhone, submittedAt, program, degree, courses;
        Button btnApprove, btnReject;
        VH(@NonNull View itemView) {
            super(itemView);
            nameRole   = itemView.findViewById(R.id.tvNameRole);
            emailPhone = itemView.findViewById(R.id.tvEmailPhone);
            submittedAt= itemView.findViewById(R.id.tvSubmittedAt);
            program    = itemView.findViewById(R.id.tvProgram);
            degree     = itemView.findViewById(R.id.tvDegree);
            courses    = itemView.findViewById(R.id.tvCourses);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject  = itemView.findViewById(R.id.btnReject);
        }
    }
}
