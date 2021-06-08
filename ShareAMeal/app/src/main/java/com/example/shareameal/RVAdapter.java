package com.example.shareameal;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    ArrayList<User> list = new ArrayList<>();

    public RVAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setItems(ArrayList<User> users) {
        list.addAll(users);
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);
        return new DonorVH(view);
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
}
