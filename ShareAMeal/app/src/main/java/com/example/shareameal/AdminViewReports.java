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
import java.util.Collections;
import java.util.Comparator;

public class AdminViewReports extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;

    private DatabaseReference reference;
    private ArrayList<Report> reports = new ArrayList<>();
    private AdminViewReportsAdapter adapter;

    private String donorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_reports);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Reported Donors");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        donorId = intent.getStringExtra("donorId");

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        adapter = new AdminViewReportsAdapter(this, reports);
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
                    Intent intent = new Intent(AdminViewReports.this, AdminHomepageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.verifications) {
                    Intent intent = new Intent(AdminViewReports.this, AdminViewVerifications.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.profile) {
                    Intent intent = new Intent(AdminViewReports.this, AdminUserPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
    }

    private void loadData() {
        reference.child(donorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren()) {
                    reports.add(data.getValue(Report.class));
                }
                Collections.sort(reports, new Comparator<Report>() {
                    @Override
                    public int compare(Report o1, Report o2) {
                        if(o1.getYear() < o2.getYear()) {
                            return -1;
                        } else if (o1.getYear() > o2.getYear()) {
                            return 1;
                        } else {
                            if(o1.getMonth() < o2.getMonth()) {
                                return -1;
                            } else if (o1.getMonth() > o2.getMonth()) {
                                return 1;
                            } else {
                                if(o1.getDay() < o2.getDay()) {
                                    return -1;
                                } else if(o1.getDay() > o2.getDay()) {
                                    return 1;
                                } else {
                                    if(o1.getHour() < o2.getHour()) {
                                        return -1;
                                    } else if (o1.getHour() > o2.getHour()) {
                                        return 1;
                                    } else {
                                        if (o1.getMinute() < o2.getMinute()) {
                                            return -1;
                                        } else if (o1.getMinute() > o2.getMinute()) {
                                            return 1;
                                        } else {
                                            return 0;
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

                recyclerView = findViewById(R.id.rv);
                recyclerView.setHasFixedSize(true);
                adapter = new AdminViewReportsAdapter(AdminViewReports.this, reports);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager manager = new LinearLayoutManager(AdminViewReports.this);
                recyclerView.setLayoutManager(manager);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminViewReports.this, AdminViewReportedDonors.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(AdminViewReports.this, AdminViewReportedDonors.class);
        startActivity(intent);
        finish();
        return true;
    }
}