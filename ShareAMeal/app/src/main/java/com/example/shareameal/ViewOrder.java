package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class ViewOrder extends AppCompatActivity {

    private ImageView foodImage;
    private TextView foodNameTxt, foodDescriptionTxt, txtOrderQuantity, txtSchedule, txtAddress;
    private DatabaseReference reference1, reference2, reference3, reference4;
    private String donorId, foodId, slotId, recipientId;
    private Order order;
    private Slot slot;
    private Food food;
    private User donor, recipient;
    private int orderQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

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

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = user.getUid();

                        reference2 = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
                        reference2.addValueEventListener(
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
                                                            if (data.getKey().equals(recipientId)) {
                                                                recipient = data.getValue(User.class);
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
    }

    public void onCancelOrder(View view) {
        // remove order
        reference2.child(slotId).removeValue();

        // update slot
        reference4 = FirebaseDatabase.getInstance().getReference("Slots").child(donorId);
        reference4.addValueEventListener(
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
                        reference4.child(slotId).setValue(slot);
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
