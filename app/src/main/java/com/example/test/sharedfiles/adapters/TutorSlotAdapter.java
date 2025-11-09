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
                .inflate(R.layout.item_slot_REPLACE_ME, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Slot currentSlot = slotList.get(position);

        String dateTime = currentSlot.getDate() + " " +
                currentSlot.getStartTime() + " - " +
                currentSlot.getEndTime();

        holder.tvSlotTime_REPLACE_WITH_XML_ID.setText(dateTime);

        holder.btnDeleteSlot_REPLACE_WITH_XML_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        TextView tvSlotTime_REPLACE_WITH_XML_ID;
        Button btnDeleteSlot_REPLACE_WITH_XML_ID;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSlotTime_REPLACE_WITH_XML_ID = itemView.findViewById(R.id.tvSlotTime_REPLACE_WITH_XML_ID);
            btnDeleteSlot_REPLACE_WITH_XML_ID = itemView.findViewById(R.id.btnDeleteSlot_REPLACE_WITH_XML_ID);
        }
    }
}