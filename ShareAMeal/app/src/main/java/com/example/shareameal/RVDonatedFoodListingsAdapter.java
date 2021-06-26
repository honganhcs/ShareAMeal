package com.example.shareameal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class RVDonatedFoodListingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {
    private Context context;
    private ArrayList<Food> list = new ArrayList<>();
    private ArrayList<Food> listFull;
    private String donorId, clickedDonorId = "";

    public RVDonatedFoodListingsAdapter(Context ctx, ArrayList<Food> foodsList) {
        this.context = ctx;
        this.list = foodsList;
        listFull = new ArrayList<>(list);
    }

    public class FoodVH extends RecyclerView.ViewHolder {

        public ImageView img_food;
        public TextView txt_food, txt_food_donor, txt_food_quantity;
        public CardView cardView;

        public FoodVH(@NonNull @NotNull View itemView) {
            super(itemView);
            img_food = itemView.findViewById(R.id.img_food);
            txt_food = itemView.findViewById(R.id.txt_food);
            txt_food_donor = itemView.findViewById(R.id.txt_food_description);
            txt_food_quantity = itemView.findViewById(R.id.txt_food_quantity);
            cardView = itemView.findViewById(R.id.cvFood);
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rvfood_item, parent, false);
        return new FoodVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        FoodVH vh = (FoodVH) holder;
        Food food = list.get(position);

        if (food.getImageUrl() == null) {
            vh.img_food.setImageResource(R.drawable.dish128);
        } else {
            if (food.getImageUrl().equals("null")) {
                vh.img_food.setImageResource(R.drawable.dish128);
            } else {
                Picasso.get().load(food.getImageUrl()).into(vh.img_food);
            }
        }

        String foodId = food.getFoodId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Foods");
        reference.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String userId = data.getKey();
                            DatabaseReference nestedRef = reference.child(userId);
                            nestedRef.addValueEventListener(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot2) {
                                            for (DataSnapshot dataFoods : snapshot2.getChildren()) {
                                                if (dataFoods.getKey().equals(foodId)) {
                                                    donorId = userId;
                                                }
                                            }

                                            if (donorId != null) {
                                                DatabaseReference usersRef =
                                                        FirebaseDatabase.getInstance().getReference("Users");
                                                usersRef
                                                        .child(donorId)
                                                        .addValueEventListener(
                                                                new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                                        User user = snapshot.getValue(User.class);
                                                                        if (user.getRestaurant() == null
                                                                                || TextUtils.isEmpty(user.getRestaurant())) {
                                                                            vh.txt_food_donor.setText(user.getName());
                                                                        } else {
                                                                            vh.txt_food_donor.setText(user.getRestaurant());
                                                                        }
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
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });

        vh.txt_food.setText(food.getName());
        vh.txt_food_quantity.setText("Quantity: " + food.getQuantity());

        ((FoodVH) holder)
                .cardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String clickedFoodId = list.get(position).getFoodId();
                        reference.addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        for (DataSnapshot data : snapshot.getChildren()) {
                                            String userId = data.getKey();
                                            DatabaseReference nestedRef = reference.child(userId);
                                            nestedRef.addValueEventListener(
                                                    new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot2) {
                                                            for (DataSnapshot dataFoods : snapshot2.getChildren()) {
                                                                if (dataFoods.getKey().equals(clickedFoodId)) {
                                                                    clickedDonorId = userId;
                                                                }
                                                            }

                                                            Intent intent = new Intent(v.getContext(), ReserveFoodItem.class);
                                                            intent.putExtra("donorId", clickedDonorId);
                                                            intent.putExtra("foodId", list.get(position).getFoodId());
                                                            v.getContext().startActivity(intent);
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
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter =
            new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    if (listFull.size() == 0) {
                        listFull = new ArrayList<>(list);
                    }

                    List<Food> filteredList = new ArrayList<>();

                    if (constraint == null || constraint.length() == 0) {
                        filteredList.addAll(listFull);
                    } else {
                        String filteredPattern = constraint.toString().toLowerCase().trim();

                        for (Food food : listFull) {
                            if (food.getName().toLowerCase().contains(filteredPattern)) {
                                filteredList.add(food);
                            }
                        }
                    }

                    FilterResults results = new FilterResults();
                    results.values = filteredList;
                    results.count = filteredList.size();

                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    list.clear();
                    list.addAll((List) results.values);
                    notifyDataSetChanged();
                }
            };
}
