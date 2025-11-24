package com.example.test.sharedfiles.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test.R;
import com.example.test.sharedfiles.model.StudentBooking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PastSessionAdapter extends RecyclerView.Adapter<PastSessionAdapter.ViewHolder> {

    public interface OnRateClickListener {
        void onRateClick(StudentBooking booking);
    }

    private List<StudentBooking> sessionList;
    private OnRateClickListener listener;

    private final SimpleDateFormat dateTimeParser =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    private final SimpleDateFormat outputFormat =
            new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.getDefault());

    public PastSessionAdapter(List<StudentBooking> sessionList, OnRateClickListener listener) {
        this.sessionList = sessionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_past_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final StudentBooking booking = sessionList.get(position);

        holder.tvCourse.setText("Course: " + booking.getCourseCode());

        String tutorInfo = "Tutor: " + booking.getTutorName() +
                " (" + String.format(Locale.getDefault(), "%.1f", booking.getTutorRating()) + "⭐)";
        holder.tvTutor.setText(tutorInfo);

        try {
            Date date = dateTimeParser.parse(
                    booking.getDate() + " " + booking.getStartTime()
            );
            holder.tvTime.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvTime.setText(booking.getDate() + " " + booking.getStartTime());
        }
        boolean alreadyRated = booking.isAlreadyRated();
        holder.btnRate.setEnabled(!alreadyRated);
        holder.btnRate.setAlpha(alreadyRated ? 0.4f : 1f);

        holder.btnRate.setOnClickListener(v -> listener.onRateClick(booking));
    }


    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourse, tvTutor, tvTime;
        Button btnRate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCourse = itemView.findViewById(R.id.tvPastCourse);
            tvTutor = itemView.findViewById(R.id.tvPastTutor);
            tvTime = itemView.findViewById(R.id.tvPastTime);
            btnRate = itemView.findViewById(R.id.btnRateTutor);
        }
    }
}
