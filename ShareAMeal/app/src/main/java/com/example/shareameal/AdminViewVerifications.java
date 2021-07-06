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

public class AdminViewVerifications extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;

    private DatabaseReference reference;
    private ArrayList<Verification> verifications = new ArrayList<>();
    private AdminViewVerificationsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_verifications);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Review Verification Requests");

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        adapter = new AdminViewVerificationsAdapter(this, verifications);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        reference = FirebaseDatabase.getInstance().getReference("Verifications");
        loadData();

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.verifications);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int curr = item.getItemId();
                if (curr == R.id.home) {
                    Intent intent = new Intent(AdminViewVerifications.this, AdminHomepageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.reports) {
                    Intent intent = new Intent(AdminViewVerifications.this, AdminViewReportedDonors.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.profile) {
                    Intent intent = new Intent(AdminViewVerifications.this, AdminUserPageActivity.class);
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
                    verifications.add(data.getValue(Verification.class));
                }
                Collections.sort(verifications, new Comparator<Verification>() {
                    @Override
                    public int compare(Verification o1, Verification o2) {
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
                adapter = new AdminViewVerificationsAdapter(AdminViewVerifications.this, verifications);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager manager = new LinearLayoutManager(AdminViewVerifications.this);
                recyclerView.setLayoutManager(manager);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminViewVerifications.this, AdminHomepageActivity.class);
        startActivity(intent);
        finish();
    }
}