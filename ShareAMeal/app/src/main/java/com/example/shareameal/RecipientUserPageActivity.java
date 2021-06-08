package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class RecipientUserPageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private TextView editProfileTxt;
    private TextView logoutTxt;
    private TextView userNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_user_page);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(RecipientUserPageActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Setting name to the registered name of the account
        userNameTxt = findViewById(R.id.userNameTxt);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                userNameTxt.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Highlighting the right icon in the bottom navigation bar
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.profile);

        // Adding reactions to the different settings
        editProfileTxt = findViewById(R.id.editProfileTxt);
        logoutTxt = findViewById(R.id.logoutTxt);
        editProfileTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecipientUserPageActivity.this, "Editing profile not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
        logoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(RecipientUserPageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int curr = item.getItemId();
                if (curr == R.id.claimFood) {
                    Intent intent = new Intent(RecipientUserPageActivity.this, RVDonors.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.records) {
                    Toast.makeText(RecipientUserPageActivity.this, "Records not yet implemented", Toast.LENGTH_SHORT).show();
                } else if (curr == R.id.home) {
                    Intent intent = new Intent(RecipientUserPageActivity.this, RecipientHomepageActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }
}