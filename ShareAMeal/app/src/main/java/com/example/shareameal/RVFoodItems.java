package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
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
import java.util.Collections;
import java.util.Comparator;

public class RVFoodItems extends AppCompatActivity {
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;

    private DatabaseReference reference;
    private ArrayList<Food> food = new ArrayList<>();
    private RVFoodItemsAdapter adapter;
    String donorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rvfood_items);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        adapter = new RVFoodItemsAdapter(this, food);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

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
        String donorName = intent.getStringExtra("donorName");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle(donorName);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadData(donorId);
    }

    private void loadData(String donorId) {
        reference = reference.child(donorId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    Food currFood = data.getValue(Food.class);

                    // So that foods with 0 qty won't be show to recipients
                    if (currFood.getQuantity() > 0) {
                        food.add(currFood);
                        adapter.notifyDataSetChanged();
                    }
                }

                recyclerView = findViewById(R.id.rv);
                recyclerView.setHasFixedSize(true);
                adapter = new RVFoodItemsAdapter(RVFoodItems.this, food);
                recyclerView.setAdapter(adapter);
                LinearLayoutManager manager = new LinearLayoutManager(RVFoodItems.this);
                recyclerView.setLayoutManager(manager);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RVFoodItems.this, RVDonors.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu_searchandfilter, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search food listing here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
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

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(RVFoodItems.this, RVDonors.class);
        startActivity(intent);
        finish();
        return true;
    }

    private void showSortDialog() {
        // Options to display in dialog
        String[] sortOptions = {"Descending Quantity", "Ascending Quantity"};

        // Create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by").setIcon(R.drawable.ic_filter).setItems(sortOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Collections.sort(food, new Comparator<Food>() {
                        @Override
                        public int compare(Food o1, Food o2) {
                            return o2.getQuantity() - o1.getQuantity();
                        }
                    });
                    adapter.notifyDataSetChanged();
                } else if (which == 1) {
                    Collections.sort(food, new Comparator<Food>() {
                        @Override
                        public int compare(Food o1, Food o2) {
                            return o1.getQuantity() - o2.getQuantity();
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }
        });
        builder.show();
    }
}