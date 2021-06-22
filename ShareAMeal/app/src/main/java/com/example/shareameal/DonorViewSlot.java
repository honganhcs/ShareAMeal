package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class DonorViewSlot extends AppCompatActivity {

    private TextView txtDate, txtTime, txtReserved, txtReservedItem;
    private Slot slot;
    private Order order;
    private Food food;
    private DatabaseReference reference1, reference2, reference3;
    private String donorId, recipientId, foodId, slotId;
    private int qty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_view_slot);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("View time slot");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtReserved = findViewById(R.id.txtReserved);
        txtReservedItem = findViewById(R.id.txtReservedItem);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        slot = bundle.getParcelable("slot");
        recipientId = slot.getRecipientId();
        slotId = slot.getSlotId();

        txtDate.setText("Date: " + slot.getDate());
        txtTime.setText("Time: " + slot.getStartTime() + " - " + slot.getEndTime());
        txtReserved.setText(slot.isAvailability() ? "Not Reserved" : "Reserved for Donation of:");

        if(slot.isAvailability()) {
            txtReservedItem.setText("");
        } else {
            reference1 = FirebaseDatabase.getInstance().getReference("Orders").child(recipientId);
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot data : snapshot.getChildren()) {
                        if(data.getKey().equals(slotId)) {
                            order = data.getValue(Order.class);
                            donorId = order.getDonorId();
                            foodId = order.getFoodId();
                        }
                    }

                    reference2 = FirebaseDatabase.getInstance().getReference("Foods").child(donorId);
                    reference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for(DataSnapshot data : snapshot.getChildren()) {
                                if(data.getKey().equals(foodId)) {
                                    food = data.getValue(Food.class);
                                }
                            }

                            qty = order.getQuantity();
                            String foodName = food.getName();
                            txtReservedItem.setText(qty + " " + foodName);
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
    }


    public void onDeleteBtn(View view) {
        reference3 = FirebaseDatabase.getInstance().getReference("Slots").child(donorId);
        //remove slot
        reference3.child(slotId).removeValue();

        //remove order
        reference1.child(slotId).removeValue();

        //update food quantity
        food.setQuantity(food.getQuantity() + qty);
        reference2.child(foodId).setValue(food);

        Intent intent = new Intent(DonorViewSlot.this, DonorsScheduleActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(DonorViewSlot.this, DonorsScheduleActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
}