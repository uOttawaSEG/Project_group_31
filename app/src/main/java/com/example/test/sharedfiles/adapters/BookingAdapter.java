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

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    public interface OnCancelClickListener {
        void onCancelClick(StudentBooking booking);
    }

    private List<StudentBooking> bookingList;
    private OnCancelClickListener listener;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public BookingAdapter(List<StudentBooking> bookingList, OnCancelClickListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final StudentBooking booking = bookingList.get(position);

        holder.tvCourse.setText("Course: " + booking.getCourseCode());
        holder.tvTutor.setText("Tutor: " + booking.getTutorName());
        holder.tvStatus.setText("Status: " + booking.getStatus());

        holder.tvTime.setText(dateTimeFormat.format(new Date(booking.getStartTime())));

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus()) || "REJECTED".equalsIgnoreCase(booking.getStatus())) {
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancelClick(booking);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourse, tvTutor, tvTime, tvStatus;
        Button btnCancel;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCourse = itemView.findViewById(R.id.tvBookingCourse);
            tvTutor = itemView.findViewById(R.id.tvBookingTutor);
            tvTime = itemView.findViewById(R.id.tvBookingTime);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
        }
    }
}