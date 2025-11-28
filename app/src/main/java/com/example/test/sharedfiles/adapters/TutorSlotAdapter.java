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

import java.util.ArrayList;
import java.util.List;

public class TutorSlotAdapter extends RecyclerView.Adapter<TutorSlotAdapter.SlotViewHolder> {

    private final List<Slot> slotList = new ArrayList<>();
    private final OnSlotDeleteListener deleteListener;

    public interface OnSlotDeleteListener {
        void onSlotDelete(Slot slot);
    }

    public TutorSlotAdapter(OnSlotDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Slot currentSlot = slotList.get(position);

        String dateTime = currentSlot.getDate() + " " +
                currentSlot.getStartTime() + " - " +
                currentSlot.getEndTime();

        holder.tvSlotTime.setText(dateTime);

        if (currentSlot.getIsBooked()) {
            holder.btnDeleteSlot.setEnabled(false);
            holder.btnDeleteSlot.setAlpha(0.4f);
        } else {
            holder.btnDeleteSlot.setEnabled(true);
            holder.btnDeleteSlot.setAlpha(1f);
        }

        holder.btnDeleteSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSlot.getIsBooked()) {
                    return;
                }

                if (deleteListener != null) {
                    deleteListener.onSlotDelete(currentSlot);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public void setSlots(List<Slot> slots) {
        this.slotList.clear();
        if (slots != null) {
            this.slotList.addAll(slots);
        }
        notifyDataSetChanged();
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {

        TextView tvSlotTime;
        Button btnDeleteSlot;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSlotTime = itemView.findViewById(R.id.tvSlotTime);
            btnDeleteSlot = itemView.findViewById(R.id.btnDeleteSlot);
        }
    }
}