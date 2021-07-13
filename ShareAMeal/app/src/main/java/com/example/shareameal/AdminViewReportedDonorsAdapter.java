package com.example.shareameal;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdminViewReportedDonorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<String> donorIdList = new ArrayList<>();

    public AdminViewReportedDonorsAdapter(Context ctx, ArrayList<String> donorIdList) {
        this.context = ctx;
        this.donorIdList = donorIdList;
    }

    public class ReportedDonorsVH extends RecyclerView.ViewHolder {
        public TextView txt_donor, txt_donor_reportsNum;
        public CardView cardView;

        public ReportedDonorsVH(@NonNull @NotNull View itemView) {
            super(itemView);
            txt_donor = itemView.findViewById(R.id.txt_donor);
            txt_donor_reportsNum = itemView.findViewById(R.id.txt_donor_reportsNum);
            cardView = itemView.findViewById(R.id.cvReportedDonor);
        }
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_reporteddonor_item, parent, false);
        return new AdminViewReportedDonorsAdapter.ReportedDonorsVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReportedDonorsVH vh = (ReportedDonorsVH) holder;
        String donorId = donorIdList.get(position);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.child(donorId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
                } else {
                    User donor = task.getResult().getValue(User.class);
                    if (TextUtils.isEmpty(donor.getRestaurant())) {
                        vh.txt_donor.setText(donor.getName());
                    } else {
                        vh.txt_donor.setText(donor.getRestaurant());
                    }
                }
            }
        });

        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Reports");
        reportsRef.child(donorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 1) {
                    vh.txt_donor_reportsNum.setText(String.valueOf(snapshot.getChildrenCount()) + " Unreviewed Report");
                } else {
                    vh.txt_donor_reportsNum.setText(String.valueOf(snapshot.getChildrenCount()) + " Unreviewed Reports");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });

        ((ReportedDonorsVH) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String donorId = donorIdList.get(position);
                Intent intent = new Intent(v.getContext(), AdminViewReports.class);
                intent.putExtra("donorId", donorId);

                Intent adapterIntent = new Intent(v.getContext(), AdminViewReportsAdapter.class);
                adapterIntent.putExtra("donorId", donorId);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return donorIdList.size();
    }
}
