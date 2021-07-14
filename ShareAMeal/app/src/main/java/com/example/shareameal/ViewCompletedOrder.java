package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ViewCompletedOrder extends AppCompatActivity {

    private ImageView foodImage;
    private TextView foodNameTxt, foodDescriptionTxt, txtOrderQuantity, txtSchedule, txtAddress, txtDonorHeader, txtDonor;
    private DatabaseReference reference1, reference2, reference3;
    private AppCompatButton btnReport;
    private String userId, donorId, foodId, slotId, recipientId;
    private Order order;
    private Food food;
    private User donor;
    private int orderQuantity;
    private ConstraintLayout bufferLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_completed_order);

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("View completed order");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        foodImage = findViewById(R.id.foodImage);
        foodNameTxt = findViewById(R.id.foodNameTxt);
        foodDescriptionTxt = findViewById(R.id.foodDescriptionTxt);
        txtOrderQuantity = findViewById(R.id.txtOrderQuantity);
        txtSchedule = findViewById(R.id.txtSchedule);
        txtAddress = findViewById(R.id.txtAddress);
        txtDonorHeader = findViewById(R.id.txtDonorHeader);
        txtDonor = findViewById(R.id.txtDonor);
        bufferLayout = findViewById(R.id.layout1);
        btnReport = findViewById(R.id.btnReport);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        Intent intent = getIntent();

        recipientId = intent.getStringExtra("recipientId");
        donorId = intent.getStringExtra("donorId");
        foodId = intent.getStringExtra("foodId");
        slotId = intent.getStringExtra("slotId");

        reference1 = FirebaseDatabase.getInstance().getReference("Foods").child(donorId);
        reference1.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (data.getKey().equals(foodId)) {
                                food = data.getValue(Food.class);
                            }
                        }

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                        if (food.getImageUrl() == null) {
                            foodImage.setImageResource(R.drawable.dish);
                            ViewGroup.LayoutParams layoutParams = foodImage.getLayoutParams();
                            layoutParams.width = displayMetrics.widthPixels;
                            final float density = getApplicationContext().getResources().getDisplayMetrics().density;
                            layoutParams.height = (int) (230 * density);
                            foodImage.setLayoutParams(layoutParams);
                        } else {
                            if (food.getImageUrl().equals("null")) {
                                foodImage.setImageResource(R.drawable.dish);
                                ViewGroup.LayoutParams layoutParams = foodImage.getLayoutParams();
                                layoutParams.width = displayMetrics.widthPixels;
                                final float density = getApplicationContext().getResources().getDisplayMetrics().density;
                                layoutParams.height = (int) (230 * density);
                                foodImage.setLayoutParams(layoutParams);
                            } else {
                                bufferLayout.setVisibility(View.GONE);
                                Picasso.get().load(food.getImageUrl()).fit().into(foodImage);
                            }
                        }


                        reference2 = FirebaseDatabase.getInstance().getReference("Orders");
                        reference2.child("Completed").child(recipientId).child(slotId).child(foodId).addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        order = snapshot.getValue(Order.class);

                                        reference3 = FirebaseDatabase.getInstance().getReference("Users");
                                        reference3.addValueEventListener(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                        for (DataSnapshot data : snapshot.getChildren()) {
                                                            if (data.getKey().equals(donorId)) {
                                                                donor = data.getValue(User.class);
                                                            }
                                                        }

                                                        orderQuantity = order.getQuantity();
                                                        foodNameTxt.setText(food.getName());
                                                        foodDescriptionTxt.setText(food.getDescription());
                                                        txtOrderQuantity.setText(String.valueOf(orderQuantity));
                                                        txtSchedule.setText(order.getStartTime()
                                                                + " - "
                                                                + order.getEndTime()
                                                                + ", "
                                                                + order.getDate());
                                                        txtAddress.setText(donor.getAddress());
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });

        FirebaseDatabase.getInstance().getReference("Users").child(donorId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                User user = task.getResult().getValue(User.class);
                if (user.getRestaurant() == null || user.getRestaurant().equals("null")) {
                    txtDonor.setText(user.getName());
                } else {
                    txtDonor.setText(user.getRestaurant());
                }
            }
        });

        if(userId.equals(recipientId)) {
            btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewCompletedOrder.this, ReportDonorActivity.class);
                    intent.putExtra("donorId", donorId);
                    intent.putExtra("slotId", slotId);
                    intent.putExtra("foodId", foodId);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            btnReport.setVisibility(View.GONE);
            txtDonor.setVisibility(View.GONE);
            txtDonorHeader.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent;
        if(userId.equals(recipientId)) {
            intent = new Intent(ViewCompletedOrder.this, RecipientsRecords.class);
        } else {
            intent = new Intent(ViewCompletedOrder.this, DonorsRecords.class);
        }
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if(userId.equals(recipientId)) {
            intent = new Intent(ViewCompletedOrder.this, RecipientsRecords.class);
        } else {
            intent = new Intent(ViewCompletedOrder.this, DonorsRecords.class);
        }
        startActivity(intent);
        finish();
    }
}