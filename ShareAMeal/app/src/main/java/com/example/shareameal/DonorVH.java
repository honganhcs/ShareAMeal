package com.example.shareameal;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class DonorVH extends RecyclerView.ViewHolder {

    public TextView txt_donor, txt_donor_address;
    public DonorVH(@NonNull @NotNull View itemView) {
        super(itemView);
        txt_donor = itemView.findViewById(R.id.txt_donor);
        txt_donor_address = itemView.findViewById(R.id.txt_donor_address);
    }
}
