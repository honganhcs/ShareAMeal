package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class DonorHomepageActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private TextView userNameTxt, reminderQty;
    private int numberOfReports;
    private ConstraintLayout donorBlockedMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_homepage);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(DonorHomepageActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        userNameTxt = findViewById(R.id.userNameTxt);
        reminderQty = findViewById(R.id.reminderQty);
        donorBlockedMsg = findViewById(R.id.donorBlockedMsg);
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
                                numberOfReports = user.getNumberOfReports();

                                if (numberOfReports < 3) {
                                    donorBlockedMsg.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");
        foodRef
                .child(userid)
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                int totalQty = 0;
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    Food currFood = data.getValue(Food.class);
                                    totalQty += currFood.getQuantity();
                                }
                                reminderQty.setText(String.valueOf(totalQty));
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int curr = item.getItemId();
                        if (curr == R.id.food) {
                            if (numberOfReports < 3) {
                                Intent intent = new Intent(DonorHomepageActivity.this, DonateFoodActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(DonorHomepageActivity.this, "You are not allowed access to this page", Toast.LENGTH_SHORT).show();
                            }
                        } else if (curr == R.id.schedule) {
                            if (numberOfReports < 3) {
                                Intent intent = new Intent(DonorHomepageActivity.this, DonorsScheduleActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(DonorHomepageActivity.this, "You are not allowed access to this page", Toast.LENGTH_SHORT).show();
                            }
                        } else if (curr == R.id.profile) {
                            Intent intent = new Intent(DonorHomepageActivity.this, DonorUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {}
}
