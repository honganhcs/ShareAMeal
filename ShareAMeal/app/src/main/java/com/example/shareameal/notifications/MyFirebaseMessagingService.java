package com.example.shareameal.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.shareameal.LoginActivity;
import com.example.shareameal.MainActivity;
import com.example.shareameal.R;
import com.example.shareameal.RecipientViewOrders;
import com.example.shareameal.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManager notificationManager;

    @Override
    public void onNewToken(String token) {
        Log.d("new token", "Refreshed token " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(MyFirebaseMessagingService.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            String userid = user.getUid();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference
                    .child(userid)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    user.setFcmToken(token);
                                    reference.child(userid).setValue(user);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("message_received", "From " + remoteMessage.getFrom());
        Intent intent;

        if(remoteMessage.getNotification().getTitle().equals(getString(R.string.slot_booked))
        || remoteMessage.getNotification().getTitle().equals("Outdated order(s)")) {
            intent = new Intent(this, RecipientViewOrders.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // vibration when notification is received
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300, 300, 300};
        v.vibrate(pattern, -1);

        // can add sound and image to notification as well

        // need more research on channel ID
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage
                .getNotification().getBody()))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.logo);

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // need more research for this part
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id),
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(getString(R.string.channel_id));
        }

        //how to define notification id??
        notificationManager.notify(0, builder.build());
    }

}