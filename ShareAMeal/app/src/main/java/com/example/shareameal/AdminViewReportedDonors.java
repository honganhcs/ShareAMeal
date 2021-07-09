package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdminViewReportedDonors extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;

    private DatabaseReference reference;
    private ArrayList<String> donorIds = new ArrayList<>();
    private AdminViewReportedDonorsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_reported_donors);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Reported Donors");

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        adapter = new AdminViewReportedDonorsAdapter(this, donorIds);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        reference = FirebaseDatabase.getInstance().getReference("Reports");
        loadData();

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.reports);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int curr = item.getItemId();
                if (curr == R.id.home) {
                    Intent intent = new Intent(AdminViewReportedDonors.this, AdminHomepageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.verifications) {
                    Intent intent = new Intent(AdminViewReportedDonors.this, AdminViewVerifications.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.profile) {
                    Intent intent = new Intent(AdminViewReportedDonors.this, AdminUserPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }

    private void loadData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    donorIds.add(data.getKey());
                }

                recyclerView = findViewById(R.id.rv);
                recyclerView.setHasFixedSize(true);
                adapter = new AdminViewReportedDonorsAdapter(AdminViewReportedDonors.this, donorIds);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager manager = new LinearLayoutManager(AdminViewReportedDonors.this);
                recyclerView.setLayoutManager(manager);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }
}