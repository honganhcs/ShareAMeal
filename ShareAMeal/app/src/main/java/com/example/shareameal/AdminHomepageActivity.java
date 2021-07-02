package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class AdminHomepageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private TextView numberOfReportsTxt, numberOfUsersToBlockTxt, numberOfRecipientsToVerifyTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_homepage);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(AdminHomepageActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int curr = item.getItemId();
                if (curr == R.id.reports) {

                } else if (curr == R.id.verifications) {

                } else if (curr == R.id.profile) {
                    Intent intent = new Intent(AdminHomepageActivity.this, AdminUserPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        numberOfReportsTxt = findViewById(R.id.numberOfReports);
        numberOfUsersToBlockTxt = findViewById(R.id.numberOfUsersToBlock);
        numberOfRecipientsToVerifyTxt = findViewById(R.id.numberOfRecipientsToVerify);

        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("Reports");
        DatabaseReference reportedUsersRef = FirebaseDatabase.getInstance().getReference("ReportedUsers");
        DatabaseReference verificationsRef = FirebaseDatabase.getInstance().getReference("Verifications");
    }
}