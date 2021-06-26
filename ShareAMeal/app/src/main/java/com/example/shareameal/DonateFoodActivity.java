package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("View food listings");

        // Initialising bottom navigation bar
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.food);
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int curr = item.getItemId();
                        if (curr == R.id.profile) {
                            Intent intent = new Intent(DonateFoodActivity.this, DonorUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (curr == R.id.schedule) {
                            Intent intent = new Intent(DonateFoodActivity.this, DonorsScheduleActivity.class);
                            startActivity(intent);
                            finish();
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
        fabAddFood.setOnClickListener(
                new View.OnClickListener() {
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

        getFirebaseData(
                new FoodCallback() {
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

        foodsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Result will be held here
                        for (DataSnapshot dataSnap : snapshot.getChildren()) {
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
                        rvFood = findViewById(R.id.recycleViewFood);
                        rvFood.setHasFixedSize(true);
                        foodRvAdapter = new FoodRvAdapter(DonateFoodActivity.this, foodList);
                        rvFood.setAdapter(foodRvAdapter);
                        LinearLayoutManager linearLayoutManager =
                                new LinearLayoutManager(DonateFoodActivity.this);
                        rvFood.setLayoutManager(linearLayoutManager);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle database error
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu_searchandfilter, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search food listing here");

        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        foodRvAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {
        // Options to display in dialog
        String[] sortOptions = {"Descending Quantity", "Ascending Quantity"};

        // Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Sort by")
                .setIcon(R.drawable.ic_filter)
                .setItems(
                        sortOptions,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Collections.sort(
                                            foodList,
                                            new Comparator<Food>() {
                                                @Override
                                                public int compare(Food o1, Food o2) {
                                                    return o2.getQuantity() - o1.getQuantity();
                                                }
                                            });
                                    foodRvAdapter.notifyDataSetChanged();
                                } else if (which == 1) {
                                    Collections.sort(
                                            foodList,
                                            new Comparator<Food>() {
                                                @Override
                                                public int compare(Food o1, Food o2) {
                                                    return o1.getQuantity() - o2.getQuantity();
                                                }
                                            });
                                    foodRvAdapter.notifyDataSetChanged();
                                }
                            }
                        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DonateFoodActivity.this, DonorHomepageActivity.class);
        startActivity(intent);
        finish();
    }
}
