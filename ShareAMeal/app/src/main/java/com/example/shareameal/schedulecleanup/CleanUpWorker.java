package com.example.shareameal.schedulecleanup;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.shareameal.Slot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        return Result.success();
    }
}
