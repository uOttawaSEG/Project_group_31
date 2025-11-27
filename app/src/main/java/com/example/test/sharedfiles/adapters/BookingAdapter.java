package com.example.test.sharedfiles.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;
import android.widget.Toast;


import com.example.test.R;
import com.example.test.sharedfiles.model.StudentBooking;
import java.text.ParseException;
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

        String tutorText = booking.getTutorName() +
                " (" + String.format("%.1f", booking.getTutorRating()) + "⭐)";
        holder.tvTutor.setText("Tutor: " + tutorText);

        String fullTime = booking.getDate() + "  " +
                booking.getStartTime() + " - " + booking.getEndTime();

        holder.tvTime.setText(fullTime);
        holder.tvStatus.setText("Status: " + booking.getStatus());

        if ("Approved".equalsIgnoreCase(booking.getStatus())) {
            holder.btnExport.setVisibility(View.VISIBLE);
        } else {
            holder.btnExport.setVisibility(View.GONE);
        }

        holder.btnExport.setOnClickListener(v -> {
            exportToCalendar(v.getContext(), booking);
        });


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

    // this method open google calendar
    private void exportToCalendar(Context context, StudentBooking booking) {
        try {
            // show date and start time from booking
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date startDate = sdf.parse(booking.getDate() + " " + booking.getStartTime());
            Date endDate   = sdf.parse(booking.getDate() + " " + booking.getEndTime());

            long startMillis = startDate.getTime();
            long endMillis   = endDate.getTime();

            // show information of tutorial session and the tutor
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE,
                            "Tutorial session: " + booking.getCourseCode())
                    .putExtra(CalendarContract.Events.DESCRIPTION,
                            "OTAMS tutorial session with tutor: "
                                    + booking.getTutorName()
                                    + " (" + String.format("%.1f", booking.getTutorRating()) + "⭐)")
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis);

            context.startActivity(intent);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(context, "Can not export to Calendar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourse, tvTutor, tvTime, tvStatus;
        Button btnCancel;
        Button btnExport;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCourse = itemView.findViewById(R.id.tvBookingCourse);
            tvTutor = itemView.findViewById(R.id.tvBookingTutor);
            tvTime = itemView.findViewById(R.id.tvBookingTime);
            tvStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelBooking);
            btnExport = itemView.findViewById(R.id.btnExport);
        }
    }
}