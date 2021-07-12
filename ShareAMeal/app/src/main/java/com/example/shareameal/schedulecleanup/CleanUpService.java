package com.example.shareameal.schedulecleanup;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shareameal.Order;
import com.example.shareameal.Slot;
import com.example.shareameal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.EnhancedIntentService;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class CleanUpService extends IntentService {

    private static final String TAG = "CleanUpService";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CleanUpService(String name) {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("service_started", "Clean-up service started.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        Calendar cal = Calendar.getInstance();
        int todayYear = cal.get(Calendar.YEAR);
        int todayMonth = cal.get(Calendar.MONTH) + 1;
        int today = cal.get(Calendar.DAY_OF_MONTH);

        // clean up entire database for Slots and Orders
        DatabaseReference slotsReference = FirebaseDatabase.getInstance().getReference("Slots").child("Pending");
        slotsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot donor : snapshot.getChildren()) {
                    for(DataSnapshot data : donor.getChildren()) {
                        Slot slot = data.getValue(Slot.class);
                        // if the slot has expired then remove it
                        if(slot.getYear() < todayYear ||
                                (slot.getYear() == todayYear && slot.getMonth() < todayMonth) ||
                                (slot.getYear() == todayYear && slot.getMonth() == todayMonth && slot.getDayOfMonth() < today)) {
                            slotsReference.child(donor.getKey()).child(slot.getSlotId()).removeValue();
                        }
                        Log.d("schedule clean up", "Outdated slots removed.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference("Orders").child("Pending");
        ordersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot recipient : snapshot.getChildren()) {
                    for(DataSnapshot slot : recipient.getChildren()) {
                        for(DataSnapshot data : slot.getChildren()) {
                            Order order = data.getValue(Order.class);
                            // if the slot has expired then remove it
                            if(order.getYear() < todayYear ||
                                    (order.getYear() == todayYear && order.getMonth() < todayMonth) ||
                                    (order.getYear() == todayYear && order.getMonth() == todayMonth && order.getDayOfMonth() < today)) {
                                ordersReference.child(recipient.getKey()).child(order.getSlotId()).child(order.getFoodId()).removeValue();
                            }
                            Log.d("schedule clean up", "Outdated orders removed.");
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
