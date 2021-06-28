package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class RecipientHomepageActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView userNameTxt, reminderTxt;
    private int numOrdersLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_homepage);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(RecipientHomepageActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int curr = item.getItemId();
                        if (curr == R.id.claimFood) {
                            Intent intent = new Intent(RecipientHomepageActivity.this, RVDonors.class);
                            startActivity(intent);
                            finish();
                        } else if (curr == R.id.schedule) {
                            Intent intent = new Intent(RecipientHomepageActivity.this, RecipientViewOrders.class);
                            startActivity(intent);
                            finish();
                        } else if (curr == R.id.profile) {
                            Intent intent =
                                    new Intent(RecipientHomepageActivity.this, RecipientUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });

        userNameTxt = findViewById(R.id.userNameTxt);
        reminderTxt = findViewById(R.id.reminderTxt);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference
                .child(userid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                userNameTxt.setText(user.getName());

                                if (user.getYear() != currentYear || user.getMonth() != currentMonth
                                   || user.getDayOfMonth() != currentDayOfMonth) {
                                    user.setYear(currentYear);
                                    user.setMonth(currentMonth);
                                    user.setDayOfMonth(currentDayOfMonth);
                                    user.setNumOrdersLeft(3);
                                    numOrdersLeft = 3;
                                    reference.child(userid).setValue(user);

                                } else {
                                    numOrdersLeft = user.getNumOrdersLeft();
                                }

                                reminderTxt.setText("Daily claimable food items left: " + numOrdersLeft);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
    }

    @Override
    public void onBackPressed() {

    }
}
