package com.example.shareameal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ReserveFoodItem extends AppCompatActivity implements RVSlotsAdapter.OnSlotClickListener{

    private RecyclerView recyclerView;

    private DatabaseReference reference;
    private ArrayList<Slot> slots = new ArrayList<>();
    private RVSlotsAdapter adapter;
    String donorId, foodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_food_item);

        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new RVSlotsAdapter(this, this);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        donorId = intent.getStringExtra("donorId");
        foodId = intent.getStringExtra("foodId");
        reference = FirebaseDatabase.getInstance().getReference("Slots").child(donorId);

        loadData();

    }

    private void loadData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()) {
                    Slot slot = data.getValue(Slot.class);
                    if(slot.isAvailability()) {
                        slots.add(slot);
                    }
                }
                adapter.setItems(slots);
                adapter.notifyDataSetChanged();
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
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}