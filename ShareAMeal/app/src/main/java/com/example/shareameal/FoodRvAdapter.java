package com.example.shareameal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodRvAdapter extends RecyclerView.Adapter<FoodRvAdapter.FoodsViewHolder>
    implements Filterable {
  private Context mContext;
  private List<Food> foodsList;
  private List<Food> foodsListFull;

  public FoodRvAdapter(Context context, List<Food> foods) {
    mContext = context;
    foodsList = foods;
    foodsListFull = new ArrayList<>(foodsList);
  }

  @NonNull
  @Override
  public FoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.food_note, parent, false);
    FoodsViewHolder foodsViewHolder = new FoodsViewHolder(itemView);
    return foodsViewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull FoodsViewHolder holder, int position) {
    final Food food = foodsList.get(position);
    holder.name.setText(food.getName());
    holder.description.setText(food.getDescription());
    holder.quantity.setText("Quantity: " + String.valueOf(food.getQuantity()));
    if (food.getImageUrl() == null) {
      holder.image.setImageResource(R.drawable.dish128);
    } else {
      if (food.getImageUrl().equals("null")) {
        holder.image.setImageResource(R.drawable.dish128);
      } else {
        Picasso.get().load(food.getImageUrl()).into(holder.image);
      }
    }
    holder.cardView.setOnClickListener(
        new View.OnClickListener() {
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
            filteredList.addAll(foodsListFull);
          } else {
            String filteredPattern = constraint.toString().toLowerCase().trim();

            for (Food food : foodsListFull) {
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
          foodsList.clear();
          foodsList.addAll((List) results.values);
          notifyDataSetChanged();
        }
      };
}
