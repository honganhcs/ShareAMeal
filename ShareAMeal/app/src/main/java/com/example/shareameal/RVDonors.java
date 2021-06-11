package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVDonors extends AppCompatActivity implements RVDonorsAdapter.OnDonorClickListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;

    private DatabaseReference reference;
    private ArrayList<User> users = new ArrayList<>();
    private RVDonorsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rvdonors);

        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RVDonorsAdapter(this, this);
        recyclerView.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        loadData();

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.claimFood);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int curr = item.getItemId();
                if (curr == R.id.home) {
                    Intent intent = new Intent(RVDonors.this, RecipientHomepageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.records) {
                    Toast.makeText(RVDonors.this, "Records not yet implemented", Toast.LENGTH_SHORT).show();
                } else if (curr == R.id.profile) {
                    Intent intent = new Intent(RVDonors.this, RecipientUserPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    User donor = data.getValue(User.class);
                    if(donor.getUserGroup().equals("donor")) {
                        users.add(donor);
                    }
                }
                adapter.setItems(users);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDonorClick(int position) {
        String donorId = users.get(position).getUserId();
        Intent intent = new Intent(RVDonors.this, RVFoodItems.class);
        intent.putExtra("donorId", donorId);
        startActivity(intent);
        finish();
    }
}