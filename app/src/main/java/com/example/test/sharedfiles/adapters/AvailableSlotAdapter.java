package com.example.test.sharedfiles.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test.R;
import com.example.test.sharedfiles.model.Slot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AvailableSlotAdapter extends RecyclerView.Adapter<AvailableSlotAdapter.ViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Slot slot);
    }

    private List<Slot> slotList;
    private OnBookClickListener listener;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public AvailableSlotAdapter(List<Slot> slotList, OnBookClickListener listener) {
        this.slotList = slotList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_slot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Slot slot = slotList.get(position);


        holder.tvTutorName.setText("Tutor: " + slot.getTutorId());

        String timeInfo = dateTimeFormat.format(new Date(slot.getStartTime())) + " - " + dateTimeFormat.format(new Date(slot.getEndTime()));
        holder.tvDateTime.setText(timeInfo);

        holder.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBookClick(slot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTutorName, tvDateTime;
        Button btnBook;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTutorName = itemView.findViewById(R.id.tvSlotTutor);
            tvDateTime = itemView.findViewById(R.id.tvSlotTime);
            btnBook = itemView.findViewById(R.id.btnBookSlot);
        }
    }
}