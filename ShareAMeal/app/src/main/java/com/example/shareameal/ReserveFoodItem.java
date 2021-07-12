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

public class ReserveFoodItem extends AppCompatActivity
        implements RVSlotsAdapter.OnSlotClickListener {

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;

    private DatabaseReference reference, reference1;
    private ArrayList<Slot> slots = new ArrayList<>();
    private RVSlotsAdapter adapter;
    String donorId, foodId, donorName, prevScreen, recipientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_food_item);

        getWindow().setStatusBarColor(Color.parseColor("#F6DABA"));

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F6DABA")));
        getSupportActionBar().setTitle("Select a time slot");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RVSlotsAdapter(this, this);
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recipientId = user.getUid();

        Intent intent = getIntent();
        donorId = intent.getStringExtra("donorId");
        foodId = intent.getStringExtra("foodId");
        donorName = intent.getStringExtra("donorName");
        prevScreen = intent.getStringExtra("prevScreen");
        reference = FirebaseDatabase.getInstance().getReference("Slots").child("Pending").child(donorId);
        reference1 = FirebaseDatabase.getInstance().getReference("Orders").child("Pending").child(recipientId);

        // only load slots that aren't already reserved by the same recipient for the same food item
        // and the slots must also have fewer than 3 recipients
        loadData();

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.claimFood);
        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int curr = item.getItemId();
                        if (curr == R.id.home) {
                            Intent intent = new Intent(ReserveFoodItem.this, RecipientHomepageActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (curr == R.id.schedule) {
                            Intent intent = new Intent(ReserveFoodItem.this, RecipientViewOrders.class);
                            startActivity(intent);
                            finish();
                        } else if (curr == R.id.profile) {
                            Intent intent = new Intent(ReserveFoodItem.this, RecipientUserPageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
    }

    private void loadData() {
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<String> sameSlotIds = new ArrayList<>();
                for(DataSnapshot slotData : snapshot.getChildren()) {
                    String slotId = slotData.getKey();
                    for(DataSnapshot foodData : slotData.getChildren()) {
                        if(foodData.getKey().equals(foodId)) {
                            sameSlotIds.add(slotId);
                        }
                    }
                }
                if(sameSlotIds.isEmpty()) {
                    reference.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Slot slot = data.getValue(Slot.class);
                                        if (slot.getNumRecipients() < 3) {
                                            slots.add(slot);
                                        }
                                    }
                                    Collections.sort(slots, new Comparator<Slot>() {
                                        @Override
                                        public int compare(Slot o1, Slot o2) {
                                            if (o1.getYear() < o2.getYear()) {
                                                return -1;
                                            } else if (o1.getYear() > o2.getYear()) {
                                                return 1;
                                            } else {
                                                if (o1.getMonth() < o2.getMonth()) {
                                                    return -1;
                                                } else if (o1.getMonth() > o2.getMonth()) {
                                                    return 1;
                                                } else {
                                                    if (o1.getDayOfMonth() < o2.getDayOfMonth()) {
                                                        return -1;
                                                    } else if (o1.getDayOfMonth() > o2.getDayOfMonth()) {
                                                        return 1;
                                                    } else {
                                                        if (o1.getStartHour() < o2.getStartHour()) {
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
                                    adapter.setItems(slots);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                }
                            });
                } else {
                    reference.addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Slot slot = data.getValue(Slot.class);
                                        Boolean sameSlot = false;
                                        for(String slotId: sameSlotIds) {
                                            if(slot.getSlotId().equals(slotId)) {
                                                sameSlot = true;
                                            }
                                        }
                                        if ((!sameSlot) && slot.getNumRecipients() < 3) {
                                            slots.add(slot);
                                        }
                                    }
                                    Collections.sort(slots, new Comparator<Slot>() {
                                        @Override
                                        public int compare(Slot o1, Slot o2) {
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
                                    adapter.setItems(slots);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onSlotClick(int position) {
        Intent intent = new Intent(ReserveFoodItem.this, OrderConfirmation.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("slot", slots.get(position));
        bundle.putString("donorId", donorId);
        bundle.putString("foodId", foodId);
        bundle.putString("donorName", donorName);
        bundle.putString("prevScreen", prevScreen);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (prevScreen.equals("someFoods")) {
            Intent intent = new Intent(ReserveFoodItem.this, RVFoodItems.class);
            intent.putExtra("donorId", donorId);
            intent.putExtra("donorName", donorName);
            startActivity(intent);
            finish();
            return true;
        } else {
            Intent intent = new Intent(ReserveFoodItem.this, RVDonatedFoodListings.class);
            startActivity(intent);
            finish();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (prevScreen.equals("someFoods")) {
            Intent intent = new Intent(ReserveFoodItem.this, RVFoodItems.class);
            intent.putExtra("donorId", donorId);
            intent.putExtra("donorName", donorName);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(ReserveFoodItem.this, RVDonatedFoodListings.class);
            startActivity(intent);
            finish();
        }
    }
}
