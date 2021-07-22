package com.example.shareameal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdminViewReportsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Report> reportList = new ArrayList<>();

    public AdminViewReportsAdapter(Context ctx, ArrayList<Report> reportList) {
        this.context = ctx;
        this.reportList = reportList;
    }

    public class ReportVH extends RecyclerView.ViewHolder {
        public TextView txt_recipient, txt_report_time;
        public CardView cardView;

        public ReportVH(@NonNull @NotNull View itemView) {
            super(itemView);
            txt_recipient = itemView.findViewById(R.id.txt_recipient);
            txt_report_time = itemView.findViewById(R.id.txt_report_time);
            cardView = itemView.findViewById(R.id.cvReport);
        }
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_report_item, parent, false);
        return new AdminViewReportsAdapter.ReportVH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ReportVH vh = (ReportVH) holder;
        Report report = reportList.get(position);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        String recipientId = report.getRecipientId();
        usersRef.child(recipientId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
                } else {
                    User recipient = task.getResult().getValue(User.class);
                    vh.txt_recipient.setText("Reported By: " + recipient.getName());
                }
            }
        });

        vh.txt_report_time.setText("At " + report.getReportTime());

        ((ReportVH) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReviewReport.class);
                intent.putExtra("donorId", report.getDonorId());
                intent.putExtra("recipientId", report.getRecipientId());
                intent.putExtra("slotId", report.getSlotId());
                intent.putExtra("reportId", report.getReportId());
                intent.putExtra("foodId", report.getFoodId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
}

