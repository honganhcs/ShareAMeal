package com.example.shareameal;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVSlotsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Slot> list = new ArrayList<>();
    private OnSlotClickListener onSlotClickListener;

    public RVSlotsAdapter(Context ctx, OnSlotClickListener onSlotClickListener) {
        this.context = ctx;
        this.onSlotClickListener = onSlotClickListener;
    }

    public void setItems(ArrayList<Slot> slots) {
        list.addAll(slots);
    }

    public class SlotVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txt_date, txt_time, txt_availability;
        public OnSlotClickListener onSlotClickListener;

        public SlotVH(@NonNull @NotNull View itemView, OnSlotClickListener onSlotClickListener) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);
            txt_availability = itemView.findViewById(R.id.txt_availability);

            this.onSlotClickListener = onSlotClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSlotClickListener.onSlotClick(getBindingAdapterPosition());
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_slot_item, parent, false);
        return new SlotVH(view, onSlotClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        SlotVH vh = (SlotVH) holder;

        Slot slot = list.get(position);
        vh.txt_date.setText(slot.getDate());
        vh.txt_time.setText(slot.getStartTime() + " to " + slot.getEndTime());
        if (slot.getNumRecipients() == 0) {
            vh.txt_availability.setText("Not Reserved");
        } else if (slot.getNumRecipients() == 3){
            vh.txt_availability.setText("Fully Reserved");
        } else if (slot.getNumRecipients() == 1){
            vh.txt_availability.setText("2 More Reservations Allowed");
        } else {
            vh.txt_availability.setText("1 More Reservation Allowed");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnSlotClickListener {
        void onSlotClick(int position);
    }
}
