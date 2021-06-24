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

public class RVDonatedFoodListings extends AppCompatActivity {
  private RecyclerView recyclerView;
  private BottomNavigationView bottomNav;
  private DatabaseReference reference;
  private ArrayList<Food> foods;
  private RVDonatedFoodListingsAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_rvfood_items);

    foods = new ArrayList<>();
    recyclerView = findViewById(R.id.rv);
    recyclerView.setHasFixedSize(true);
    adapter = new RVDonatedFoodListingsAdapter(this, foods);
    recyclerView.setAdapter(adapter);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(manager);

    bottomNav = findViewById(R.id.bottom_navigation);
    bottomNav.setSelectedItemId(R.id.claimFood);
    bottomNav.setOnNavigationItemSelectedListener(
        new BottomNavigationView.OnNavigationItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int curr = item.getItemId();
            if (curr == R.id.home) {
              Intent intent =
                  new Intent(RVDonatedFoodListings.this, RecipientHomepageActivity.class);
              startActivity(intent);
              finish();
            } else if (curr == R.id.schedule) {
              Intent intent = new Intent(RVDonatedFoodListings.this, RecipientViewOrders.class);
              startActivity(intent);
              finish();
            } else if (curr == R.id.profile) {
              Intent intent =
                  new Intent(RVDonatedFoodListings.this, RecipientUserPageActivity.class);
              startActivity(intent);
              finish();
            }
            return true;
          }
        });

    reference = FirebaseDatabase.getInstance().getReference("Foods");

    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
    getSupportActionBar().setTitle("Donated Foods");

    getFirebaseData(
        new FoodCallback() {
          @Override
          public void onCallback(Food food) {
            foods.add(food);
            adapter.notifyDataSetChanged();
          }
        });
  }

  private void getFirebaseData(final FoodCallback foodCallback) {
    reference.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot data : snapshot.getChildren()) {
              String userId = data.getKey();
              DatabaseReference nestedRef = reference.child(userId);
              nestedRef.addValueEventListener(
                  new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot2) {
                      for (DataSnapshot dataFoods : snapshot2.getChildren()) {
                        Food currFood = dataFoods.getValue(Food.class);

                        // So that foods with 0 qty won't be show to recipients
                        if (currFood.getQuantity() > 0) {
                          foodCallback.onCallback(currFood);
                        }
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                  });
            }

            recyclerView = findViewById(R.id.rv);
            recyclerView.setHasFixedSize(true);
            adapter = new RVDonatedFoodListingsAdapter(RVDonatedFoodListings.this, foods);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager manager = new LinearLayoutManager(RVDonatedFoodListings.this);
            recyclerView.setLayoutManager(manager);
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
    inflater.inflate(R.menu.example_menu_changelistings, menu);

    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) searchItem.getActionView();
    searchView.setMaxWidth(Integer.MAX_VALUE);
    searchView.setQueryHint("Search donated food here");

    searchView.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
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
    } else if (id == R.id.action_changeListings) {
      showChangeListingsDialog();
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
                      foods,
                      new Comparator<Food>() {
                        @Override
                        public int compare(Food o1, Food o2) {
                          return o2.getQuantity() - o1.getQuantity();
                        }
                      });
                  adapter.notifyDataSetChanged();
                } else if (which == 1) {
                  Collections.sort(
                      foods,
                      new Comparator<Food>() {
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

  private void showChangeListingsDialog() {
    String[] options = {"View by food donors", "View by donated foods"};

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder
        .setTitle("Choose Listing")
        .setIcon(R.drawable.ic_sort_visible)
        .setItems(
            options,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                  Intent intent = new Intent(RVDonatedFoodListings.this, RVDonors.class);
                  startActivity(intent);
                  finish();
                } else if (which == 1) {
                  Toast.makeText(
                          RVDonatedFoodListings.this,
                          "Already viewing donated foods",
                          Toast.LENGTH_SHORT)
                      .show();
                }
              }
            });
    builder.show();
  }
}
