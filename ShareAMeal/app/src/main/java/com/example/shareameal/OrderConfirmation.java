package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class OrderConfirmation extends AppCompatActivity {

    private Button btnBack, btnConfirmOrder;
    private ImageView foodImage;
    private TextView foodNameTxt, foodDescriptionTxt, txtCurrentQuantity, txtSchedule, txtAddress;
    private EditText foodQuantityEdt;

    //data
    private DatabaseReference reference1, reference2, reference3, reference4;
    private Bundle bundle;
    private Slot slot;
    private String donorId, foodId;
    private Food food;
    private User donor;
    private int orderQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        btnBack = findViewById(R.id.btnBack);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        foodImage = findViewById(R.id.foodImage);
        foodNameTxt = findViewById(R.id.foodNameTxt);
        foodDescriptionTxt = findViewById(R.id.foodDescriptionTxt);
        txtCurrentQuantity = findViewById(R.id.txtOrderQuantity);
        txtSchedule = findViewById(R.id.txtSchedule);
        txtAddress = findViewById(R.id.txtAddress);
        foodQuantityEdt = findViewById(R.id.foodQuantityEdt);

        Intent intent = getIntent();
        bundle = intent.getExtras();
        slot = bundle.getParcelable("slot");
        donorId = bundle.getString("donorId");
        foodId = bundle.getString("foodId");

        reference1 = FirebaseDatabase.getInstance().getReference("Foods").child(donorId);

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    if(data.getKey().equals(foodId)) {
                        food = data.getValue(Food.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        reference2 = FirebaseDatabase.getInstance().getReference("Users");

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    if(data.getKey().equals(donorId)) {
                        donor = data.getValue(User.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        if (food.getImageUrl() == null) {
            foodImage.setImageResource(R.drawable.dish);
        } else {
            if (food.getImageUrl().equals("null")) {
                foodImage.setImageResource(R.drawable.dish);
            } else {
                Picasso.get().load(food.getImageUrl()).into(foodImage);
            }
        }

        foodNameTxt.setText(food.getName());
        foodDescriptionTxt.setText(food.getDescription());
        txtCurrentQuantity.setText("Current quantity: " + food.getQuantity());
        txtSchedule.setText("Scheduled for collection at:\n" + slot.getStartTime() + " - " + slot.getEndTime() + ", " + slot.getDate());
        txtAddress.setText("Address: " + donor.getAddress());



    }

    public void onConfirmOrder(View view) {
        String qty = foodQuantityEdt.getText().toString();
        if(Integer.valueOf(qty) == 0 || TextUtils.isEmpty(qty)) {
            Toast.makeText(OrderConfirmation.this, "Please enter a number larger than 0.", Toast.LENGTH_SHORT).show();
        } else {
            orderQuantity = Integer.valueOf(qty);

            //create new order
            Order order = new Order();
            order.setDate(slot.getDate());
            order.setStartTime(slot.getStartTime());
            order.setEndTime(slot.getEndTime());
            order.setDonorId(donorId);
            order.setFoodId(foodId);
            order.setQuantity(orderQuantity);
            order.setFoodName(food.getName());
            order.setFoodImageURL(food.getImageUrl());
            reference4 = FirebaseDatabase.getInstance().getReference("Orders").child(slot.getRecipientId());
            String orderId = reference4.push().getKey();
            reference4.child(orderId).setValue(order);

            //update food item
            food.setQuantity(food.getQuantity() - orderQuantity);
            reference1.child(foodId).setValue(food);

            //update slot
            slot.setAvailability(false);
            reference3 = FirebaseDatabase.getInstance().getReference("Slots").child(donorId);
            reference3.child(slot.getSlotId()).setValue(slot);

            Toast.makeText(OrderConfirmation.this, "Your order has been successfully created.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(OrderConfirmation.this, RVDonors.class);
            startActivity(intent);
            finish();
        }
    }

    public void onBackBtn(View view) {
        // how do i get back to the Reserve Food Item page for the right food item?
        // temporary solution:
        Intent intent = new Intent(OrderConfirmation.this, RVDonors.class);
        startActivity(intent);
        finish();
    }
}