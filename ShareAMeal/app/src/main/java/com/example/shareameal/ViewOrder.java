package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recipientId = user.getUid();

        reference4 = FirebaseDatabase.getInstance().getReference("Slots");

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

                        if (food.getImageUrl() == null) {
                            foodImage.setImageResource(R.drawable.dish);
                        } else {
                            if (food.getImageUrl().equals("null")) {
                                foodImage.setImageResource(R.drawable.dish);
                            } else {
                                Picasso.get().load(food.getImageUrl()).into(foodImage);
                            }
                        }


                        reference2 = FirebaseDatabase.getInstance().getReference("Orders");
                        reference2.child("Pending").child(recipientId).child(slotId).child(foodId).addListenerForSingleValueEvent(
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
                                                        txtOrderQuantity.setText("Order quantity: " + orderQuantity);
                                                        txtSchedule.setText(
                                                                "Scheduled for collection at:\n"
                                                                        + order.getStartTime()
                                                                        + " - "
                                                                        + order.getEndTime()
                                                                        + ", "
                                                                        + order.getDate());
                                                        txtAddress.setText("Address: " + donor.getAddress());
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
        reference2.child("Pending").child(recipientId).child(slotId).child(foodId).removeValue();

        // update slot only if the same recipient does not have any other orders in the same time slot
        ArrayList<String> foodIds = new ArrayList<>();
        reference2.child("Pending").child(recipientId).child(slotId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    foodIds.add(data.getKey());
                }
                if(foodIds.isEmpty()) {
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
                }
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
        reference2.child("Completed").child(recipientId).child(slotId).child(foodId).setValue(order);
        reference2.child("Pending").child(recipientId).child(slotId).child(foodId).removeValue();

        // update slot only if the same recipient does not have any other orders in the same time slot
        ArrayList<String> foodIds = new ArrayList<>();
        reference2.child("Pending").child(recipientId).child(slotId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    foodIds.add(data.getKey());
                }
                if(foodIds.isEmpty()) {
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
                                    if(recipientId.equals(slot.getRecipientId1())) {
                                        slot.setRecipientId1(null);
                                    } else if(recipientId.equals(slot.getRecipientId2())) {
                                        slot.setRecipientId2(null);
                                    } else if(recipientId.equals(slot.getRecipientId3())) {
                                        slot.setRecipientId3(null);
                                    }
                                    reference4.child("Pending").child(donorId).child(slotId).setValue(slot);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        // record order in Completed section of Slots
        reference4.child("Completed").child(donorId).child(slotId).child(recipientId).child(foodId).setValue(order);

        Toast.makeText(ViewOrder.this, "Order has been registered as completed.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ViewOrder.this, RecipientViewOrders.class);
        startActivity(intent);
        finish();
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
