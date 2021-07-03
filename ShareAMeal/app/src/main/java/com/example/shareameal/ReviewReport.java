package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ReviewReport extends AppCompatActivity {
    private ImageView foodImage, reportImage;
    private TextView foodNameTxt, foodDescriptionTxt, txtOrderQuantity, txtSchedule, reportContentTxt;
    private String donorId, recipientId, slotId, reportId;
    private AppCompatButton rejectReportBtn, acceptReportBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_report);

        Intent intent = getIntent();
        donorId = intent.getStringExtra("donorId");
        recipientId = intent.getStringExtra("recipientId");
        slotId = intent.getStringExtra("slotId");
        reportId = intent.getStringExtra("reportId");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Review Report");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        foodImage = findViewById(R.id.foodImage);
        foodNameTxt = findViewById(R.id.foodNameTxt);
        foodDescriptionTxt = findViewById(R.id.foodDescriptionTxt);
        txtOrderQuantity = findViewById(R.id.txtOrderQuantity);
        txtSchedule = findViewById(R.id.txtSchedule);
        reportImage = findViewById(R.id.reportImage);
        reportContentTxt = findViewById(R.id.reportContent);

        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Reports");
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference foodsRef = FirebaseDatabase.getInstance().getReference("Foods");

        ordersRef.child(recipientId).child(slotId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Order order = snapshot.getValue(Order.class);
                if (order.getFoodImageURL() == null) {
                    foodImage.setImageResource(R.drawable.dish128);
                } else {
                    if (order.getFoodImageURL().equals("null")) {
                        foodImage.setImageResource(R.drawable.dish128);
                    } else {
                        Picasso.get().load(order.getFoodImageURL()).into(foodImage);
                    }
                }
                foodNameTxt.setText(order.getFoodName());
                String foodId = order.getFoodId();

                foodsRef.child(donorId).child(foodId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Food food = snapshot.getValue(Food.class);
                        foodDescriptionTxt.setText(food.getDescription());
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                });

                txtOrderQuantity.setText("Collected quantity:\n" + String.valueOf(order.getQuantity()));
                txtSchedule.setText("Scheduled for collection at:\n" + order.getDate() + ", " + order.getStartTime() + " - " + order.getEndTime());

                reportsRef.child(donorId).child(reportId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Firebase", "Error getting data", task.getException());
                        } else {
                            Report report = task.getResult().getValue(Report.class);

                            if (report.getReportImageUrl() == null) {
                                reportImage.setImageResource(R.drawable.ic_baseline_insert_photo_128);
                            } else {
                                if (report.getReportImageUrl().equals("null")) {
                                    reportImage.setImageResource(R.drawable.ic_baseline_insert_photo_128);
                                } else {
                                    Picasso.get().load(report.getReportImageUrl()).into(reportImage);
                                }
                            }

                            reportContentTxt.setText("Report Description: " + report.getReportContent());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });

        rejectReportBtn = findViewById(R.id.rejectReportBtn);
        acceptReportBtn = findViewById(R.id.acceptReportBtn);

        rejectReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference currReportRef = reportsRef.child(donorId).child(reportId);
                currReportRef.removeValue();
                Intent intent = new Intent(ReviewReport.this, AdminViewReportedDonors.class);
                startActivity(intent);
                finish();
            }
        });

        acceptReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference currReportRef = reportsRef.child(donorId).child(reportId);
                currReportRef.removeValue();

                usersRef.child(donorId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Firebase", "Error getting data", task.getException());
                        } else {
                            User user = task.getResult().getValue(User.class);
                            int numOfReports = user.getNumberOfReports();
                            numOfReports += 1;
                            usersRef.child(donorId).child("numberOfReports").setValue(numOfReports);
                        }
                    }
                });

                Intent intent = new Intent(ReviewReport.this, AdminViewReportedDonors.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReviewReport.this, AdminViewReports.class);
        intent.putExtra("donorId", donorId);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ReviewReport.this, AdminViewReports.class);
        intent.putExtra("donorId", donorId);
        startActivity(intent);
        finish();
        return true;
    }
}