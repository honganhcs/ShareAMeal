package com.example.shareameal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FoodRvAdapter extends RecyclerView.Adapter<FoodRvAdapter.FoodsViewHolder> {
    private Context mContext;
    private List<Food> foodsList;

    public FoodRvAdapter(Context context, List<Food> foods) {
        mContext = context;
        foodsList = foods;
    }

    @NonNull
    @Override
    public FoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_note, parent, false);
        FoodsViewHolder foodsViewHolder = new FoodsViewHolder(itemView);
        return foodsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodsViewHolder holder, int position) {
        final Food food = foodsList.get(position);
        holder.name.setText(food.getName());
        holder.description.setText(food.getDescription());
        holder.quantity.setText(String.valueOf(food.getQuantity()));
        if (food.getImageUrl() == null) {
            holder.image.setImageResource(R.drawable.dish);
        } else {
            if (food.getImageUrl().equals("null")) {
                holder.image.setImageResource(R.drawable.dish);
            } else {
                Picasso.get().load(food.getImageUrl()).into(holder.image);
            }
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DonorFoodItemPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("food", food);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodsList.size();
    }

    public class FoodsViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public TextView quantity;
        public ImageView image;
        public CardView cardView;

        public FoodsViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.foodName);
            description = view.findViewById(R.id.foodDescription);
            quantity = view.findViewById(R.id.foodQuantity);
            image = view.findViewById(R.id.foodImage);
            cardView = view.findViewById(R.id.cardViewNote);
        }
    }
}