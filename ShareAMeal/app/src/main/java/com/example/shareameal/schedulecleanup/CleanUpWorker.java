package com.example.shareameal.schedulecleanup;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.shareameal.MainActivity;
import com.example.shareameal.Order;
import com.example.shareameal.Slot;
import com.example.shareameal.User;
import com.example.shareameal.notifications.NotificationsSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class CleanUpWorker extends Worker {

    private int todayYear, todayMonth, todayDay;
    private Integer lastRefreshedYear, lastRefreshedMonth, lastRefreshedDay;

    public CleanUpWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        Calendar cal = Calendar.getInstance();
        todayYear = cal.get(Calendar.YEAR);
        todayMonth = cal.get(Calendar.MONTH) + 1;
        todayDay = cal.get(Calendar.DAY_OF_MONTH);


        // clean up outdated Slots
        DatabaseReference slotsRef = FirebaseDatabase.getInstance().getReference("Slots");
        slotsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Log.e("Firebase", "Failed to get last refreshed date for slots", task.getException());
                } else {
                    DataSnapshot dataSnapshot = task.getResult();
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        if(data.getKey().equals("lastRefreshedYear")) {
                            lastRefreshedYear = data.getValue(Integer.class);
                        } else if(data.getKey().equals("lastRefreshedMonth")) {
                            lastRefreshedMonth = data.getValue(Integer.class);
                        } else if(data.getKey().equals("lastRefreshedDay")) {
                            lastRefreshedDay = data.getValue(Integer.class);
                        }
                    }

                    if(!(lastRefreshedYear == todayYear && lastRefreshedMonth == todayMonth && lastRefreshedDay == todayDay)) {
                        slotsRef.child("Pending").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                                if(!task.isSuccessful()) {
                                    Log.e("Firebase", "Failed to get pending slots", task.getException());
                                } else {
                                    DataSnapshot pendingSlots = task.getResult();
                                    for(DataSnapshot slotsUnderDonors : pendingSlots.getChildren()) {
                                        String donorId = slotsUnderDonors.getKey();
                                        Log.d("donorId", donorId);
                                        for(DataSnapshot slotData : slotsUnderDonors.getChildren()) {
                                            Slot slot = slotData.getValue(Slot.class);
                                            Log.d("slotId", slot.getSlotId());
                                            if(slot.getYear() < todayDay || (slot.getYear() == todayYear && slot.getMonth() < todayMonth)
                                                || (slot.getYear() == todayYear && slot.getMonth() == todayMonth && slot.getDayOfMonth() < todayDay)) {
                                                Log.d("cleanup", "outdated slot removed");
                                                slotsRef.child("Pending").child(donorId).child(slot.getSlotId()).removeValue();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        slotsRef.child("lastRefreshedYear").setValue(todayYear);
                        slotsRef.child("lastRefreshedMonth").setValue(todayMonth);
                        slotsRef.child("lastRefreshedDay").setValue(todayDay);
                    }
                }
            }
        });

        Log.d("ScheduleCleanUp", "Pending slots clean up completed.");

        // send a notification to recipients whose orders are before the current date.

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        ordersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()) {
                    Log.e("Firebase", "Failed to get last refreshed date for orders", task.getException());
                } else {
                    DataSnapshot dataSnapshot = task.getResult();
                    for(DataSnapshot data : dataSnapshot.getChildren()) {
                        if(data.getKey().equals("lastRefreshedYear")) {
                            lastRefreshedYear = data.getValue(Integer.class);
                        } else if(data.getKey().equals("lastRefreshedMonth")) {
                            lastRefreshedMonth = data.getValue(Integer.class);
                        } else if(data.getKey().equals("lastRefreshedDay")) {
                            lastRefreshedDay = data.getValue(Integer.class);
                        }
                    }

                    if(!(lastRefreshedYear == todayYear && lastRefreshedMonth == todayMonth && lastRefreshedDay == todayDay)) {
                        ordersRef.child("Pending").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for(DataSnapshot ordersUnderRecipient : snapshot.getChildren()) {
                                    String recipientId = ordersUnderRecipient.getKey();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                    reference
                                            .child(recipientId)
                                            .addListenerForSingleValueEvent(
                                                    new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            User recipient = snapshot.getValue(User.class);
                                                            String token = recipient.getFcmToken();
                                                            for(DataSnapshot ordersUnderSlot : ordersUnderRecipient.getChildren()) {
                                                                for(DataSnapshot orderData : ordersUnderSlot.getChildren()) {
                                                                    Order order = orderData.getValue(Order.class);
                                                                    if(order.getYear() < todayDay || (order.getYear() == todayYear && order.getMonth() < todayMonth)
                                                                            || (order.getYear() == todayYear && order.getMonth() == todayMonth && order.getDayOfMonth() < todayDay)) {

                                                                        String body = "You have outdated order(s). Please confirm the completion of or delete the order(s).";
                                                                        Activity activity = (Activity) getApplicationContext();

                                                                        NotificationsSender notificationsSender = new NotificationsSender(token, "Order is outdated", body, getApplicationContext(), activity);
                                                                        notificationsSender.sendNotification();
                                                                    }
                                                                }
                                                            }
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
                        ordersRef.child("lastRefreshedYear").setValue(todayYear);
                        ordersRef.child("lastRefreshedMonth").setValue(todayMonth);
                        ordersRef.child("lastRefreshedDay").setValue(todayDay);
                    }
                }
            }
        });

        return Result.success();
    }
}
