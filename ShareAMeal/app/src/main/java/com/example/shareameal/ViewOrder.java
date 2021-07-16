package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
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

import java.util.ArrayList;

public class ViewOrder extends AppCompatActivity {

    private ImageView foodImage;
    private TextView foodNameTxt, foodDescriptionTxt, txtOrderQuantity, txtSchedule, txtAddress, txtDonorHeader, txtDonor;
    private DatabaseReference reference1, reference2, reference3, reference4;
    private String donorId, foodId, slotId, recipientId;
    private Order order;
    private Slot slot;
    private Food food;
    private User donor;
    private int orderQuantity;
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
        txtDonorHeader = findViewById(R.id.txtDonorHeader);
        txtDonor = findViewById(R.id.txtDonor);
        bufferLayout = findViewById(R.id.layout1);

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
    }

    public void onCancelOrder(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                })
                .setNegativeButton("No", null)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#AF1B1B"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#AF1B1B"));
    }

    public void onOrderCompleted(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Complete Order")
                .setMessage("Are you sure you have claimed the donated food items as stated in the order?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                                                        if (data.getKey().equals(slotId)) {
                                                            slot = data.getValue(Slot.class);
                                                        }
                                                    }
                                                    slot.setNumRecipients(slot.getNumRecipients() - 1);
                                                    if (recipientId.equals(slot.getRecipientId1())) {
                                                        slot.setRecipientId1(null);
                                                    } else if (recipientId.equals(slot.getRecipientId2())) {
                                                        slot.setRecipientId2(null);
                                                    } else if (recipientId.equals(slot.getRecipientId3())) {
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
                        order.setRecipientId(recipientId);
                        reference4.child("Completed").child(donorId).child(slotId).child(recipientId).child(foodId).setValue(order);

                        // Update weekly points and all-time points for donors
                        DatabaseReference donorRef = FirebaseDatabase.getInstance().getReference("Users").child(donorId);
                        donorRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                User user = task.getResult().getValue(User.class);
                                int currWeeklyPoints = user.getNumberOfWeeklyPoints();
                                int currAllTimePoints = user.getNumberOfPoints();
                                donorRef.child("numberOfWeeklyPoints").setValue(currWeeklyPoints + orderQuantity);
                                donorRef.child("numberOfPoints").setValue(currAllTimePoints + orderQuantity);
                            }
                        });

                        Toast.makeText(ViewOrder.this, "Order has been registered as completed.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewOrder.this, RecipientsRecords.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#AF1B1B"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#AF1B1B"));
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
