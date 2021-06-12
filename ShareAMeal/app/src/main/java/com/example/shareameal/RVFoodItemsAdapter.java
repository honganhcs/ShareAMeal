package com.example.shareameal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class RVFoodItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<Food> list = new ArrayList<>();
    private OnFoodClickListener onFoodClickListener;

    public RVFoodItemsAdapter(Context ctx, OnFoodClickListener onFoodClickListener) {
        this.context = ctx;
        this.onFoodClickListener = onFoodClickListener;
    }

    public void setItems(ArrayList<Food> items) {
        list.addAll(items);
    }

    public class FoodVH extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView img_food;
        public TextView txt_food, txt_food_description, txt_food_quantity;
        public OnFoodClickListener onFoodClickListener;

        public FoodVH(@NonNull @NotNull View itemView, OnFoodClickListener onFoodClickListener) {
            super(itemView);
            img_food = itemView.findViewById(R.id.img_food);
            txt_food = itemView.findViewById(R.id.txt_food);
            txt_food_description = itemView.findViewById(R.id.txt_food_description);
            txt_food_quantity = itemView.findViewById(R.id.txt_food_quantity);
            this.onFoodClickListener = onFoodClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onFoodClickListener.onFoodClick(getBindingAdapterPosition());
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rvfood_item, parent, false);
        return new FoodVH(view, onFoodClickListener);
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnFoodClickListener {
        void onFoodClick(int position);
    }
}
