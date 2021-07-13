package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    private String token;

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
