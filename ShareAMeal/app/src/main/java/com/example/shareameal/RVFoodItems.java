package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RVFoodItems extends AppCompatActivity implements RVFoodItemsAdapter.OnFoodClickListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;

    private DatabaseReference reference;
    private ArrayList<Food> food = new ArrayList<>();
    private RVFoodItemsAdapter adapter;
    String donorId;

    private TextView foodDonorName;
    private AppCompatButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rvfood_items);

        swipeRefreshLayout = findViewById(R.id.swipe);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RVFoodItemsAdapter(this, this);
        recyclerView.setAdapter(adapter);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.claimFood);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int curr = item.getItemId();
                if (curr == R.id.home) {
                    Intent intent = new Intent(RVFoodItems.this, RecipientHomepageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.schedule) {
                    Intent intent = new Intent(RVFoodItems.this, RecipientViewOrders.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.profile) {
                    Intent intent = new Intent(RVFoodItems.this, RecipientUserPageActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Foods");
        Intent intent = getIntent();
        donorId = intent.getStringExtra("donorId");
        loadData(donorId);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Obtaining restaurant name / username of food donor
        foodDonorName = findViewById(R.id.foodDonorName);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(donorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String restaurantName = user.getRestaurant();
                if (TextUtils.isEmpty(restaurantName)) {
                    foodDonorName.setText(user.getName());
                } else {
                    foodDonorName.setText(restaurantName);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RVFoodItems.this, RVDonors.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void loadData(String donorId) {
        reference = reference.child(donorId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    Food currFood = data.getValue(Food.class);
                    food.add(currFood);
                }
                adapter.setItems(food);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onFoodClick(int position) {
        Intent intent = new Intent(RVFoodItems.this, ReserveFoodItem.class);
        intent.putExtra("donorId", donorId);
        intent.putExtra("foodId", food.get(position).getFoodId());
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RVFoodItems.this, RVDonors.class);
        startActivity(intent);
        finish();
    }
}