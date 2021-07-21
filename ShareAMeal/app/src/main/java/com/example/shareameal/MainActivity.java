package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.shareameal.notifications.NotificationsSender;
import com.example.shareameal.schedulecleanup.CleanUpWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private String token;
    private int todayYear, todayMonth, todayDay;
    private Integer lastRefreshedYear, lastRefreshedMonth, lastRefreshedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userid = user.getUid();

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("fail", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            token = task.getResult();
                        }
                    });

            WorkRequest cleanUpWorkRequest = new OneTimeWorkRequest.Builder(CleanUpWorker.class).build();
            WorkManager.getInstance(MainActivity.this).enqueue(cleanUpWorkRequest);

            // send a notification to recipients whose orders are before the current date.
            Calendar cal = Calendar.getInstance();
            todayYear = cal.get(Calendar.YEAR);
            todayMonth = cal.get(Calendar.MONTH) + 1;
            todayDay = cal.get(Calendar.DAY_OF_MONTH);

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
                                                                        if(order.getYear() < todayYear || (order.getYear() == todayYear && order.getMonth() < todayMonth)
                                                                                || (order.getYear() == todayYear && order.getMonth() == todayMonth && order.getDayOfMonth() < todayDay)) {

                                                                            String body = "You have outdated order(s). Please confirm the completion of or delete the order(s).";

                                                                            Log.d("notifications", "sending notifications");
                                                                            Log.d("notifications", recipientId);

                                                                            NotificationsSender notificationsSender = new NotificationsSender(token, "Outdated order(s)", body, getApplicationContext(), MainActivity.this);
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

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference
                    .child(userid)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);

                                    // Redirecting user to the correct interface/home page
                                    if (user == null) {
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        user.setFcmToken(token);
                                        reference.child(userid).setValue(user);
                                        if (user.getUserGroup().equals("donor")) {
                                            Intent intent = new Intent(MainActivity.this, DonorHomepageActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (user.getUserGroup().equals("recipient")) {
                                            Intent intent = new Intent(MainActivity.this, RecipientHomepageActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (user.getUserGroup().equals("admin")) {
                                            Intent intent = new Intent(MainActivity.this, AdminHomepageActivity.class);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
        }
    }
}
