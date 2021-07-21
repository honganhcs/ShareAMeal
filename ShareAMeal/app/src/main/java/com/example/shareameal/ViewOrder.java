package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shareameal.notifications.NotificationsSender;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewOrder extends AppCompatActivity {

    private ImageView foodImage;
    private TextView foodNameTxt, foodDescriptionTxt, txtOrderQuantity, txtSchedule, txtAddress, txtDonor;
    private DatabaseReference reference1, reference2, reference3, reference4;
    private String donorId, foodId, slotId, recipientId;
    private Order order;
    private Slot slot;
    private Food food;
    private User donor;
    private int orderQuantity;
    private String foodName;
    private ConstraintLayout bufferLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        //top action bar
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("View order");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // initiate widgets
        foodImage = findViewById(R.id.foodImage);
        foodNameTxt = findViewById(R.id.foodNameTxt);
        foodDescriptionTxt = findViewById(R.id.foodDescriptionTxt);
        txtOrderQuantity = findViewById(R.id.txtOrderQuantity);
        txtSchedule = findViewById(R.id.txtSchedule);
        txtAddress = findViewById(R.id.txtAddress);
        txtDonor = findViewById(R.id.txtDonor);
        bufferLayout = findViewById(R.id.layout1);

        //initiate current userId (recipientId)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recipientId = user.getUid();

        // initiate reference4 for Slots.
        reference4 = FirebaseDatabase.getInstance().getReference("Slots");

        Intent intent = getIntent();

        // initiate donorId, foodId, slotId
        donorId = intent.getStringExtra("donorId");
        foodId = intent.getStringExtra("foodId");
        slotId = intent.getStringExtra("slotId");

        // initiate reference 1 for Foods
        reference1 = FirebaseDatabase.getInstance().getReference("Foods").child(donorId);
        reference1.child(foodId).get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Firebase", "Error getting food", task.getException());
                        } else {
                            food = task.getResult().getValue(Food.class);

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

                            // initiate reference2 for Orders
                            reference2 = FirebaseDatabase.getInstance().getReference("Orders");
                            reference2.child("Pending").child(recipientId).child(slotId).child(foodId).get().addOnCompleteListener(
                                    new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("Firebase", "Error getting order", task.getException());
                                            } else {
                                                order = task.getResult().getValue(Order.class);

                                                // initiate reference3 for Users
                                                reference3 = FirebaseDatabase.getInstance().getReference("Users");
                                                reference3.child(donorId).get().addOnCompleteListener(
                                                        new OnCompleteListener<DataSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                                if(!task.isSuccessful()) {
                                                                    Log.e("Firebase", "Error getting user", task.getException());
                                                                } else {
                                                                    donor = task.getResult().getValue(User.class);

                                                                    orderQuantity = order.getQuantity();
                                                                    foodName = food.getName();
                                                                    foodNameTxt.setText(foodName);
                                                                    foodDescriptionTxt.setText(food.getDescription());
                                                                    txtOrderQuantity.setText(String.valueOf(orderQuantity));
                                                                    txtSchedule.setText(order.getStartTime()
                                                                            + " - "
                                                                            + order.getEndTime()
                                                                            + ", "
                                                                            + order.getDate());
                                                                    txtAddress.setText(donor.getAddress());
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

        FirebaseDatabase.getInstance().getReference("Users").child(donorId).get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
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

                        // update food quantity
                        food.setQuantity(food.getQuantity() + orderQuantity);
                        reference1.child(foodId).setValue(food);

                        // send notification to donor
                        String fcmToken = donor.getFcmToken();
                        NotificationsSender notificationsSender = new NotificationsSender(fcmToken, "Order cancelled", "The order of " + orderQuantity + " " + foodName
                                + " at " + order.getStartTime() + " on " + order.getDate() + " has been cancelled by the recipient.", getApplicationContext(), ViewOrder.this);
                        notificationsSender.sendNotification();

                        // update slot only if the same recipient does not have any other orders in the same time slot
                        ArrayList<String> foodIds = new ArrayList<>();
                        reference2.child("Pending").child(recipientId).child(slotId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for(DataSnapshot data : snapshot.getChildren()) {
                                    foodIds.add(data.getKey());
                                }
                                if(foodIds.isEmpty()) {
                                    reference4.child("Pending").child(donorId).child(slotId).get().addOnCompleteListener(
                                            new OnCompleteListener<DataSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                    if (!task.isSuccessful()) {
                                                        Log.e("Firebase", "Error getting slot", task.getException());
                                                    } else {
                                                        slot = task.getResult().getValue(Slot.class);

                                                        if(slot != null) {
                                                            slot.setNumRecipients(slot.getNumRecipients() - 1);
                                                            if (slot.getRecipientId1().equals(recipientId)) {
                                                                slot.setRecipientId1(null);
                                                            } else if (slot.getRecipientId2().equals(recipientId)) {
                                                                slot.setRecipientId2(null);
                                                            } else if (slot.getRecipientId3().equals(recipientId)) {
                                                                slot.setRecipientId3(null);
                                                            }
                                                            reference4.child("Pending").child(donorId).child(slotId).setValue(slot);
                                                        }

                                                    }
                                                }
                                            });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
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
        Calendar cal = Calendar.getInstance();
        int todayYear = cal.get(Calendar.YEAR);
        int todayMonth = cal.get(Calendar.MONTH) + 1;
        int today = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int endHour, endMinute;
        if(order.getStartMinute() == 30) {
            endHour = order.getStartHour() + 1;
            endMinute = 0;
        } else {
            endHour = order.getStartHour();
            endMinute = 30;
        }

        if(order.getYear() > todayYear || (order.getYear() == todayYear && order.getMonth() > todayMonth)
           || (order.getYear() == todayYear && order.getMonth() == todayMonth && order.getDayOfMonth() > today)
        || (order.getYear() == todayYear && order.getMonth() == todayMonth && order.getDayOfMonth() == today && endHour > hour)
        || (order.getYear() == todayYear && order.getMonth() == todayMonth && order.getDayOfMonth() == today && endHour == hour && endMinute >= minute)) {
            Toast.makeText(this, "You can only complete the order once the time slot for this order has ended.", Toast.LENGTH_LONG).show();
        } else {

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Complete Order")
                    .setMessage("Are you sure you have claimed the donated food items as stated in the order?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // move the order from Pending to Completed in the database
                            reference2.child("Completed").child(recipientId).child(slotId).child(foodId).setValue(order);
                            reference2.child("Pending").child(recipientId).child(slotId).child(foodId).removeValue();

                            // send notification to donor
                            String fcmToken = donor.getFcmToken();
                            NotificationsSender notificationsSender = new NotificationsSender(fcmToken, "Order completed", "The order of " + orderQuantity + " " + foodName
                                    + " at " + order.getStartTime() + " on " + order.getDate() + " has been registered as completed by the recipient.", getApplicationContext(), ViewOrder.this);
                            notificationsSender.sendNotification();

                            // update slot only if the same recipient does not have any other orders in the same time slot
                            ArrayList<String> foodIds = new ArrayList<>();
                            reference2.child("Pending").child(recipientId).child(slotId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        foodIds.add(data.getKey());
                                    }
                                    if (foodIds.isEmpty()) {
                                        reference4.child("Pending").child(donorId).child(slotId).get().addOnCompleteListener(
                                                new OnCompleteListener<DataSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                                        if (!task.isSuccessful()) {
                                                            Log.e("Firebase", "Error getting slot", task.getException());
                                                        } else {
                                                            slot = task.getResult().getValue(Slot.class);
                                                            if(slot != null) {
                                                                slot.setNumRecipients(slot.getNumRecipients() - 1);
                                                                if (slot.getRecipientId1().equals(recipientId)) {
                                                                    slot.setRecipientId1(null);
                                                                } else if (slot.getRecipientId2().equals(recipientId)) {
                                                                    slot.setRecipientId2(null);
                                                                } else if (slot.getRecipientId3().equals(recipientId)) {
                                                                    slot.setRecipientId3(null);
                                                                }
                                                                reference4.child("Pending").child(donorId).child(slotId).setValue(slot);
                                                            }

                                                        }
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
