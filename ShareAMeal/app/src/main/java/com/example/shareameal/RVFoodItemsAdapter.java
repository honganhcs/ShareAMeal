package com.example.shareameal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RVFoodItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {
    private Context context;
    private ArrayList<Food> list = new ArrayList<>();
    private ArrayList<Food> listFull;

    public RVFoodItemsAdapter(Context ctx, ArrayList<Food> foodsList) {
        this.context = ctx;
        this.list = foodsList;
        listFull = new ArrayList<>(list);
    }

    public class FoodVH extends RecyclerView.ViewHolder {

        public ImageView img_food;
        public TextView txt_food, txt_food_description, txt_food_quantity;
        public CardView cardView;

        public FoodVH(@NonNull @NotNull View itemView) {
            super(itemView);
            img_food = itemView.findViewById(R.id.img_food);
            txt_food = itemView.findViewById(R.id.txt_food);
            txt_food_description = itemView.findViewById(R.id.txt_food_description);
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

        vh.txt_food.setText(food.getName());
        vh.txt_food_description.setText(food.getDescription());
        vh.txt_food_quantity.setText("Quantity: " + food.getQuantity());

        ((FoodVH) holder)
                .cardView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent oldIntent = ((Activity) v.getContext()).getIntent();
                        String donorId = oldIntent.getStringExtra("donorId");
                        String donorName = oldIntent.getStringExtra("donorName");
                        Intent intent = new Intent(v.getContext(), ReserveFoodItem.class);
                        intent.putExtra("donorId", donorId);
                        intent.putExtra("foodId", list.get(position).getFoodId());
                        intent.putExtra("donorName", donorName);
                        intent.putExtra("prevScreen", "someFoods");
                        v.getContext().startActivity(intent);
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
