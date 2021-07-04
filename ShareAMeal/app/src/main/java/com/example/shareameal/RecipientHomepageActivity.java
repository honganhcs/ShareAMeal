package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private int numOrdersLeft, verificationState;
    private ConstraintLayout verifyMsg, verifyInProcessMsg, rejectMsg, unclearDocMsg;
    private AppCompatButton verifyBtn, verifyAgainBtn;

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

        userNameTxt = findViewById(R.id.userNameTxt);
        reminderTxt = findViewById(R.id.reminderTxt);

        verifyMsg = findViewById(R.id.verifyMsg);
        verifyInProcessMsg = findViewById(R.id.verifyInProcessMsg);
        rejectMsg = findViewById(R.id.rejectMsg);
        unclearDocMsg = findViewById(R.id.unclearDocMsg);
        verifyBtn = findViewById(R.id.verifyBtn);
        verifyAgainBtn = findViewById(R.id.verifyAgainBtn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipientHomepageActivity.this, RecipientSendVerification.class);
                startActivity(intent);
                finish();
            }
        });

        verifyAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipientHomepageActivity.this, RecipientSendVerification.class);
                startActivity(intent);
                finish();
            }
        });

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

                                verificationState = user.getVerificationState();
                                if (verificationState == 0) {
                                    verifyInProcessMsg.setVisibility(View.GONE);
                                    rejectMsg.setVisibility(View.GONE);
                                    unclearDocMsg.setVisibility(View.GONE);
                                } else if (verificationState == 1) {
                                    verifyMsg.setVisibility(View.GONE);
                                    rejectMsg.setVisibility(View.GONE);
                                    unclearDocMsg.setVisibility(View.GONE);
                                } else if (verificationState == 2) {
                                    verifyInProcessMsg.setVisibility(View.GONE);
                                    verifyMsg.setVisibility(View.GONE);
                                    rejectMsg.setVisibility(View.GONE);
                                    unclearDocMsg.setVisibility(View.GONE);
                                } else if (verificationState == 3) {
                                    verifyInProcessMsg.setVisibility(View.GONE);
                                    verifyMsg.setVisibility(View.GONE);
                                    unclearDocMsg.setVisibility(View.GONE);
                                } else if (verificationState == 4) {
                                    verifyInProcessMsg.setVisibility(View.GONE);
                                    verifyMsg.setVisibility(View.GONE);
                                    rejectMsg.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int curr = item.getItemId();
                        if (curr == R.id.claimFood) {
                            if (verificationState == 2) {
                                Intent intent = new Intent(RecipientHomepageActivity.this, RVDonors.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RecipientHomepageActivity.this, "You are not allowed access to this page", Toast.LENGTH_SHORT).show();
                                bottomNav.setSelectedItemId(R.id.home);
                            }
                        } else if (curr == R.id.schedule) {
                            if (verificationState == 2) {
                                Intent intent = new Intent(RecipientHomepageActivity.this, RecipientViewOrders.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RecipientHomepageActivity.this, "You are not allowed access to this page", Toast.LENGTH_SHORT).show();
                                bottomNav.setSelectedItemId(R.id.home);
                            }
                        } else if (curr == R.id.profile) {
                            Intent intent =
                                    new Intent(RecipientHomepageActivity.this, RecipientUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {

    }
}
