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
import java.util.List;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AvailableSlotAdapter extends RecyclerView.Adapter<AvailableSlotAdapter.ViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Slot slot);
    }

    private List<Slot> slotList;
    private OnBookClickListener listener;

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
        DatabaseReference tutorRef = FirebaseDatabase.getInstance()
                .getReference("tutors")
                .child(slot.getTutorId());

        tutorRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String first = snapshot.child("firstName").getValue(String.class);
                String last  = snapshot.child("lastName").getValue(String.class);
                Double rating = snapshot.child("averageRating").getValue(Double.class);
                if (first == null) first = "";
                if (last == null)  last = "";
                if (rating == null) rating = 0.0;

                holder.tvTutorName.setText(
                        "Tutor: " + first + " " + last + " (" + rating + "⭐)"
                );

            } else {
                holder.tvTutorName.setText("Tutor: Unknown (0.0⭐)");
            }
        });

        String timeInfo = slot.getDate() + "  " + slot.getStartTime() + " - " + slot.getEndTime();
        holder.tvDateTime.setText(timeInfo);

        holder.btnBook.setOnClickListener(v -> listener.onBookClick(slot));
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