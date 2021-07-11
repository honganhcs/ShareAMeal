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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ViewOrder extends AppCompatActivity {

    private ImageView foodImage;
    private TextView foodNameTxt, foodDescriptionTxt, txtOrderQuantity, txtSchedule, txtAddress;
    private DatabaseReference reference1, reference2, reference3, reference4;
    private String donorId, foodId, slotId, recipientId;
    private Order order;
    private Slot slot;
    private Food food;
    private User donor;
    private int orderQuantity;
    private AppCompatButton btnReport;
    private ConstraintLayout bufferLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("View order");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        foodImage = findViewById(R.id.foodImage);
        foodNameTxt = findViewById(R.id.foodNameTxt);
        foodDescriptionTxt = findViewById(R.id.foodDescriptionTxt);
        txtOrderQuantity = findViewById(R.id.txtOrderQuantity);
        txtSchedule = findViewById(R.id.txtSchedule);
        txtAddress = findViewById(R.id.txtAddress);
        bufferLayout = findViewById(R.id.layout1);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recipientId = user.getUid();

        Intent intent = getIntent();

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
                        reference2.child("Pending").child(recipientId).addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        for (DataSnapshot data : snapshot.getChildren()) {
                                            if (data.getKey().equals(slotId)) {
                                                order = data.getValue(Order.class);
                                            }
                                        }

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

        btnReport = findViewById(R.id.btnReport);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewOrder.this, ReportDonorActivity.class);
                intent.putExtra("donorId", donorId);
                intent.putExtra("slotId", slotId);
                intent.putExtra("foodId", foodId);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onCancelOrder(View view) {
        // remove order
        reference2.child("Pending").child(recipientId).child(slotId).removeValue();

        // update slot
        reference4 = FirebaseDatabase.getInstance().getReference("Slots");
        reference4.child("Pending").child(donorId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if(data.getKey().equals(slotId)) {
                                slot = data.getValue(Slot.class);
                            }
                        }
                        slot.setNumRecipients(slot.getNumRecipients() - 1);
                        if(slot.getRecipientId1().equals(recipientId)) {
                            slot.setRecipientId1(null);
                        } else if(slot.getRecipientId2().equals(recipientId)) {
                            slot.setRecipientId2(null);
                        } else if(slot.getRecipientId3().equals(recipientId)) {
                            slot.setRecipientId3(null);
                        }
                        reference4.child("Pending").child(donorId).child(slotId).setValue(slot);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });

        // update food quantity
        food.setQuantity(food.getQuantity() + orderQuantity);
        reference1.child(foodId).setValue(food);

        Toast.makeText(ViewOrder.this, "Your order has been successfully cancelled.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ViewOrder.this, RecipientViewOrders.class);
        startActivity(intent);
        finish();
    }

    public void onOrderCompleted(View view) {
        // move the order from Pending to Completed in the database
        reference2.child("Completed").child(recipientId).child(slotId).setValue(order);
        reference2.child("Pending").child(recipientId).child(slotId).removeValue();

        // update the slot in the Pending section
        reference4.child("Pending").child(donorId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if(data.getKey().equals(slotId)) {
                                slot = data.getValue(Slot.class);
                            }
                        }
                        slot.setNumRecipients(slot.getNumRecipients() - 1);
                        if(slot.getRecipientId1().equals(recipientId)) {
                            slot.setRecipientId1(null);
                        } else if(slot.getRecipientId2().equals(recipientId)) {
                            slot.setRecipientId2(null);
                        } else if(slot.getRecipientId3().equals(recipientId)) {
                            slot.setRecipientId3(null);
                        }
                        reference4.child("Pending").child(donorId).child(slotId).setValue(slot);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
        // record order in Completed section of Slots
        reference4.child("Completed").child(donorId).child(slotId).setValue(order);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(ViewOrder.this, RecipientViewOrders.class);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewOrder.this, RecipientViewOrders.class);
        startActivity(intent);
        finish();
    }

}
