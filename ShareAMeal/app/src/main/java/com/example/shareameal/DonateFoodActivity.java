package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DonateFoodActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private FloatingActionButton fabAddFood;
    private RecyclerView rvFood;
    private ArrayList<Food> foodList;
    private FoodRvAdapter foodRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_food);

        // Check if user is logged in
        // If user is not logged in, direct user to login page
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(DonateFoodActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Initialising bottom navigation bar
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.food);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int curr = item.getItemId();
                if (curr == R.id.pay) {
                    Toast.makeText(DonateFoodActivity.this, "Pay-it-forward system not yet implemented", Toast.LENGTH_SHORT).show();
                } else if (curr == R.id.profile) {
                    Intent intent = new Intent(DonateFoodActivity.this, DonorUserPageActivity.class);
                    startActivity(intent);
                    finish();
                } else if (curr == R.id.records) {
                    Toast.makeText(DonateFoodActivity.this, "Records not yet implemented", Toast.LENGTH_SHORT).show();
                } else if (curr == R.id.home) {
                    Intent intent = new Intent(DonateFoodActivity.this, DonorHomepageActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        // Initialising the floating action button
        fabAddFood = findViewById(R.id.fabAddFood);
        fabAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonateFoodActivity.this, AddFoodItemActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // CRUD Display
        foodList = new ArrayList<>();
        rvFood = findViewById(R.id.recycleViewFood);
        rvFood.setHasFixedSize(true);
        foodRvAdapter = new FoodRvAdapter(this, foodList);
        rvFood.setAdapter(foodRvAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFood.setLayoutManager(linearLayoutManager);

        getFirebaseData(new FoodCallback() {
            @Override
            public void onCallback(Food food) {
                foodList.add(food);
                foodRvAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getFirebaseData(final FoodCallback foodCallback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        // To make sure that the donor can see only his entries, and not others' entries
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference foodsRef = reference.child("Foods").child(userId);

        foodsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Result will be held here
                for (DataSnapshot dataSnap: snapshot.getChildren()) {
                    Food food = new Food();
                    String name = String.valueOf(dataSnap.child("name").getValue());
                    String description = String.valueOf(dataSnap.child("description").getValue());
                    String quantityString = String.valueOf(dataSnap.child("quantity").getValue());
                    String imageUrl = String.valueOf(dataSnap.child("imageUrl").getValue());
                    String foodId = String.valueOf(dataSnap.child("foodId").getValue());
                    int quantity = Integer.valueOf(quantityString);
                    food.setName(name);
                    food.setDescription(description);
                    food.setQuantity(quantity);
                    food.setImageUrl(imageUrl);
                    food.setFoodId(foodId);
                    foodCallback.onCallback(food);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }
}