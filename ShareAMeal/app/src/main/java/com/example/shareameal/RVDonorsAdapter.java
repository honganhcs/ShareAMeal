package com.example.shareameal;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class RVDonorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<User> list = new ArrayList<>();
    private OnDonorClickListener onDonorClickListener;

    public RVDonorsAdapter(Context ctx, OnDonorClickListener onDonorClickListener) {
        this.context = ctx;
        this.onDonorClickListener = onDonorClickListener;
    }

    public void setItems(ArrayList<User> users) {
        list.addAll(users);
    }

    public class DonorVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txt_donor, txt_donor_address;
        public OnDonorClickListener onDonorClickListener;

        public DonorVH(@NonNull @NotNull View itemView, OnDonorClickListener onDonorClickListener) {
            super(itemView);
            txt_donor = itemView.findViewById(R.id.txt_donor);
            txt_donor_address = itemView.findViewById(R.id.txt_donor_address);
            this.onDonorClickListener = onDonorClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onDonorClickListener.onDonorClick(getBindingAdapterPosition());
        }
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rvdonor_item, parent, false);
        return new DonorVH(view, onDonorClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull RecyclerView.ViewHolder holder, int position) {
        DonorVH vh = (DonorVH) holder;
        // probably change to donor later
        User donor = list.get(position);
        if(TextUtils.isEmpty(donor.getRestaurant())) {
            vh.txt_donor.setText(donor.getName());
        } else {
            vh.txt_donor.setText(donor.getRestaurant());
        }
        vh.txt_donor_address.setText(donor.getAddress());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnDonorClickListener {
        void onDonorClick(int position);
    }
}
