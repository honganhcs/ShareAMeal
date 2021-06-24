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

import org.jetbrains.annotations.NotNull;

public class DonorHomepageActivity extends AppCompatActivity {

  private BottomNavigationView bottomNav;
  private TextView userNameTxt, reminderQty;

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

    bottomNav = findViewById(R.id.bottom_navigation);
    bottomNav.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int curr = item.getItemId();
            if (curr == R.id.food) {
              Intent intent = new Intent(DonorHomepageActivity.this, DonateFoodActivity.class);
              startActivity(intent);
              finish();
            } else if (curr == R.id.schedule) {
              Intent intent = new Intent(DonorHomepageActivity.this, DonorsScheduleActivity.class);
              startActivity(intent);
              finish();
            } else if (curr == R.id.profile) {
              Intent intent = new Intent(DonorHomepageActivity.this, DonorUserPageActivity.class);
              startActivity(intent);
              finish();
            }
            return true;
          }
        });

    userNameTxt = findViewById(R.id.userNameTxt);
    reminderQty = findViewById(R.id.reminderQty);
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
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {}
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
              public void onCancelled(@NonNull @NotNull DatabaseError error) {}
            });
  }
}
