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

public class RecipientViewOrders extends AppCompatActivity
        implements RVOrdersAdapter.OnOrderClickListener {

    private BottomNavigationView bottomNav;
    private RecyclerView recyclerView;

    private DatabaseReference reference;
    private ArrayList<Order> orders = new ArrayList<>();
    private RVOrdersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_view_orders);

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("View current orders");

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RVOrdersAdapter(this, this);
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Orders").child(userId);

        loadData();

        Toast.makeText(RecipientViewOrders.this, "Orders are in chronological order.", Toast.LENGTH_SHORT).show();

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.schedule);
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int curr = item.getItemId();
                        if (curr == R.id.claimFood) {
                            Intent intent = new Intent(RecipientViewOrders.this, RVDonors.class);
                            startActivity(intent);
                            finish();
                        } else if (curr == R.id.home) {
                            Intent intent = new Intent(RecipientViewOrders.this, RecipientHomepageActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (curr == R.id.profile) {
                            Intent intent = new Intent(RecipientViewOrders.this, RecipientUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
    }

    private void loadData() {
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            orders.add(data.getValue(Order.class));
                        }
                        Collections.sort(orders, new Comparator<Order>() {
                            @Override
                            public int compare(Order o1, Order o2) {
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
                                        if(o1.getDayOfMonth() < o2.getDayOfMonth()) {
                                            return -1;
                                        } else if(o1.getDayOfMonth() > o2.getDayOfMonth()) {
                                            return 1;
                                        } else {
                                            if(o1.getStartHour() < o2.getStartHour()) {
                                                return -1;
                                            } else if (o1.getStartHour() > o2.getStartHour()) {
                                                return 1;
                                            } else {
                                                return 0;
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        adapter.setItems(orders);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onOrderClick(int position) {
        Intent intent = new Intent(RecipientViewOrders.this, ViewOrder.class);
        intent.putExtra("donorId", orders.get(position).getDonorId());
        intent.putExtra("slotId", orders.get(position).getSlotId());
        intent.putExtra("foodId", orders.get(position).getFoodId());
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RecipientViewOrders.this, RecipientHomepageActivity.class);
        startActivity(intent);
        finish();
    }
}
